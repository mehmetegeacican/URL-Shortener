package com.urlshortenerserver.server.service;


import com.urlshortenerserver.server.exception.CodeAlreadyExistsExceptiom;
import com.urlshortenerserver.server.exception.UrlNotFoundException;
import com.urlshortenerserver.server.model.Url;
import com.urlshortenerserver.server.repository.UrlRepository;
import com.urlshortenerserver.server.util.IdGenerator;
import com.urlshortenerserver.server.util.RandomStringGenerator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UrlService {

    private final UrlRepository urlRepository;
    private final RandomStringGenerator randomStringGenerator;
    private final IdGenerator idGenerator;

    private final CacheService cacheService;

    public UrlService(UrlRepository urlRepository, RandomStringGenerator randomStringGenerator, IdGenerator idGenerator, CacheService cacheService) {
        this.urlRepository = urlRepository;
        this.randomStringGenerator = randomStringGenerator;
        this.idGenerator = idGenerator;
        this.cacheService = cacheService;
    }

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


    public List<Url> getAllUrls() {
        return this.urlRepository.findAll();
    }

    public Url getUrlByCode(String code) throws Exception {
        String normalizedCode = code.toUpperCase();

        String cachedUrl = cacheService.getUrlFromCache(normalizedCode);
        if (cachedUrl != null) {
            Url cached = new Url();
            cached.setCode(normalizedCode);
            cached.setUrl(cachedUrl);
            return cached;
        }

        Url url = this.urlRepository.findAllByCodeAndDeletedFalse(normalizedCode)
                .orElseThrow(() -> new UrlNotFoundException("Url not found"));

        cacheService.cacheUrl(normalizedCode, url.getUrl());

        return url;
    }

    public void deleteUrl(String code) throws Exception {
        String normalizedCode = code.toUpperCase();

        Url url = this.urlRepository.findAllByCode(normalizedCode)
                .orElseThrow(() -> new UrlNotFoundException("Url not found"));

        url.setDeleted(true);
        this.urlRepository.save(url);

        cacheService.invalidateCache(normalizedCode);
    }

    public String generateCode(){
        String code = "";
        do {
            code = randomStringGenerator.generateRandomString();
        }while (urlRepository.findAllByCode(code).isPresent());
        return code;
    }

    public Long generateID(){
        Long repoSize = this.urlRepository.count();
        return (Long) repoSize + 1;
    }


}
