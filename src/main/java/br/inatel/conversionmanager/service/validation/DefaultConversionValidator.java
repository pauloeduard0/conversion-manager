package br.inatel.conversionmanager.service.validation;

import br.inatel.conversionmanager.adapter.ConversionAdapter;
import br.inatel.conversionmanager.exception.CurrencyNotFoundException;
import br.inatel.conversionmanager.model.dto.ExchangeRateResponse;
import org.springframework.stereotype.Component;

@Component
public class DefaultConversionValidator implements DefaultValidator {

    private final ConversionAdapter conversionAdapter;

    public DefaultConversionValidator(ConversionAdapter conversionAdapter) {
        this.conversionAdapter = conversionAdapter;
    }

    @Override
    public void isValid(ExchangeRateResponse exchangeRateResponse) {
        if (conversionAdapter.getExchangeRates().stream()
                .noneMatch(conver ->conver.rates().equals(exchangeRateResponse.rates()))) {
            throw new CurrencyNotFoundException(exchangeRateResponse);
        }
        }
}
