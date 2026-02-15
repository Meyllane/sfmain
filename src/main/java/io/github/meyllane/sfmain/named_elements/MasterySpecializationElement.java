package io.github.meyllane.sfmain.named_elements;

public class MasterySpecializationElement extends NamedElement{
    private final Integer masteryId;
    public MasterySpecializationElement(Integer id, String name, Integer masteryId) {
        super(id, name);
        this.masteryId = masteryId;
    }

    public Integer getMasteryId() {
        return masteryId;
    }
}
