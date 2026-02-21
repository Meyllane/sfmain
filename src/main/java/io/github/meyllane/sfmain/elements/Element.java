package io.github.meyllane.sfmain.elements;

public class Element {
    protected Integer id;
    protected String name;

    public Element(Integer id, String name) {
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
        return obj != null && obj.getClass() == this.getClass() && ((Element) obj).id.equals(id);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }
}
