package br.inatel.conversionmanager.controller;

import br.inatel.conversionmanager.adapter.ConversionAdapter;

import br.inatel.conversionmanager.model.dto.ConversionDto;
import br.inatel.conversionmanager.model.dto.ExchangeRateResponse;
import br.inatel.conversionmanager.service.ConversionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/exchange-rates")
public class ConversionController {

    private final ConversionAdapter conversionAdapter;

    private final ConversionService conversionService;

    public ConversionController(ConversionAdapter conversionAdapter, ConversionService conversionService) {
        this.conversionAdapter = conversionAdapter;
        this.conversionService = conversionService;
    }

    @GetMapping
    public ResponseEntity<List<ExchangeRateResponse>> getExchangeRates() {
        List<ExchangeRateResponse> exchangeRates = conversionAdapter.getExchangeRates();

        if (exchangeRates.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(exchangeRates);
    }

    @GetMapping("/all")
    public ResponseEntity<Page<ConversionDto>> getAllQuotes(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(conversionService.getAllConversions(pageable));
    }

    @PostMapping
    public ResponseEntity<ConversionDto> saveConversion(@Valid @RequestBody ConversionDto conversionDto) {

        LocalDate currentDate = LocalDate.now();

        String baseCurrency = "EURO";

        ConversionDto savedConversion = new ConversionDto(
                UUID.randomUUID(),
                baseCurrency,
                conversionDto.amount(),
                conversionDto.to(),
                conversionDto.convertedAmount(),
                currentDate
        );

        return ResponseEntity.created(null).body(conversionService.saveConversion(savedConversion));
    }
}
