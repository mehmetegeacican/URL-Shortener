package com.urlshortenerserver.server.service;


import com.urlshortenerserver.server.model.Url;
import com.urlshortenerserver.server.repository.UrlRepository;
import com.urlshortenerserver.server.response.AdminUrlResponse;
import com.urlshortenerserver.server.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;

@Service
public class AdminService implements IAdminService {
    private final UrlRepository urlRepository;

    public AdminService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }


    @Override
    @Transactional(readOnly = true)
    public PageResponse<AdminUrlResponse> listUrls(Pageable pageable) {
        Page<Url> page = urlRepository.findAll(pageable);
        return PageResponse.from(page.map(this::toResponse));
    }

    private AdminUrlResponse toResponse(Url url) {
        return AdminUrlResponse.builder()
                .code(url.getCode())
                .url(url.getUrl())
                .deleted(url.isDeleted())
                .build();
    }
}
