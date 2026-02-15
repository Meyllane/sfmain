package io.github.meyllane.sfmain.database.converters;

import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.named_elements.SpeciesElement;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class SpeciesConverter implements AttributeConverter<SpeciesElement, Integer> {
    @Override
    public Integer convertToDatabaseColumn(SpeciesElement species) {
        return species.getId();
    }

    @Override
    public SpeciesElement convertToEntityAttribute(Integer id) {
        return SFMain.speciesRegistry.getById(id);
    }
}
