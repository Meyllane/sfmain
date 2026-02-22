package io.github.meyllane.sfmain.persistence.database.converters;

import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.application.registries.ElementRegistry;
import io.github.meyllane.sfmain.elements.MasterySpeElement;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class MasterySpeConverter implements AttributeConverter<MasterySpeElement, Integer> {
    private static final ElementRegistry<MasterySpeElement> masterySpecializationRegistry = SFMain.masterySpecializationsRegistry;

    @Override
    public Integer convertToDatabaseColumn(MasterySpeElement attribute) {
        return attribute.getId();
    }

    @Override
    public MasterySpeElement convertToEntityAttribute(Integer dbData) {
        return masterySpecializationRegistry.getById(dbData);
    }
}
