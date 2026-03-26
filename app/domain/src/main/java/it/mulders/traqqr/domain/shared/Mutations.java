package it.mulders.traqqr.domain.shared;

import java.util.Collection;
import java.util.Map;

public class Mutations {
    private Mutations() {}

    public static <T> Mutator<T> overwrite(T original) {
        return new Mutator<>(original);
    }

    public static <T> Mutator<Collection<T>> overwrite(Collection<T> original) {
        throw new IllegalArgumentException("Not yet implemented for collections");
    }

    public static <K, V> Mutator<Map<K, V>> overwrite(Map<K, V> original) {
        throw new IllegalArgumentException("Not yet implemented for maps");
    }

    public record Mutator<T>(T original) {
        public T with(T update) {
            return update != null ? update : original;
        }
    }
}
