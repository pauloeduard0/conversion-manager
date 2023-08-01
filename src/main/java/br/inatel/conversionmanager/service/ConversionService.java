package br.inatel.conversionmanager.service;

import br.inatel.conversionmanager.adapter.ConversionAdapter;
import br.inatel.conversionmanager.exception.CurrencyNotFoundException;
import br.inatel.conversionmanager.mapper.ConversionMapper;
import br.inatel.conversionmanager.model.dto.ConversionDto;
import br.inatel.conversionmanager.model.dto.ExchangeRateResponse;
import br.inatel.conversionmanager.model.entities.Conversion;
import br.inatel.conversionmanager.repository.ConversionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class ConversionService {

    private final ConversionRepository conversionRepository;
    private final ConversionAdapter conversionAdapter;

    public ConversionService(ConversionRepository conversionRepository, ConversionAdapter conversionAdapter) {
        this.conversionRepository = conversionRepository;
        this.conversionAdapter = conversionAdapter;
    }

    public ConversionDto saveConversion(ConversionDto conversionDto) {
        LocalDate currentDate = LocalDate.now();
        String baseCurrency = "EURO";

        BigDecimal exchangeRate = findExchangeRateByCurrency(conversionAdapter.getExchangeRates(), conversionDto.to());

        BigDecimal convertedAmount = conversionDto.amount().multiply(exchangeRate).setScale(5, RoundingMode.HALF_UP);

        Conversion conversion = ConversionMapper.toEntity(new ConversionDto(
                baseCurrency,
                conversionDto.amount(),
                conversionDto.to(),
                convertedAmount,
                currentDate
        ));

        Conversion savedConversion = conversionRepository.save(conversion);

        return ConversionMapper.toDto(savedConversion);
    }

    public List<ExchangeRateResponse> getAllCurrency() {
        return conversionAdapter.getExchangeRates();
    }

    public List<ConversionDto> getAllConversions() {
        List<Conversion> conversions = conversionRepository.findAll();
        return conversions.stream()
                .map(ConversionMapper::toDto)
                .toList();
    }

    public List<ConversionDto> getConversionsByCurrency(String currency) {
        List<Conversion> conversions = conversionRepository.findByCurrency(currency);
        List<Conversion> filteredConversions = conversions.stream()
                .filter(conversion -> conversion.getCurrency().equals(currency))
                .toList();
        return ConversionMapper.toDtoList(filteredConversions);
    }

    private BigDecimal findExchangeRateByCurrency(List<ExchangeRateResponse> exchangeRates, String currency) {
        return exchangeRates.stream()
                .filter(exchangeRate -> exchangeRate.rates().containsKey(currency))
                .findFirst()
                .map(exchangeRate -> exchangeRate.rates().get(currency))
                .orElseThrow(() -> new CurrencyNotFoundException(currency));
    }
}