package com.urlshortenerserver.server.service;

public interface ICacheService {
    String getUrlFromCache(String code);

    void cacheUrl(String code, String url);

    void invalidateCache(String code);

    default String buildCacheKey(String code) {
        return CacheService.REDIRECT_CACHE_PREFIX + code.toUpperCase();
    }
}
