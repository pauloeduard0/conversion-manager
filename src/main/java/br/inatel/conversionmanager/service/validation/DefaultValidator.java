package br.inatel.conversionmanager.service.validation;

import br.inatel.conversionmanager.model.entities.Conversion;

public interface DefaultValidator {
    void isValid(Conversion conversion);
}
