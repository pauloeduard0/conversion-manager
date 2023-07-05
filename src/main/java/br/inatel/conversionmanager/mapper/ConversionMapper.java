package br.inatel.conversionmanager.mapper;

import br.inatel.conversionmanager.model.dto.ConversionDto;
import br.inatel.conversionmanager.model.entities.Conversion;
import org.springframework.stereotype.Component;

@Component
public class ConversionMapper {

    public static ConversionDto toDto(Conversion conversion) {
        return new ConversionDto(
                conversion.getId(),
                conversion.getBase(),
                conversion.getAmount(),
                conversion.getTocurrency(),
                conversion.getConverted(),
                conversion.getDate()
        );
    }

    public static Conversion toEntity(ConversionDto conversionDto) {
        return Conversion.builder()
                .id(conversionDto.id())
                .amount(conversionDto.amount())
                .tocurrency(conversionDto.to())
                .converted(conversionDto.convertedAmount())
                .date(conversionDto.date())
                .base(conversionDto.baseCurrency())
                .build();
    }
}

