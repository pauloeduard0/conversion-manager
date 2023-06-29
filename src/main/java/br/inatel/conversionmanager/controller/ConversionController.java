package br.inatel.conversionmanager.controller;

import br.inatel.conversionmanager.adapter.ConversionAdapter;

import br.inatel.conversionmanager.model.entities.ExchangeRateResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exchange-rates")
public class ConversionController {

    private final ConversionAdapter conversionAdapter;

    public ConversionController(ConversionAdapter conversionAdapter) {
        this.conversionAdapter = conversionAdapter;
    }

    @GetMapping
    public ResponseEntity<List<ExchangeRateResponse>> getExchangeRates() {
        List<ExchangeRateResponse> exchangeRates = conversionAdapter.getExchangeRates();

        if (exchangeRates.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(exchangeRates);
    }
}
