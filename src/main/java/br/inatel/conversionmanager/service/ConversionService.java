package br.inatel.conversionmanager.service;

import br.inatel.conversionmanager.adapter.ConversionAdapter;
import br.inatel.conversionmanager.exception.CurrencyNotFoundException;
import br.inatel.conversionmanager.mapper.ConversionMapper;
import br.inatel.conversionmanager.model.dto.ConversionDto;
import br.inatel.conversionmanager.model.dto.ExchangeRateResponse;
import br.inatel.conversionmanager.model.entities.Conversion;
import br.inatel.conversionmanager.repository.ConversionRepository;
import br.inatel.conversionmanager.service.validation.DefaultValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConversionService {

    private final ConversionRepository conversionRepository;
    private final List<DefaultValidator> listCurrency;
    private final ConversionAdapter conversionAdapter;

    public ConversionService(ConversionRepository conversionRepository, List<DefaultValidator> listCurrency, ConversionAdapter conversionAdapter) {
        this.conversionRepository = conversionRepository;
        this.listCurrency = listCurrency;
        this.conversionAdapter = conversionAdapter;
    }

    public ConversionDto saveConversion(ConversionDto conversionDto) {
        Conversion conversion = ConversionMapper.toEntity(conversionDto);

        listCurrency.forEach(currencyVal -> currencyVal.isValid(conversion));

        List<ExchangeRateResponse> exchangeRates = conversionAdapter.getExchangeRates();

        ExchangeRateResponse exchangeRate = findExchangeRateByCurrency(exchangeRates, conversion.getCurrency());

        if (exchangeRate == null) {
            throw new CurrencyNotFoundException(conversion);
        }

        float rate = exchangeRate.rates().get(conversion.getCurrency());
        float convertedAmount = conversion.getAmount() * rate;
        conversion.setConverted(convertedAmount);

        Conversion savedConversion = conversionRepository.save(conversion);

        return ConversionMapper.toDto(savedConversion);
    }

    private ExchangeRateResponse findExchangeRateByCurrency(List<ExchangeRateResponse> exchangeRates, String currency) {
        return exchangeRates.stream()
                .filter(exchangeRate -> exchangeRate.rates().containsKey(currency))
                .findFirst()
                .orElse(null);
    }

    public Page<ConversionDto> getAllConversions(Pageable pageable) {
        return conversionRepository.findAll(pageable).map(ConversionMapper::toDto);
    }

    public List<ConversionDto> getConversionsByCurrency(String currency) {
        List<Conversion> conversions = conversionRepository.findByCurrency(currency);
        List<Conversion> filteredConversions = conversions.stream()
                .filter(conversion -> conversion.getCurrency().equals(currency))
                .collect(Collectors.toList());
        return ConversionMapper.toDtoList(filteredConversions);
    }
}

