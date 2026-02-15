package io.github.meyllane.sfmain.named_elements;

public class TraitElement extends NamedElement{
    private final String description;

    public TraitElement(Integer id, String name, String description) {
        super(id, name);
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
