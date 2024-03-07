package uk.joshiejack.penguinlib.util.helper;

import net.neoforged.fml.util.ObfuscationReflectionHelper;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class ReflectionHelper {
    public static <T, E> void setPrivateFinalValue(Class<? super T> classToAccess, T instance, E value, String fieldName) {
        try {
            Field field = ObfuscationReflectionHelper.findField(classToAccess, fieldName);
            field.setAccessible(true);
            field.set(instance, value);
        } catch (ObfuscationReflectionHelper.UnableToFindFieldException | IllegalAccessException ignored) {}
    }

    @Nullable
    public static <T> T newInstance(Class<T> data) {
        try {
            return data.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public static <T> Constructor<T> getConstructor(Class<T> data, Class<?>... params) {
        try {
            return data.getConstructor(params);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public static <T> T newInstance(Constructor<T> constructor, Object... data) {
        try {
            return constructor.newInstance(data);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }
}