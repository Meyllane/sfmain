package io.github.meyllane.sfmain.persistence.database.converters;

import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.elements.TraitElement;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TraitConverter implements AttributeConverter<TraitElement, Integer> {
    @Override
    public Integer convertToDatabaseColumn(TraitElement traitElement) {
        return traitElement.getId();
    }

    @Override
    public TraitElement convertToEntityAttribute(Integer id) {
        return SFMain.traitsRegistry.getById(id);
    }
}
