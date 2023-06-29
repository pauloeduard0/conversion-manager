package br.inatel.conversionmanager.adapter;

import br.inatel.conversionmanager.model.entities.ExchangeRateResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class ConversionAdapter {

    private static final String API_BASE_URL = "https://currency-conversion-and-exchange-rates.p.rapidapi.com";
    private static final String API_KEY = "522cb5c963msh0361c7dba5a5298p1992a9jsn8f2bcd2540fd";
    private static final String API_HOST = "currency-conversion-and-exchange-rates.p.rapidapi.com";

    public ConversionAdapter(WebClient.Builder webClientBuilder) {

    }

    public List<ExchangeRateResponse> getExchangeRates() {
        String date = String.valueOf(LocalDate.now());
        WebClient.Builder builder = WebClient.builder();

        ExchangeRateResponse response = builder.build()
                .get()
                .uri(API_BASE_URL + "/latest?{date}", date)
                .header("X-RapidAPI-Key", API_KEY)
                .header("X-RapidAPI-Host", API_HOST)
                .retrieve()
                .bodyToMono(ExchangeRateResponse.class)
                .block();

        if (response != null) {
            List<ExchangeRateResponse> exchangeRates = new ArrayList<>();
            exchangeRates.add(response);
            return exchangeRates;
        }

        return Collections.emptyList();
    }
}
