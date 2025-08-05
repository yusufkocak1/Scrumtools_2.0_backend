package com.kocak.scrumtoolsbackend.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Memory-optimized cache configuration
 * Sadece gerekli cache'leri enable eder ve memory kullanımını sınırlar
 */
@Configuration
@EnableCaching
@Profile("!test")
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        // Sadece kritik cache'leri enable et
        cacheManager.setCacheNames("teams", "users", "poker-sessions");
        // Cache size'ını sınırla
        cacheManager.setAllowNullValues(false);
        return cacheManager;
    }
}
