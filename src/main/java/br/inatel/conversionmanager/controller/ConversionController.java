package br.inatel.conversionmanager.controller;

import br.inatel.conversionmanager.adapter.ConversionAdapter;

import br.inatel.conversionmanager.model.dto.ConversionDto;
import br.inatel.conversionmanager.model.dto.ExchangeRateResponse;
import br.inatel.conversionmanager.repository.ConversionRepository;
import br.inatel.conversionmanager.service.ConversionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/exchange-rates")
public class ConversionController {

    private final ConversionAdapter conversionAdapter;

    private final ConversionService conversionService;

    private final ConversionRepository conversionRepository;

    public ConversionController(ConversionAdapter conversionAdapter, ConversionService conversionService, ConversionRepository conversionRepository) {
        this.conversionAdapter = conversionAdapter;
        this.conversionService = conversionService;
        this.conversionRepository = conversionRepository;
    }

    @GetMapping("/all")
    public ResponseEntity<List<ExchangeRateResponse>> getExchangeRates() {
        List<ExchangeRateResponse> exchangeRates = conversionAdapter.getExchangeRates();

        if (exchangeRates.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(exchangeRates);
    }

    @GetMapping
    public ResponseEntity<Page<ConversionDto>> getAllQuotes(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(conversionService.getAllConversions(pageable));
    }

    @GetMapping("/{tocurrency}")
    public ResponseEntity<List<ConversionDto>> getConversionsByCurrency(@PathVariable String tocurrency) {
        List<ConversionDto> conversions = conversionService.getConversionsByCurrency(tocurrency);
        return ResponseEntity.ok(conversions);
    }

    @PostMapping
    public ResponseEntity<ConversionDto> saveConversion(@Valid @RequestBody ConversionDto conversionDto) {

        LocalDate currentDate = LocalDate.now();
        String baseCurrency = "EURO";

        conversionDto = new ConversionDto(
                baseCurrency,
                conversionDto.amount(),
                conversionDto.to(),
                conversionDto.convertedAmount(),
                currentDate
        );

        ConversionDto savedConversion = conversionService.saveConversion(conversionDto);

        return ResponseEntity.created(null).body(savedConversion);
    }

    @Transactional
    @DeleteMapping("/clear-database")
    public void clearDatabase() {

        conversionRepository.deleteAll();
    }
}
