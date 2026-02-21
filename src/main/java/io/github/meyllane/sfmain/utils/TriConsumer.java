package io.github.meyllane.sfmain.utils;

@FunctionalInterface
public interface TriConsumer<K, P, V> {
    void accept(K k, P p, V v);
}
