package com.urlshortenerserver.server.service;

import com.urlshortenerserver.server.model.Url;

import java.util.List;

public interface IUrlService {
    Url create(Url url);

    List<Url> getAllUrls();

    Url getUrlByCode(String code) throws Exception;

    void deleteUrl(String code) throws Exception;

    String generateCode();

    Long generateID();
}
