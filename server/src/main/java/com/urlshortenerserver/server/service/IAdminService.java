package com.urlshortenerserver.server.service;

import com.urlshortenerserver.server.request.filter.UrlFilter;
import com.urlshortenerserver.server.response.AdminStatResponse;
import com.urlshortenerserver.server.response.AdminUrlResponse;
import com.urlshortenerserver.server.response.PageResponse;
import com.urlshortenerserver.server.response.UrlClickStatResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

public interface IAdminService {
    @Transactional(readOnly = true)
    PageResponse<AdminUrlResponse> listUrls(UrlFilter filter, Pageable pageable);

    void restoreUrl(String code);

    @Transactional(readOnly = true)
    UrlClickStatResponse getUrlClicks(String code, Instant from, Instant to, Pageable pageable);

    @Transactional(readOnly = true)
    AdminStatResponse getSystemStats();
}
