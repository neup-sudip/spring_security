package com.example.security.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
@Slf4j
public class CacheConfig {

    @Bean("defaultCacheManager")
    @Primary
    public CacheManager cacheManager() {

        log.info("Cache is picked !");

        CaffeineCacheManager cacheManager = new CaffeineCacheManager("defaultCache");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(200)
                .maximumSize(100) // Maximum size of the cache
                .expireAfterWrite(1, TimeUnit.HOURS) // Expire entries after 1 hour
                .recordStats()
        );
        return cacheManager;
    }

}