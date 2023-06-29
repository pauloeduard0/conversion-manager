package br.inatel.conversionmanager.model.entities;

import java.util.Map;

public record ExchangeRateResponse(long timestamp,
                           String base,
                           boolean success,
                           Map<String, Double> rates,
                           String date,
                           boolean historical) {
}
