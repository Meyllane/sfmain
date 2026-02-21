package io.github.meyllane.sfmain.persistence.database.converters;

import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.application.registries.ElementRegistry;
import io.github.meyllane.sfmain.elements.MasteryElement;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class MasteryConverter implements AttributeConverter<MasteryElement, Integer> {
    private static final ElementRegistry<MasteryElement> masteryRegistry = SFMain.masteriesRegistry;

    @Override
    public Integer convertToDatabaseColumn(MasteryElement attribute) {
        return attribute.getId();
    }

    @Override
    public MasteryElement convertToEntityAttribute(Integer dbData) {
        return masteryRegistry.getById(dbData);
    }
}
