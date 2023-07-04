package br.inatel.conversionmanager.adapter;

import br.inatel.conversionmanager.exception.CurrencyConversionException;
import br.inatel.conversionmanager.model.dto.ExchangeRateResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class ConversionAdapter {

//    private static final String API_BASE_URL = "https://currency-conversion-and-exchange-rates.p.rapidapi.com";
//    private static final String API_KEY = "522cb5c963msh0361c7dba5a5298p1992a9jsn8f2bcd2540fd";
//    private static final String API_HOST = "currency-conversion-and-exchange-rates.p.rapidapi.com";

    @Value("${api.conversion.host}")
    private String currencyHost;

    @Value("${api.conversion.key}")
    private String currencyKey;

    private final WebClient webClient;

    public ConversionAdapter(WebClient webClient) {
        this.webClient = webClient;
    }

    @Cacheable("exchangeRates")
    public List<ExchangeRateResponse> getExchangeRates() {
        try {
            ExchangeRateResponse[] exchangeRateResponses = this.webClient.get()
                    .uri(currencyHost + "/latest")
                    .header("X-RapidAPI-Key", currencyKey)
                    .retrieve()
                    .bodyToMono(ExchangeRateResponse[].class)
                    .block();

            if (exchangeRateResponses != null) {
                return Arrays.asList(exchangeRateResponses);
            }
        } catch (WebClientException webClientException) {
            throw new CurrencyConversionException("Failed to fetch exchange rates.");
        }

        return Collections.emptyList();
    }

//    private final WebClient.Builder webClientBuilder;
//
//    public ConversionAdapter(WebClient.Builder webClientBuilder) {
//        this.webClientBuilder = webClientBuilder;
//    }

//    @Cacheable("exchangeRates")

//    public List<ExchangeRateResponse> getExchangeRates() {
//        String date = String.valueOf(LocalDate.now());
//
//        WebClient webClient = webClientBuilder.baseUrl(API_BASE_URL).build();
//
//        ExchangeRateResponse response = webClient.get()
//                .uri(API_BASE_URL + "/latest?{date}", date)
//                .header("X-RapidAPI-Key", API_KEY)
//                .header("X-RapidAPI-Host", API_HOST)
//                .retrieve()
//                .bodyToMono(ExchangeRateResponse.class)
//                .block();
//
//        if (response != null) {
//            List<ExchangeRateResponse> exchangeRates = new ArrayList<>();
//            exchangeRates.add(response);
//            return exchangeRates;
//        }
//
//        return Collections.emptyList();
//    }

}
