package br.inatel.conversionmanager.model.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Table(name = "conversion_coin")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Conversion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String base;

    private BigDecimal amount;

    private String currency;

    private BigDecimal converted;

    @JsonFormat(pattern = "YYYY-MM-DD")
    private LocalDate date;

}
