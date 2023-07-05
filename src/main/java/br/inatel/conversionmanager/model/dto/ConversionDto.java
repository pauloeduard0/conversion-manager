package br.inatel.conversionmanager.model.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record ConversionDto(

        UUID id,
        String baseCurrency,
        Float amount,
        String to,
        Float convertedAmount,
        LocalDate date

) {
}
