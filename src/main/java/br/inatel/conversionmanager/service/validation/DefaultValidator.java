package br.inatel.conversionmanager.service.validation;

import br.inatel.conversionmanager.model.dto.ExchangeRateResponse;

public interface DefaultValidator {
    void isValid(ExchangeRateResponse exchangeRateResponse);
}
