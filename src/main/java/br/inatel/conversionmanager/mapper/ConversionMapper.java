package br.inatel.conversionmanager.mapper;

import br.inatel.conversionmanager.model.dto.ConversionDto;
import br.inatel.conversionmanager.model.entities.Conversion;
import org.springframework.stereotype.Component;

@Component
public class ConversionMapper {

    public static ConversionDto toDto(Conversion conversion) {
        return new ConversionDto(
                conversion.getId(),
                conversion.getAmount(),
                conversion.getToCurrency(),
                conversion.getConversionDate(),
                conversion.getBaseCurrency()
        );
    }

    public static Conversion toEntity(ConversionDto conversionDto) {
        return Conversion.builder()
                .id(conversionDto.id())
                .amount(conversionDto.amount())
                .toCurrency(conversionDto.to())
                .conversionDate(conversionDto.date())
                .baseCurrency(conversionDto.baseCurrency())
                .build();
    }
}

