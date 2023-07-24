package br.inatel.conversionmanager.adapter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
@EnableScheduling
public class CacheManagerService {

    @Autowired
    CacheManager cacheManager;

    public void evictAllCaches() {
        cacheManager.getCacheNames()
                .forEach(cacheName -> Objects.requireNonNull(cacheManager.getCache(cacheName)).clear());
    }

    @Scheduled(fixedRate = 60000)
    public void evictAllCachesAtIntervals() {
        evictAllCaches();
        log.info("Cache cleared");
    }

}