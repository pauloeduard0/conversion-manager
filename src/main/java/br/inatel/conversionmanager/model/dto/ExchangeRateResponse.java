package br.inatel.conversionmanager.model.dto;

import lombok.Builder;

import java.util.Map;

@Builder
public record ExchangeRateResponse(long timestamp,
                           String base,
                           boolean success,
                           Map<String, Float> rates,
                           String date,
                           boolean historical) {
}
