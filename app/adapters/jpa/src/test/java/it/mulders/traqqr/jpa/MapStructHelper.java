package it.mulders.traqqr.jpa;

import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import org.mapstruct.factory.Mappers;

/**
 * MapStructs {@link Mappers#getMapper(Class)} only works for mappers that do not have a {@link org.mapstruct.MappingConstants.ComponentModel} annotation.
 * This class works around that limitation by providing a way to lookup nested mappers.
 */
public class MapStructHelper {
    public static <T> T getMapper(Class<T> clazz) {
        // Could consider to cache implementations if this code appears to slow down tests too much.
        try {
            var implClass = Mappers.getMapperClass(clazz);
            var impl = implClass.getDeclaredConstructor().newInstance();

            Arrays.stream(implClass.getDeclaredFields())
                    .filter(declaredField -> declaredField.getName().endsWith("Mapper"))
                    .forEach(declaredField -> {
                        try {
                            var field = implClass.getDeclaredField(declaredField.getName());
                            field.setAccessible(true);
                            field.set(impl, getMapper(field.getType()));
                        } catch (NoSuchFieldException | IllegalAccessException e) {
                            fail(e);
                        }
                    });

            return impl;
        } catch (NoSuchMethodException
                | InstantiationException
                | IllegalAccessException
                | InvocationTargetException
                | SecurityException e) {
            fail(e);
            return null;
        }
    }
}
