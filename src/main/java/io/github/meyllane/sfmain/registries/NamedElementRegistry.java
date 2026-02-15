package io.github.meyllane.sfmain.registries;

import io.github.meyllane.sfmain.named_elements.NamedElement;

import java.util.Collection;
import java.util.HashMap;

public class NamedElementRegistry<C extends NamedElement>{
    private final HashMap<String, C> byName = new HashMap<>();
    private final HashMap<Integer, C> byId = new HashMap<>();

    public void register(C elem) {
        byName.put(elem.getName(), elem);
        byId.put(elem.getId(), elem);
    }

    public C getByName(String name) { return byName.get(name);}

    public C getById(Integer id) {return byId.get(id);}

    public int count() {return this.byId.size();}

    public Collection<C> values() {
        return byId.values();
    }
}
