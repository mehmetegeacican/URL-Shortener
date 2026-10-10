package com.urlshortenerserver.server.service;

import com.urlshortenerserver.server.model.Url;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

import java.util.List;

public interface IUrlService {
    Url create(Url url);

    List<Url> getAllUrls();

    Url getUrlByCode(String code) throws Exception;

    @Transactional
    void recordClick(String code, HttpServletRequest request);

    void deleteUrl(String code) throws Exception;

    String generateCode();

    Long generateID();
}
