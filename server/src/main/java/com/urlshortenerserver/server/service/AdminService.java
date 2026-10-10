package com.urlshortenerserver.server.service;


import com.urlshortenerserver.server.model.Url;
import com.urlshortenerserver.server.repository.UrlRepository;
import com.urlshortenerserver.server.repository.specifications.UrlSpecifications;
import com.urlshortenerserver.server.request.filter.UrlFilter;
import com.urlshortenerserver.server.response.AdminUrlResponse;
import com.urlshortenerserver.server.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AdminService implements IAdminService {
    private final UrlRepository urlRepository;
    private final CacheService cacheService;

    public AdminService(UrlRepository urlRepository, CacheService cacheService) {
        this.urlRepository = urlRepository;
        this.cacheService = cacheService;
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

        // Optionally put it back into cache so it's active immediately
        cacheService.cacheUrl(url.getCode(), url.getUrl());
    }
}
