package br.inatel.conversionmanager.model.dto;

import java.time.LocalDate;
import java.util.UUID;

public record ConversionDto(

        UUID id,
        Float amount,
        String to,
        LocalDate date,
        String baseCurrency) {
}
