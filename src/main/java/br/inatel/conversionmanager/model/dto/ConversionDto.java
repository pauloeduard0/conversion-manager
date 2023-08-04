package br.inatel.conversionmanager.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Builder
public record ConversionDto(

        UUID id,
        String baseCurrency,
        @Positive
        @NotNull
        BigDecimal amount,
        @NotNull
        String to,
        BigDecimal convertedAmount,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate date

) {
}
