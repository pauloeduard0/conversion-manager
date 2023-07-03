package br.inatel.conversionmanager.service;

import br.inatel.conversionmanager.mapper.ConversionMapper;
import br.inatel.conversionmanager.model.dto.ConversionDto;
import br.inatel.conversionmanager.model.entities.Conversion;
import br.inatel.conversionmanager.repository.ConversionRepository;
import org.springframework.stereotype.Service;


@Service
public class ConversionService {

    private final ConversionRepository conversionRepository;

    public ConversionService(ConversionRepository conversionRepository) {
        this.conversionRepository = conversionRepository;
    }

    public ConversionDto saveConversion(ConversionDto conversionDto) {

        Conversion conversion = ConversionMapper.toEntity(conversionDto);

        return ConversionMapper.toDto(conversionRepository.save(conversion));
    }

}
