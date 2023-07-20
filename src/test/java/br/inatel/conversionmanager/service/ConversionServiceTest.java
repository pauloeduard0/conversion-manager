package br.inatel.conversionmanager.service;

import br.inatel.conversionmanager.adapter.ConversionAdapter;
import br.inatel.conversionmanager.exception.CurrencyNotFoundException;
import br.inatel.conversionmanager.model.dto.ConversionDto;
import br.inatel.conversionmanager.model.dto.ExchangeRateResponse;
import br.inatel.conversionmanager.model.entities.Conversion;
import br.inatel.conversionmanager.repository.ConversionRepository;
import br.inatel.conversionmanager.service.validation.DefaultValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

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
    List<DefaultValidator> defaultValidatorList;
    @Mock
    private ConversionRepository conversionRepository;
    @Mock
    private ConversionAdapter conversionAdapter;

    @InjectMocks
    private ConversionService conversionService;

    private ConversionDto createConversionDto(Float amount, String to, LocalDate date, Float converted) {
        return ConversionDto.builder()
                .baseCurrency("EURO")
                .amount(amount)
                .to(to)
                .convertedAmount(converted)
                .date(date)
                .build();
    }

    private Conversion createConversion(Float amount, String currency, LocalDate date, Float converted) {
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
                        "ANG", 1.968256f,
                        "SVC", 9.555569f,
                        "CAD", 1.44658f,
                        "XCD", 2.949964f,
                        "USD", 1.091548f
                ),
                "2023-06-28",
                true
        );
        when(conversionAdapter.getExchangeRates()).thenReturn(Collections.singletonList(exchangeRateResponse));

        ConversionDto conversionDto = createConversionDto(500F, "USD", LocalDate.now(), 545.774f);

        Conversion savedConversion = createConversion(500F, "USD", LocalDate.now(), 545.774f);

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

        ConversionDto conversionDto = createConversionDto(500F, "INVALID", LocalDate.now(), 0F);

        assertThrows(CurrencyNotFoundException.class, () -> conversionService.saveConversion(conversionDto));

        verify(conversionRepository, never()).save(any());
    }

    @Test
    void givenCurrency_whenGetAllConversions_thenReturnConversionDtoList() {
        List<Conversion> conversionList = new ArrayList<>();
        conversionList.add(createConversion(500F, "USD", LocalDate.now(), 600F));
        conversionList.add(createConversion(800F, "GBP", LocalDate.now(), 900F));

        when(conversionRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(conversionList));

        Page<ConversionDto> result = conversionService.getAllConversions(Pageable.unpaged());

        verify(conversionRepository).findAll(any(Pageable.class));

        assertEquals(conversionList.size(), result.getContent().size());

        List<ConversionDto> dtoList = result.getContent();

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
        conversionList.add(createConversion(500F, toCurrency, LocalDate.now(), 600F));
        conversionList.add(createConversion(800F, "GBP", LocalDate.now(), 900F));

        when(conversionRepository.findByCurrency(toCurrency)).thenReturn(conversionList);

        List<ConversionDto> result = conversionService.getConversionsByCurrency(toCurrency);

        verify(conversionRepository).findByCurrency(toCurrency);

        assertEquals(1, result.size());
        assertEquals("USD", result.get(0).to());
        assertEquals(500F, result.get(0).amount());
        assertEquals(600F, result.get(0).convertedAmount());
        assertEquals(LocalDate.now(), result.get(0).date());
    }

}