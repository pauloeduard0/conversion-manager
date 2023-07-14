package br.inatel.conversionmanager.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record ConversionDto(
        String baseCurrency,
        @Positive
        Float amount,
        @NotNull
        String to,
        Float convertedAmount,
        LocalDate date

) {
}
