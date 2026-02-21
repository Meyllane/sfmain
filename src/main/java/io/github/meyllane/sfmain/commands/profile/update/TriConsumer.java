package io.github.meyllane.sfmain.commands.profile.update;

@FunctionalInterface
public interface TriConsumer<K, P, V> {
    void accept(K k, P p, V v);
}
