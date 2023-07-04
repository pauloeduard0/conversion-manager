package br.inatel.conversionmanager.exception;

import br.inatel.conversionmanager.model.dto.ExchangeRateResponse;

public class CurrencyNotFoundException extends RuntimeException{
    public CurrencyNotFoundException(ExchangeRateResponse exchangeRateResponse) {
        super(String.format("Currency with '%s' was not found.", exchangeRateResponse.rates()));
    }
}
