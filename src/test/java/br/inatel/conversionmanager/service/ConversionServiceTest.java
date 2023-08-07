package br.inatel.conversionmanager.service;

import br.inatel.conversionmanager.adapter.ConversionAdapter;
import br.inatel.conversionmanager.exception.ConversionNotFoundException;
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
import java.util.*;

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

    private ConversionDto createConversionDto(UUID id, BigDecimal amount, String to, LocalDate date, BigDecimal converted) {
        return ConversionDto.builder()
                .id(id)
                .baseCurrency("EURO")
                .amount(amount)
                .to(to)
                .convertedAmount(converted)
                .date(date)
                .build();
    }

    private Conversion createConversion(UUID id, BigDecimal amount, String currency, LocalDate date, BigDecimal converted) {
        return Conversion.builder()
                .id(id)
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

        UUID id = UUID.fromString("9b9a5998-52c5-4b60-8d3e-470191491056");

        ConversionDto conversionDto = createConversionDto(id, new BigDecimal(500), "USD", LocalDate.now(), new BigDecimal("545.774"));

        Conversion savedConversion = createConversion(id, new BigDecimal(500), "USD", LocalDate.now(), new BigDecimal("545.774"));

        when(conversionRepository.save(any(Conversion.class))).thenReturn(savedConversion);

        ConversionDto result = conversionService.saveConversion(conversionDto);

        assertEquals(savedConversion.getId(), result.id());
        assertEquals(savedConversion.getBase(), result.baseCurrency());
        assertEquals(savedConversion.getAmount(), result.amount());
        assertEquals(savedConversion.getCurrency(), result.to());
        assertEquals(savedConversion.getConverted(), result.convertedAmount());
        assertEquals(savedConversion.getDate(), result.date());

        verify(conversionAdapter).getExchangeRates();
    }

    @Test
    void givenInvalidCurrency_whenSaving_thenThrowCurrencyNotFoundException() {
        UUID id = UUID.fromString("9b9a5998-52c5-4b60-8d3e-470191491056");

        when(conversionAdapter.getExchangeRates()).thenReturn(Collections.emptyList());

        ConversionDto conversionDto = createConversionDto(id, new BigDecimal(500), "INVALID", LocalDate.now(), new BigDecimal(0));

        assertThrows(CurrencyNotFoundException.class, () -> conversionService.saveConversion(conversionDto));

        verify(conversionRepository, never()).save(any());
    }

    @Test
    void givenCurrency_whenGetAllConversions_thenReturnConversionDtoList() {
        UUID id = UUID.fromString("9b9a5998-52c5-4b60-8d3e-470191491056");
        UUID id2 = UUID.fromString("5f65ce24-301f-4b7e-a32b-b70c3d4eccae");

        List<Conversion> conversionList = new ArrayList<>();
        conversionList.add(createConversion(id, new BigDecimal(500), "USD", LocalDate.now(), new BigDecimal(600)));
        conversionList.add(createConversion(id2, new BigDecimal(800), "GBP", LocalDate.now(), new BigDecimal(900)));

        when(conversionRepository.findAll()).thenReturn(conversionList);

        List<ConversionDto> dtoList = conversionService.getAllConversions();

        verify(conversionRepository).findAll();

        assertEquals(conversionList.size(), dtoList.size());

        for (Conversion conversion : conversionList) {
            ConversionDto dto = dtoList.stream()
                    .filter(dtoItem -> dtoItem.to().equals(conversion.getCurrency()))
                    .findFirst()
                    .orElseThrow(AssertionError::new);

            assertEquals(conversion.getId(), dto.id());
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
        UUID id = UUID.fromString("9b9a5998-52c5-4b60-8d3e-470191491056");
        UUID id2 = UUID.fromString("5f65ce24-301f-4b7e-a32b-b70c3d4eccae");

        List<Conversion> conversionList = new ArrayList<>();
        conversionList.add(createConversion(id, new BigDecimal(500), toCurrency, LocalDate.now(), new BigDecimal(600)));
        conversionList.add(createConversion(id2, new BigDecimal(800), "GBP", LocalDate.now(), new BigDecimal(900)));

        when(conversionRepository.findByCurrency(toCurrency)).thenReturn(conversionList);

        List<ConversionDto> result = conversionService.getConversionsByCurrency(toCurrency);

        verify(conversionRepository).findByCurrency(toCurrency);

        assertEquals(1, result.size());
        assertEquals("USD", result.get(0).to());
        assertEquals(id, result.get(0).id());
        assertEquals(new BigDecimal("500"), result.get(0).amount());
        assertEquals(new BigDecimal("600"), result.get(0).convertedAmount());
        assertEquals(LocalDate.now(), result.get(0).date());
    }

    @Test
    void givenId_whenGetConversionsById_thenReturnConversionDto() {
        UUID id = UUID.fromString("9b9a5998-52c5-4b60-8d3e-470191491056");

        Conversion conversion = createConversion(id, new BigDecimal(500), "USD", LocalDate.now(), new BigDecimal(600));

        when(conversionRepository.findById(id)).thenReturn(Optional.of(conversion));

        ConversionDto result = conversionService.getConversionById(id);

        verify(conversionRepository).findById(id);

        assertEquals(id, result.id());
        assertEquals("USD", result.to());
        assertEquals(new BigDecimal("500"), result.amount());
        assertEquals(new BigDecimal("600"), result.convertedAmount());
        assertEquals(LocalDate.now(), result.date());
    }

    @Test
    void givenInvalidId_whenGetConversionsById_thenThrowConversionNotFoundException() {
        UUID id = UUID.fromString("9b9a5998-52c5-4b60-8d3e-470191491056");

        when(conversionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ConversionNotFoundException.class, () -> {
            conversionService.getConversionById(id);
        });

        verify(conversionRepository).findById(id);
    }
}