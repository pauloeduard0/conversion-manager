package br.inatel.conversionmanager.exception;

public class ExternalApiConnectionException extends RuntimeException {

    public ExternalApiConnectionException(String currencyConversionBaseUrl) {
        super(String.format("It was not possible to communicate with API at location [%s]  check your internet connection", currencyConversionBaseUrl));
    }
}