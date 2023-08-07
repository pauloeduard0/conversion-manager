package br.inatel.conversionmanager.adapter;

import br.inatel.conversionmanager.exception.ExternalApiConnectionException;
import br.inatel.conversionmanager.model.dto.ExchangeRateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ConversionAdapter {

    private final WebClient webClient;
    @Value("${api.conversion.host}")
    private String currencyHost;
    @Value("${api.conversion.base}")
    private String currencyBaseUrl;
    @Value("${api.conversion.key}")
    private String currencyKey;

    public ConversionAdapter() {
        this.webClient = WebClient.builder()
                .build();
    }

    @Cacheable("exchangeRates")
    public List<ExchangeRateResponse> getExchangeRates() {
        String date = String.valueOf(LocalDate.now());

        try {
            ExchangeRateResponse response = webClient.get()
                    .uri(currencyBaseUrl + "/{date}", date)
                    .header("X-RapidAPI-Key", currencyKey)
                    .header("X-RapidAPI-Host", currencyHost)
                    .retrieve()
                    .bodyToMono(ExchangeRateResponse.class)
                    .block();

            List<ExchangeRateResponse> exchangeRates = new ArrayList<>();
            exchangeRates.add(response);
            return exchangeRates;
        } catch (WebClientRequestException e) {
            log.error(e.getMessage());
            throw new ExternalApiConnectionException(this.currencyBaseUrl);
        }
    }
}