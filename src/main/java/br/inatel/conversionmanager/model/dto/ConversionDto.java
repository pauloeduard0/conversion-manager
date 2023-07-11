package br.inatel.conversionmanager.model.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record ConversionDto(

        String baseCurrency,
        Float amount,
        String to,
        Float convertedAmount,
        LocalDate date

) {
}
