package br.inatel.conversionmanager.service;

import br.inatel.conversionmanager.adapter.ConversionAdapter;
import br.inatel.conversionmanager.mapper.ConversionMapper;
import br.inatel.conversionmanager.model.dto.ConversionDto;
import br.inatel.conversionmanager.model.entities.Conversion;
import br.inatel.conversionmanager.repository.ConversionRepository;
import br.inatel.conversionmanager.service.validation.DefaultValidator;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ConversionService {

    private final ConversionRepository conversionRepository;

    private final List<DefaultValidator> listCurrency;

    public ConversionService(ConversionRepository conversionRepository, List<DefaultValidator> listCurrency) {
        this.conversionRepository = conversionRepository;
        this.listCurrency = listCurrency;
    }

    public ConversionDto saveConversion(ConversionDto conversionDto) {

        Conversion conversion = ConversionMapper.toEntity(conversionDto);

        listCurrency.forEach(currencyVal -> currencyVal.isValid(conversion));

        return ConversionMapper.toDto(conversionRepository.save(conversion));
    }

}
