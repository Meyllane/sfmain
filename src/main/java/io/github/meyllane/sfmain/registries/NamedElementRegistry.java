package io.github.meyllane.sfmain.registries;

import io.github.meyllane.sfmain.named_elements.NamedElement;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class NamedElementRegistry<C extends NamedElement>{
    private final ConcurrentHashMap<String, C> byName = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, C> byId = new ConcurrentHashMap<>();

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

    public List<String> getNames() {
        return this.values().stream()
                .map(NamedElement::getName)
                .toList();
    }
}
