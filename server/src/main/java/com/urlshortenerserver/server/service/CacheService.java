package com.urlshortenerserver.server.service;


import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class CacheService {
    private static final String REDIRECT_CACHE_PREFIX = "redirect:";
    private static final Duration CACHE_TTL = Duration.ofMinutes(30);

    private final RedisTemplate<String, String> redisTemplate;

    public CacheService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String getUrlFromCache(String code) {
        return redisTemplate.opsForValue().get(buildCacheKey(code));
    }

    public void cacheUrl(String code, String url) {
        redisTemplate.opsForValue().set(buildCacheKey(code), url, CACHE_TTL);
    }

    public void invalidateCache(String code) {
        redisTemplate.delete(buildCacheKey(code));
    }

    private String buildCacheKey(String code) {
        return REDIRECT_CACHE_PREFIX + code.toUpperCase();
    }
}
