package br.inatel.conversionmanager.adapter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Slf4j
public class ConversionAdapter {

    @Value("${api.conversion.host}")
    private String currencyHost;
    @Value("${api.conversion.key}")
    private String currencyKey;

    @Bean
    public WebClient getCurrencyWebClient()
    {
        return WebClient.builder()
                .baseUrl(this.buildCurrencyBaseUrl())
                .build();
    }

    @Bean
    public String getCurrencyHost() {
        return currencyHost;
    }

    @Bean
    public String getCurrencyKey() {
        return currencyKey;
    }

    private String buildCurrencyBaseUrl() {
        return String.format("https://%s", currencyHost);
    }
}