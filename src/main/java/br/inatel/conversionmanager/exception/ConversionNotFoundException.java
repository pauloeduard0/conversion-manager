package br.inatel.conversionmanager.exception;

import java.util.UUID;

public class ConversionNotFoundException extends RuntimeException {

    public ConversionNotFoundException(UUID id) {
        super(String.format("Conversion with '%s' was not found.", id));
    }
}
