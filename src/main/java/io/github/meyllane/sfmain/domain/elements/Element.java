package io.github.meyllane.sfmain.domain.elements;

public abstract class Element {
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
    public String toString() {
        return this.getClass().getName() + "[id: " + this.getId() + "; name: " + this.getName() + "]";
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
