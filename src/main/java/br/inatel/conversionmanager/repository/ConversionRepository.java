package br.inatel.conversionmanager.repository;

import br.inatel.conversionmanager.model.entities.Conversion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ConversionRepository extends JpaRepository<Conversion, UUID> {
    List<Conversion> findByCurrency(String currencyId);
}
