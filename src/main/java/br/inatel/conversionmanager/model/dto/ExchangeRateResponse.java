package br.inatel.conversionmanager.model.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.Map;

@Builder
public record ExchangeRateResponse(long timestamp,
                                   String base,
                                   boolean success,
                                   Map<String, BigDecimal> rates,
                                   String date,
                                   boolean historical) {
}
