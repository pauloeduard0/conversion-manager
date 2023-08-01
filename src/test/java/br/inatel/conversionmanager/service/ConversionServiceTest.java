package br.inatel.conversionmanager.service;

import br.inatel.conversionmanager.adapter.ConversionAdapter;
import br.inatel.conversionmanager.exception.CurrencyNotFoundException;
import br.inatel.conversionmanager.model.dto.ConversionDto;
import br.inatel.conversionmanager.model.dto.ExchangeRateResponse;
import br.inatel.conversionmanager.model.entities.Conversion;
import br.inatel.conversionmanager.repository.ConversionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
class ConversionServiceTest {

    @Mock
    private ConversionRepository conversionRepository;
    @Mock
    private ConversionAdapter conversionAdapter;

    @InjectMocks
    private ConversionService conversionService;

    private ConversionDto createConversionDto(BigDecimal amount, String to, LocalDate date, BigDecimal converted) {
        return ConversionDto.builder()
                .baseCurrency("EURO")
                .amount(amount)
                .to(to)
                .convertedAmount(converted)
                .date(date)
                .build();
    }

    private Conversion createConversion(BigDecimal amount, String currency, LocalDate date, BigDecimal converted) {
        return Conversion.builder()
                .amount(amount)
                .base("EURO")
                .currency(currency)
                .date(date)
                .converted(converted)
                .build();

    }

    @Test
    void givenValidRequest_whenSaving_thenReturnSavedConversion() {
        ExchangeRateResponse exchangeRateResponse = new ExchangeRateResponse(
                1687996799L,
                "EUR",
                true,
                Map.of(
                        "ANG", new BigDecimal("1.968256"),
                        "SVC", new BigDecimal("9.555569"),
                        "CAD", new BigDecimal("1.44658"),
                        "XCD", new BigDecimal("2.949964"),
                        "USD", new BigDecimal("1.091548")
                ),
                "2023-06-28",
                true
        );
        when(conversionAdapter.getExchangeRates()).thenReturn(Collections.singletonList(exchangeRateResponse));

        ConversionDto conversionDto = createConversionDto(new BigDecimal(500), "USD", LocalDate.now(), new BigDecimal("545.774"));

        Conversion savedConversion = createConversion(new BigDecimal(500), "USD", LocalDate.now(), new BigDecimal("545.774"));

        when(conversionRepository.save(any(Conversion.class))).thenReturn(savedConversion);

        ConversionDto result = conversionService.saveConversion(conversionDto);

        assertEquals(savedConversion.getBase(), result.baseCurrency());
        assertEquals(savedConversion.getAmount(), result.amount());
        assertEquals(savedConversion.getCurrency(), result.to());
        assertEquals(savedConversion.getConverted(), result.convertedAmount());
        assertEquals(savedConversion.getDate(), result.date());

        verify(conversionAdapter).getExchangeRates();
    }

    @Test
    void givenInvalidCurrency_whenSaving_thenThrowCurrencyNotFoundException() {
        when(conversionAdapter.getExchangeRates()).thenReturn(Collections.emptyList());

        ConversionDto conversionDto = createConversionDto(new BigDecimal(500), "INVALID", LocalDate.now(), new BigDecimal(0));

        assertThrows(CurrencyNotFoundException.class, () -> conversionService.saveConversion(conversionDto));

        verify(conversionRepository, never()).save(any());
    }

    @Test
    void givenCurrency_whenGetAllConversions_thenReturnConversionDtoList() {
        List<Conversion> conversionList = new ArrayList<>();
        conversionList.add(createConversion(new BigDecimal(500), "USD", LocalDate.now(), new BigDecimal(600)));
        conversionList.add(createConversion(new BigDecimal(800), "GBP", LocalDate.now(), new BigDecimal(900)));

        when(conversionRepository.findAll()).thenReturn(conversionList);

        List<ConversionDto> dtoList = conversionService.getAllConversions();

        verify(conversionRepository).findAll();

        assertEquals(conversionList.size(), dtoList.size());

        for (Conversion conversion : conversionList) {
            ConversionDto dto = dtoList.stream()
                    .filter(dtoItem -> dtoItem.to().equals(conversion.getCurrency()))
                    .findFirst()
                    .orElseThrow(AssertionError::new);

            assertEquals(conversion.getBase(), dto.baseCurrency());
            assertEquals(conversion.getAmount(), dto.amount());
            assertEquals(conversion.getCurrency(), dto.to());
            assertEquals(conversion.getConverted(), dto.convertedAmount());
            assertEquals(conversion.getDate(), dto.date());
        }
    }


    @Test
    void givenCurrency_whenGetConversionsByCurrency_thenReturnConversionDtoList() {
        String toCurrency = "USD";

        List<Conversion> conversionList = new ArrayList<>();
        conversionList.add(createConversion(new BigDecimal(500), toCurrency, LocalDate.now(), new BigDecimal(600)));
        conversionList.add(createConversion(new BigDecimal(800), "GBP", LocalDate.now(), new BigDecimal(900)));

        when(conversionRepository.findByCurrency(toCurrency)).thenReturn(conversionList);

        List<ConversionDto> result = conversionService.getConversionsByCurrency(toCurrency);

        verify(conversionRepository).findByCurrency(toCurrency);

        assertEquals(1, result.size());
        assertEquals("USD", result.get(0).to());
        assertEquals(new BigDecimal("500"), result.get(0).amount());
        assertEquals(new BigDecimal("600"), result.get(0).convertedAmount());
        assertEquals(LocalDate.now(), result.get(0).date());
    }
}