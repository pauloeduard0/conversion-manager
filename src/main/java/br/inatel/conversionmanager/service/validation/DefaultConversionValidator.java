package br.inatel.conversionmanager.service.validation;

import br.inatel.conversionmanager.adapter.ConversionAdapter;
import br.inatel.conversionmanager.exception.CurrencyNotFoundException;
import br.inatel.conversionmanager.model.entities.Conversion;
import org.springframework.stereotype.Component;

@Component
public class DefaultConversionValidator implements DefaultValidator {

    private final ConversionAdapter conversionAdapter;

    public DefaultConversionValidator(ConversionAdapter conversionAdapter) {
        this.conversionAdapter = conversionAdapter;
    }

    @Override
    public void isValid(Conversion conversion) {
        String toCurrency = conversion.getCurrency();

        if (conversionAdapter.getExchangeRates().stream()
                .noneMatch(exchangeRate -> exchangeRate.rates().containsKey(toCurrency.substring(0, 3)))) {
            throw new CurrencyNotFoundException(toCurrency);
        }
    }
}
