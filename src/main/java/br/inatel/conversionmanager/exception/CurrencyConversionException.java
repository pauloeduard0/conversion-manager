package br.inatel.conversionmanager.exception;

public class CurrencyConversionException extends RuntimeException{

    public CurrencyConversionException(String currencyConversionBaseUrl) {
        super(String.format("It was not possible to communicate with API at location [%s]", currencyConversionBaseUrl));
    }
}
