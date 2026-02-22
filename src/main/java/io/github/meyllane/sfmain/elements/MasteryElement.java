package io.github.meyllane.sfmain.elements;

import java.util.List;
import java.util.Set;

public class MasteryElement extends Element {
    private Set<MasterySpeElement> specializations;

    public MasteryElement(Integer id, String name) {
        super(id, name);
    }

    public Set<MasterySpeElement> getSpecializations() {
        return specializations;
    }

    public void setSpecializations(Set<MasterySpeElement> specializations) {
        this.specializations = specializations;
    }
}
