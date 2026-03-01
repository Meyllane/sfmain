package io.github.meyllane.sfmain.application.registries.core;

import io.github.meyllane.sfmain.domain.elements.Element;
import io.github.meyllane.sfmain.domain.elements.MasteryElement;
import io.github.meyllane.sfmain.errors.ErrorMessage;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ElementRegistry<C extends Element> implements Registry<C> {
    protected final ConcurrentHashMap<String, C> byName = new ConcurrentHashMap<>();
    protected final ConcurrentHashMap<Integer, C> byId = new ConcurrentHashMap<>();

    public void register(C elem) {
        if (byName.containsKey(elem.getName())) {
            throw new IllegalArgumentException(String.format(
                    ErrorMessage.get("element_registry.duplicate_name_key"), elem.getName()
            ));
        }
        if (byId.containsKey(elem.getId())) {
            throw new IllegalArgumentException(String.format(
                    ErrorMessage.get("element_registry.duplicate_id_key"), elem.getId()
            ));
        }
        byName.put(elem.getName(), elem);
        byId.put(elem.getId(), elem);
    }

    public C get(String name) {
        return this.byName.get(name);
    }
    public C get(int Id) {
        return this.byId.get(Id);
    }

    public Set<C> getValues() {
        return new HashSet<>(this.byName.values());
    }

    public Set<String> getKeys() {
        return this.byName.keySet();
    }

    public int getSize() {
        return this.byName.size();
    }

    public abstract void load(YamlConfiguration config);
}
