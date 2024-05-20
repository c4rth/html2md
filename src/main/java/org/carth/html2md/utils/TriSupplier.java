package org.carth.html2md.utils;

@FunctionalInterface
public interface TriSupplier<T, U, V, R> {
    R get(T t, U u, V v);
}