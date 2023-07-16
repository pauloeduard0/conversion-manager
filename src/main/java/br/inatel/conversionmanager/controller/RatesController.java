package br.inatel.conversionmanager.controller;

import br.inatel.conversionmanager.adapter.ConversionAdapter;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/clear-cache")
public class RatesController {

    private final ConversionAdapter conversionAdapter;


    public RatesController(ConversionAdapter conversionAdapter) {
        this.conversionAdapter = conversionAdapter;
    }

    @DeleteMapping
    public void deleteConversionCache() {
        conversionAdapter.clearCurrencyCache();
    }
}
