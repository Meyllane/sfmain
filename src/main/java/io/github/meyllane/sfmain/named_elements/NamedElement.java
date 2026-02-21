package io.github.meyllane.sfmain.named_elements;

public class NamedElement {
    protected Integer id;
    protected String name;

    public NamedElement(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass() == this.getClass() && ((NamedElement) obj).id.equals(id);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }
}
