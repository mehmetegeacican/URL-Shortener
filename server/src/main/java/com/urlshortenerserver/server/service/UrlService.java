package com.urlshortenerserver.server.service;


import com.urlshortenerserver.server.exception.CodeAlreadyExistsExceptiom;
import com.urlshortenerserver.server.exception.UrlNotFoundException;
import com.urlshortenerserver.server.model.Click;
import com.urlshortenerserver.server.model.Url;
import com.urlshortenerserver.server.repository.ClickRepository;
import com.urlshortenerserver.server.repository.UrlRepository;
import com.urlshortenerserver.server.util.IdGenerator;
import com.urlshortenerserver.server.util.RandomStringGenerator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class UrlService implements IUrlService {

    private final UrlRepository urlRepository;

    private final RandomStringGenerator randomStringGenerator;
    private final IdGenerator idGenerator;

    private final CacheService cacheService;

    private final ClickRepository clickRepository;

    private static final Logger logger = LoggerFactory.getLogger(UrlService.class);

    public UrlService(UrlRepository urlRepository, RandomStringGenerator randomStringGenerator, IdGenerator idGenerator, CacheService cacheService, ClickRepository clickRepository) {
        this.urlRepository = urlRepository;
        this.randomStringGenerator = randomStringGenerator;
        this.idGenerator = idGenerator;
        this.cacheService = cacheService;
        this.clickRepository = clickRepository;
    }

    @Override
    public Url create(Url url) {
        boolean isCustomCode = url.getCode() != null && !url.getCode().isEmpty();
        if (isCustomCode) {
            String code = url.getCode().toUpperCase();
            if (urlRepository.existsByCode(code)) {
                throw new CodeAlreadyExistsExceptiom(code);
            }
            url.setCode(code);
        }
        else {
            String generated;
            do {
                generated = generateCode().toUpperCase();
            } while (urlRepository.existsByCode(generated));
            url.setCode(generated);
        }
        Url saved = this.urlRepository.save(url);
        cacheService.cacheUrl(saved.getCode(), saved.getUrl());
        return saved;
    }


    @Override
    public List<Url> getAllUrls() {
        return this.urlRepository.findAllByDeletedFalse();
    }

    @Override
    public Url getUrlByCode(String code) throws Exception {
        String normalizedCode = code.toUpperCase();
        logger.debug("Attempting to fetch URL with code: {}", normalizedCode);

        String cachedUrl = cacheService.getUrlFromCache(normalizedCode);
        if (cachedUrl != null) {
            Url cached = new Url();
            cached.setCode(normalizedCode);
            cached.setUrl(cachedUrl);
            return cached;
        }

        logger.debug("Cache miss for code: {}, fetching from database", normalizedCode);
        Url url = this.urlRepository.findAllByCodeAndDeletedFalse(normalizedCode)
                .orElseThrow(() -> new UrlNotFoundException("Url not found"));

        cacheService.cacheUrl(normalizedCode, url.getUrl());

        return url;
    }


    @Override
    @Transactional
    public void recordClick(String code, HttpServletRequest request) {
        String normalizedCode = code.toUpperCase();

        String ip = extractClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        String referer = request.getHeader("Referer");

        Click click = Click.builder()
                .code(normalizedCode)
                .ip(ip)
                .userAgent(userAgent)
                .referer(referer)
                .clickedAt(Instant.now())
                .build();

        clickRepository.save(click);
    }

    private String extractClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    @Override
    public void deleteUrl(String code) throws Exception {
        String normalizedCode = code.toUpperCase();

        Url url = this.urlRepository.findAllByCode(normalizedCode)
                .orElseThrow(() -> new UrlNotFoundException("Url not found"));

        url.setDeleted(true);
        this.urlRepository.save(url);

        cacheService.invalidateCache(normalizedCode);
    }

    @Override
    public String generateCode(){
        String code = "";
        do {
            code = randomStringGenerator.generateRandomString();
        }while (urlRepository.findAllByCode(code).isPresent());
        return code;
    }

    @Override
    public Long generateID(){
        Long repoSize = this.urlRepository.count();
        return (Long) repoSize + 1;
    }


}
