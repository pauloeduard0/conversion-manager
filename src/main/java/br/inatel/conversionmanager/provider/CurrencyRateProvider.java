package br.inatel.conversionmanager.provider;

import br.inatel.conversionmanager.adapter.ConversionAdapter;
import br.inatel.conversionmanager.exception.ExternalApiConnectionException;
import br.inatel.conversionmanager.model.dto.ExchangeRateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class CurrencyRateProvider {

    CacheManager cacheManager;

    ConversionAdapter conversionAdapter;

    public CurrencyRateProvider(@Autowired ConversionAdapter conversionAdapter, CacheManager cacheManager) {
        this.conversionAdapter = conversionAdapter;
        this.cacheManager = cacheManager;
    }

    @Cacheable("exchangeRates")
    public List<ExchangeRateResponse> getExchangeRates() {
        String date = String.valueOf(LocalDate.now());

        try {
            ExchangeRateResponse response = conversionAdapter.getCurrencyWebClient().get()
                    .uri("/{date}", date)
                    .header("X-RapidAPI-Key", conversionAdapter.getCurrencyKey())
                    .header("X-RapidAPI-Host", conversionAdapter.getCurrencyHost())
                    .retrieve()
                    .bodyToMono(ExchangeRateResponse.class)
                    .block();

            List<ExchangeRateResponse> exchangeRates = new ArrayList<>();
            exchangeRates.add(response);
            return exchangeRates;
        } catch (WebClientRequestException e) {
            log.error(e.getMessage());
            throw new ExternalApiConnectionException(conversionAdapter.getCurrencyHost());
        }
    }

    public void evictAllCaches() {
        cacheManager.getCacheNames()
                .forEach(cacheName -> Objects.requireNonNull(cacheManager.getCache(cacheName)).clear());
    }

    @Scheduled(fixedRate = 30, timeUnit = TimeUnit.SECONDS)
    public void evictAllCachesAtIntervals() {
        evictAllCaches();
        log.info("Cache cleared");
    }
}