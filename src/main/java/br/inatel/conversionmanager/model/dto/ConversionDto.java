package br.inatel.conversionmanager.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record ConversionDto(
        String baseCurrency,
        @Positive
        @NotNull
        BigDecimal amount,
        @NotNull
        String to,
        BigDecimal convertedAmount,
        LocalDate date

) {
}
