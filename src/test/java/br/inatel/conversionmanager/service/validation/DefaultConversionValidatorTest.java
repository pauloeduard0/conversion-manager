package br.inatel.conversionmanager.service.validation;

import br.inatel.conversionmanager.adapter.ConversionAdapter;
import br.inatel.conversionmanager.exception.CurrencyNotFoundException;
import br.inatel.conversionmanager.model.dto.ExchangeRateResponse;
import br.inatel.conversionmanager.model.entities.Conversion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultConversionValidatorTest {

    @Mock
    private ConversionAdapter conversionAdapter;

    @InjectMocks
    private DefaultConversionValidator defaultValidator;

    @BeforeEach
    public void setUp() {
        defaultValidator = new DefaultConversionValidator(conversionAdapter);
    }

    @Test
    void givenInvalidCurrency_whenIsValidCalled_thenThrowCurrencyNotFoundException() {
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

        Conversion conversion = new Conversion();
        conversion.setTocurrency("INVALID");

        assertThrows(CurrencyNotFoundException.class, () -> defaultValidator.isValid(conversion));

        verify(conversionAdapter).getExchangeRates();
    }

    @Test
    void givenValidCurrency_whenIsValidCalled_thenNoExceptionThrown() {
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

        Conversion conversion = new Conversion();
        conversion.setTocurrency("USD");

        assertDoesNotThrow(() -> defaultValidator.isValid(conversion));

        verify(conversionAdapter).getExchangeRates();
    }

}