package io.github.meyllane.sfmain.persistence.database.converters;

import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.domain.elements.SpeciesElement;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class SpeciesConverter implements AttributeConverter<SpeciesElement, Integer> {
    @Override
    public Integer convertToDatabaseColumn(SpeciesElement species) {return species.getId();}

    @Override
    public SpeciesElement convertToEntityAttribute(Integer id) {
        return SFMain.speciesElementRegistry.get(id);
    }
}
