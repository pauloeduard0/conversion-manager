package br.inatel.conversionmanager.exception;

public class CurrencyNotFoundException extends RuntimeException {
    public CurrencyNotFoundException(String conversion) {
        super(String.format("Currency with '%s' was not found.", conversion));
    }
}
