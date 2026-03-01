package io.github.meyllane.sfmain.persistence.database.converters;

import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.domain.elements.MasterySpeElement;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class MasterySpeConverter implements AttributeConverter<MasterySpeElement, Integer> {
    @Override
    public Integer convertToDatabaseColumn(MasterySpeElement attribute) {
        return attribute.getId();
    }

    @Override
    public MasterySpeElement convertToEntityAttribute(Integer dbData) {
        return SFMain.masterySpeElementRegistry.get(dbData);
    }
}
