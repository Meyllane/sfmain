package io.github.meyllane.sfmain.domain.elements;

public class TraitElement extends Element {
    private final String description;

    public TraitElement(Integer id, String name, String description) {
        super(id, name);
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
