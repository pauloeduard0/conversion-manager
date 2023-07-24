package br.inatel.conversionmanager.mapper;

import br.inatel.conversionmanager.model.dto.ConversionDto;
import br.inatel.conversionmanager.model.entities.Conversion;

import java.util.List;

public class ConversionMapper {

    public static ConversionDto toDto(Conversion conversion) {
        return ConversionDto.builder()
                .baseCurrency(conversion.getBase())
                .amount(conversion.getAmount())
                .to(conversion.getCurrency())
                .convertedAmount(conversion.getConverted())
                .date(conversion.getDate())
                .build();
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
                .toList();
    }

}