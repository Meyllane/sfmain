package io.github.meyllane.sfmain.elements;

public class MasterySpecializationElement extends Element {
    private final Integer masteryId;
    public MasterySpecializationElement(Integer id, String name, Integer masteryId) {
        super(id, name);
        this.masteryId = masteryId;
    }

    public Integer getMasteryId() {
        return masteryId;
    }
}
