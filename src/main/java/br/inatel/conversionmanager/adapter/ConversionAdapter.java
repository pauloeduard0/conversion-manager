package br.inatel.conversionmanager.adapter;

import br.inatel.conversionmanager.exception.CurrencyConversionException;
import br.inatel.conversionmanager.model.dto.ExchangeRateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ConversionAdapter {

    @Value("${api.conversion.host}")
    private final String currencyHost;
    @Value("${api.conversion.key}")
    private final String currencyKey;
    @Value("${api.conversion.base}")
    private final String currencyBaseUrl;
    private final WebClient webClient;

    public ConversionAdapter(
            @Value("${api.conversion.base}") String currencyBaseUrl,
            @Value("${api.conversion.key}") String currencyKey,
            @Value("${api.conversion.host}") String currencyHost
    ) {
        this.currencyHost = currencyHost;
        this.currencyKey = currencyKey;
        this.currencyBaseUrl = currencyBaseUrl;
        this.webClient = WebClient.builder()
                .baseUrl(currencyBaseUrl)
                .build();
    }

    @Cacheable("exchangeRates")
    public List<ExchangeRateResponse> getExchangeRates() {
        String date = String.valueOf(LocalDate.now());

        ExchangeRateResponse response = webClient.get()
                .uri(currencyBaseUrl + "/{date}", date)
                .header("X-RapidAPI-Key", currencyKey)
                .header("X-RapidAPI-Host", currencyHost)
                .retrieve()
                .bodyToMono(ExchangeRateResponse.class)
                .block();

        if (response != null) {
            List<ExchangeRateResponse> exchangeRates = new ArrayList<>();
            exchangeRates.add(response);
            return exchangeRates;
        } else {
            throw new CurrencyConversionException(this.currencyBaseUrl);
        }
    }

//    @CacheEvict(value = "exchangeRates", allEntries = true)
//    public void clearCurrencyCache() {
//        log.info("Cache cleared");
//    }
}
