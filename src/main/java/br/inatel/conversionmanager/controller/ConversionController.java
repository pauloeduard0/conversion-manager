package br.inatel.conversionmanager.controller;

import br.inatel.conversionmanager.model.dto.ConversionDto;
import br.inatel.conversionmanager.model.dto.ExchangeRateResponse;
import br.inatel.conversionmanager.service.ConversionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/exchange-rates")
public class ConversionController {

    private final ConversionService conversionService;

    public ConversionController(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<ExchangeRateResponse>> getExchangeRates() {
        return ResponseEntity.ok(conversionService.getAllCurrency());
    }

    @GetMapping()
    public ResponseEntity<List<ConversionDto>> getConversions(@RequestParam(required = false) String currency) {
        if (currency != null) {
            List<ConversionDto> conversions = conversionService.getConversionsByCurrency(currency);
            return ResponseEntity.ok(conversions);
        } else {
            return ResponseEntity.ok(conversionService.getAllConversions());
        }
    }

    @PostMapping
    public ResponseEntity<ConversionDto> saveConversion(@Valid @RequestBody ConversionDto conversionDto) {

        ConversionDto savedConversion = conversionService.saveConversion(conversionDto);

        return ResponseEntity.created(URI.create("")).body(savedConversion);
    }

}