package br.inatel.conversionmanager.exception;

import br.inatel.conversionmanager.model.entities.Conversion;

public class CurrencyNotFoundException extends RuntimeException{
    public CurrencyNotFoundException(Conversion conversion) {
        super(String.format("Currency with '%s' was not found.", conversion.getTocurrency()));
    }
}
