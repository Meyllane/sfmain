package io.github.meyllane.sfmain.application.registries.core;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ModelRegistry<C> implements Registry<C>{
    protected Map<String, C> map = new ConcurrentHashMap<>();

    @Override
    public C get(String key) {
        return map.get(key);
    }

    @Override
    public Set<C> getValues() {
        return new HashSet<>(map.values());
    }

    @Override
    public Set<String> getKeys() {
        return map.keySet();
    }

    public boolean contains(String key) {
        return this.map.containsKey(key);
    }

    public boolean contains(C elem) {
        return this.map.containsValue(elem);
    }

    public void delete(String key) {
        this.map.remove(key);
    }
}
