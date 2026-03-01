package io.github.meyllane.sfmain.domain.elements;

public class MasterySpeElement extends Element {
    private final Integer masteryId;
    public MasterySpeElement(Integer id, String name, Integer masteryId) {
        super(id, name);
        this.masteryId = masteryId;
    }

    public Integer getMasteryId() {
        return masteryId;
    }
}
