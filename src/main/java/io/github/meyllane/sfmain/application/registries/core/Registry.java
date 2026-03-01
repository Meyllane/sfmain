package io.github.meyllane.sfmain.application.registries.core;

import java.util.Set;

public interface Registry<C> {
    public void register(C elem);
    public C get(String name);
    public Set<C> getValues();
    public Set<String> getKeys();
}
