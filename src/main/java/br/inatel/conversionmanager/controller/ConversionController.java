package br.inatel.conversionmanager.controller;

import br.inatel.conversionmanager.adapter.ConversionAdapter;

import br.inatel.conversionmanager.model.dto.ConversionDto;
import br.inatel.conversionmanager.model.entities.ExchangeRateResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

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

    @PostMapping
    public ResponseEntity<ConversionDto> saveConversion(@Valid @RequestBody ConversionDto conversionDto) {

        LocalDate currentDate = LocalDate.now();
        // Defina a moeda base como "EURO"
        String baseCurrency = "EURO";

        ConversionDto savedConversion = new ConversionDto(
                UUID.randomUUID(),
                conversionDto.amount(),
                conversionDto.to(),
                currentDate,
                baseCurrency
        );

        // Retornar a resposta com status 201 (Created) e o objeto ConversionDto como corpo da resposta
        return ResponseEntity.created(null).body(savedConversion);
    }
}
