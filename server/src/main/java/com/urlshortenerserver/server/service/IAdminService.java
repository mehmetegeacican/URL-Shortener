package com.urlshortenerserver.server.service;

import com.urlshortenerserver.server.request.filter.UrlFilter;
import com.urlshortenerserver.server.response.AdminUrlResponse;
import com.urlshortenerserver.server.response.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface IAdminService {
    @Transactional(readOnly = true)
    PageResponse<AdminUrlResponse> listUrls(UrlFilter filter, Pageable pageable);
}
