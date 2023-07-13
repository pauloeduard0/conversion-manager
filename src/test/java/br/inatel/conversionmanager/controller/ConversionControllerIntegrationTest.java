package br.inatel.conversionmanager.controller;

import br.inatel.conversionmanager.adapter.ConversionAdapter;
import br.inatel.conversionmanager.model.dto.ConversionDto;
import br.inatel.conversionmanager.model.dto.ExchangeRateResponse;
import br.inatel.conversionmanager.model.entities.Conversion;
import br.inatel.conversionmanager.repository.ConversionRepository;
import br.inatel.conversionmanager.service.ConversionService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebMvcTest(ConversionController.class)
@ActiveProfiles("h2-test")
class ConversionControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ConversionAdapter conversionAdapter;

    @MockBean
    private ConversionService conversionService;

    @MockBean
    private ConversionRepository conversionRepository;


    private ConversionDto createConversionDto(Float amount, String to, LocalDate date, Float converted) {
        return ConversionDto.builder()
                .amount(amount)
                .to(to)
                .convertedAmount(converted)
                .date(date)
                .build();
    }

    private Conversion createConversion(Float amount, String tocurrency, LocalDate date, Float converted) {
        return Conversion.builder()
                .amount(amount)
                .base("EURO")
                .tocurrency(tocurrency)
                .date(date)
                .converted(converted)
                .build();

    }

    @Test
    void givenExchangeRatesExist_whenGetAllQuotes_thenReturnExchangeRates() {
        LocalDate currentDate = LocalDate.now();
        List<Conversion> conversionList = new ArrayList<>();
        conversionList.add(createConversion(500F, "USD", currentDate, 600F));
        conversionList.add(createConversion(800F, "GBP", currentDate, 900F));

        List<ConversionDto> conversionDtoList = conversionList.stream()
                .map(conversion -> ConversionDto.builder()
                        .amount(conversion.getAmount())
                        .to(conversion.getTocurrency())
                        .convertedAmount(conversion.getConverted())
                        .date(conversion.getDate())
                        .build())
                .collect(Collectors.toList());

        Page<ConversionDto> page = new PageImpl<>(conversionDtoList);

        when(conversionService.getAllConversions(any(Pageable.class))).thenReturn(page);

        webTestClient.get().uri("/api/exchange-rates")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content").isArray()
                .jsonPath("$.content[0].amount").isEqualTo(500F)
                .jsonPath("$.content[0].to").isEqualTo("USD")
                .jsonPath("$.content[0].convertedAmount").isEqualTo(600F)
                .jsonPath("$.content[1].amount").isEqualTo(800F)
                .jsonPath("$.content[1].to").isEqualTo("GBP")
                .jsonPath("$.content[1].convertedAmount").isEqualTo(900F);
    }

    @Test
    void givenValidCurrency_whenGetConversionsByCurrency_thenReturnConversions() {
        String toCurrency = "GBP";
        List<ConversionDto> expectedConversions = new ArrayList<>();
        expectedConversions.add(createConversionDto(800F, "GBP", LocalDate.now(), 685.334F));
        expectedConversions.add(createConversionDto(1000F, "GBP", LocalDate.now(), 854.417F));

        when(conversionService.getConversionsByCurrency(toCurrency)).thenReturn(expectedConversions);

        webTestClient.get().uri("/api/exchange-rates/{tocurrency}", toCurrency)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$[0].amount").isEqualTo(800F)
                .jsonPath("$[0].to").isEqualTo("GBP")
                .jsonPath("$[0].convertedAmount").isEqualTo(685.334F)
                .jsonPath("$[1].amount").isEqualTo(1000F)
                .jsonPath("$[1].to").isEqualTo("GBP")
                .jsonPath("$[1].convertedAmount").isEqualTo(854.417F);
    }

    @Test
    void givenValidConversionDto_whenSaveConversion_thenReturnCreatedStatusAndSavedConversion() {
        ConversionDto conversionDto = ConversionDto.builder()
                .amount(500F)
                .to("USD")
                .convertedAmount(600F)
                .build();

        ConversionDto savedConversion = ConversionDto.builder()
                .amount(500F)
                .to("USD")
                .convertedAmount(600F)
                .build();

        when(conversionService.saveConversion(any(ConversionDto.class))).thenReturn(savedConversion);

        webTestClient.post().uri("/api/exchange-rates")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(conversionDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ConversionDto.class)
                .isEqualTo(savedConversion);
    }

    @Test
    void givenExchangeRatesExist_whenGetExchangeRates_thenReturnExchangeRates() {
        ExchangeRateResponse exchangeRate = new ExchangeRateResponse(
                1687996799L,
                "EUR",
                true,
                Map.of(
                        "ANG", 1.968256f,
                        "SVC", 9.555569f,
                        "CAD", 1.44658f,
                        "XCD", 2.949964f,
                        "USD", 1.091548f
                ),
                "2023-06-28",
                true
        );

        List<ExchangeRateResponse> expectedExchangeRates = Collections.singletonList(exchangeRate);

        when(conversionAdapter.getExchangeRates()).thenReturn(expectedExchangeRates);

        webTestClient.get().uri("/api/exchange-rates/all")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ExchangeRateResponse.class)
                .isEqualTo(expectedExchangeRates);
    }


    @Test
    void givenNoExchangeRatesExist_whenGetExchangeRates_thenReturnNoContent() {

        List<ExchangeRateResponse> emptyExchangeRates = new ArrayList<>();

        when(conversionAdapter.getExchangeRates()).thenReturn(emptyExchangeRates);

        webTestClient.get().uri("/api/exchange-rates/all")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void givenConversionsExist_whenClearDatabase_thenDatabaseShouldBeEmpty() {
        Conversion conversion1 = new Conversion();
        conversion1.setAmount(500F);
        conversion1.setTocurrency("USD");
        conversion1.setDate(LocalDate.now());
        conversion1.setConverted(600F);

        Conversion conversion2 = new Conversion();
        conversion2.setAmount(800F);
        conversion2.setTocurrency("GBP");
        conversion2.setDate(LocalDate.now());
        conversion2.setConverted(900F);


        webTestClient.delete().uri("/api/exchange-rates/clear-database")
                .exchange()
                .expectStatus().isOk();

        List<Conversion> conversions = conversionRepository.findAll();
        assertTrue(conversions.isEmpty());
    }


}
