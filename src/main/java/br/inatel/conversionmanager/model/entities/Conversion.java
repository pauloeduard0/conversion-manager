package br.inatel.conversionmanager.model.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Table;
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

    private Float amount;

    private String tocurrency;

    private Float converted;

    @JsonFormat(pattern = "YYYY-MM-DD")
    private LocalDate date;

}
