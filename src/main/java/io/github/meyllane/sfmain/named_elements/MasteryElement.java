package io.github.meyllane.sfmain.named_elements;

import java.util.List;

public class MasteryElement extends NamedElement{
    private List<MasterySpecializationElement> specializations;

    public MasteryElement(Integer id, String name) {
        super(id, name);
    }

    public List<MasterySpecializationElement> getSpecializations() {
        return specializations;
    }

    public void setSpecializations(List<MasterySpecializationElement> specializations) {
        this.specializations = specializations;
    }
}
