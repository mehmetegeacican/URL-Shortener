package com.urlshortenerserver.server.service;


import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CacheService implements ICacheService {
    public static final String REDIRECT_CACHE_PREFIX = "redirect:";

    private static final Duration CACHE_TTL = Duration.ofMinutes(30);
    private static final Logger logger = LoggerFactory.getLogger(CacheService.class);

    private final RedisTemplate<String, String> redisTemplate;

    public CacheService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String getUrlFromCache(String code) {
        try {
            return redisTemplate.opsForValue().get(buildCacheKey(code));
        } catch (Exception e) {
            logger.warn("Redis read failed, falling back to database: code={}, error={}", code, e.getMessage());
            return null; // treated as a cache miss by UrlService
        }
    }

    @Override
    public void cacheUrl(String code, String url) {
        try {
            redisTemplate.opsForValue().set(buildCacheKey(code), url, CACHE_TTL);
        } catch (Exception e) {
            logger.warn("Redis write failed, skipping cache: code={}, error={}", code, e.getMessage());
        }
    }

    @Override
    public void invalidateCache(String code) {
        try {
            redisTemplate.delete(buildCacheKey(code));
        } catch (Exception e) {
            logger.warn("Redis delete failed, entry may stay stale until TTL expires: code={}, error={}", code, e.getMessage());
        }
    }

     public String buildCacheKey(String code) {
        return REDIRECT_CACHE_PREFIX + code;
    }

}
