package com.urlshortenerserver.server.service;


import com.urlshortenerserver.server.model.Click;
import com.urlshortenerserver.server.model.Url;
import com.urlshortenerserver.server.repository.ClickRepository;
import com.urlshortenerserver.server.repository.UrlRepository;
import com.urlshortenerserver.server.repository.specifications.ClickSpecifications;
import com.urlshortenerserver.server.repository.specifications.UrlSpecifications;
import com.urlshortenerserver.server.request.filter.UrlFilter;
import com.urlshortenerserver.server.response.AdminUrlResponse;
import com.urlshortenerserver.server.response.PageResponse;
import com.urlshortenerserver.server.response.UrlClickStatResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Service
public class AdminService implements IAdminService {
    private final UrlRepository urlRepository;
    private final ClickRepository clickRepository;
    private final CacheService cacheService;



    public AdminService(UrlRepository urlRepository, CacheService cacheService, ClickRepository clickRepository) {
        this.urlRepository = urlRepository;
        this.cacheService = cacheService;
        this.clickRepository = clickRepository;
    }


    @Override
    @Transactional(readOnly = true)
    public PageResponse<AdminUrlResponse> listUrls(UrlFilter filter, Pageable pageable) {
        Specification<Url> spec = UrlSpecifications.withFilters(filter);
        Page<Url> page = urlRepository.findAll(spec,pageable);
        return PageResponse.from(page.map(this::toResponse));
    }

    private AdminUrlResponse toResponse(Url url) {
        return AdminUrlResponse.builder()
                .code(url.getCode())
                .url(url.getUrl())
                .deleted(url.isDeleted())
                .build();
    }

    @Override
    @Transactional
    public void restoreUrl(String code){
        Url url = urlRepository.findByCode(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "URL not found with code: " + code));

        url.setDeleted(false);
        urlRepository.save(url);

        // Optionally put it back into cache, it's active immediately
        cacheService.cacheUrl(url.getCode(), url.getUrl());
    }

    @Override
    @Transactional(readOnly = true)
    public UrlClickStatResponse getUrlClicks(String code, Instant from, Instant to, Pageable pageable) {
        if (!urlRepository.existsByCode(code)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "URL not found with code: " + code);
        }
        // 1. Build the specification for filtering
        Specification<Click> spec = ClickSpecifications.withFilters(code, from, to);
        // 2. Fetch paginated logs cleanly using Spring Data Specifications (No custom JPQL!)
        Page<Click> clickPage = clickRepository.findAll(spec, pageable);
        // 3. Stat Response
        Page<UrlClickStatResponse.ClickLog> logDtoPage = clickPage.map(c -> UrlClickStatResponse.ClickLog.builder()
                .ip(c.getIp())
                .userAgent(c.getUserAgent())
                .referer(c.getReferer())
                .clickedAt(c.getClickedAt())
                .build());

        long totalClicks = clickRepository.count(spec);

        return UrlClickStatResponse.builder()
                .code(code)
                .totalClicks(totalClicks)
                // populate other stats...
                .clicks(PageResponse.from(logDtoPage))
                .build();
    }
}
