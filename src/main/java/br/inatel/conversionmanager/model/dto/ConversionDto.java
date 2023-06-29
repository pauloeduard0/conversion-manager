package br.inatel.conversionmanager.model.dto;

import java.util.UUID;

public record ConversionDto(

        UUID id,
        Double amount,
        String to) {
}
