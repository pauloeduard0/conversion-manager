package br.inatel.conversionmanager.mapper;

import br.inatel.conversionmanager.model.dto.ConversionDto;
import br.inatel.conversionmanager.model.entities.Conversion;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ConversionMapper {

    public static ConversionDto toDto(Conversion conversion) {
        return new ConversionDto(
                conversion.getBase(),
                conversion.getAmount(),
                conversion.getCurrency(),
                conversion.getConverted(),
                conversion.getDate()
        );
    }

    public static Conversion toEntity(ConversionDto conversionDto) {
        return Conversion.builder()
                .amount(conversionDto.amount())
                .currency(conversionDto.to())
                .converted(conversionDto.convertedAmount())
                .date(conversionDto.date())
                .base(conversionDto.baseCurrency())
                .build();
    }

    public static List<ConversionDto> toDtoList(List<Conversion> conversions) {
        return conversions.stream()
                .map(ConversionMapper::toDto)
                .collect(Collectors.toList());
    }

}

