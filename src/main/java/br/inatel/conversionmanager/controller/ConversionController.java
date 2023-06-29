package br.inatel.conversionmanager.controller;

import br.inatel.conversionmanager.adapter.ConversioAdapter;

import br.inatel.conversionmanager.model.entities.ExchangeRateResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exchange-rates")
public class ConversionController {

    private final ConversioAdapter conversioAdapter;

    public ConversionController(ConversioAdapter conversioAdapter) {
        this.conversioAdapter = conversioAdapter;
    }

    @GetMapping
    public ResponseEntity<List<ExchangeRateResponse>> getExchangeRates() {
        List<ExchangeRateResponse> exchangeRates = conversioAdapter.getExchangeRates();

        if (exchangeRates.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(exchangeRates);
    }
}
