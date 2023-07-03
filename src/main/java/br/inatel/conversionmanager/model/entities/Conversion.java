package br.inatel.conversionmanager.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private Float amount;

    private String baseCurrency;

    private String toCurrency;

    private LocalDate conversionDate;

}
