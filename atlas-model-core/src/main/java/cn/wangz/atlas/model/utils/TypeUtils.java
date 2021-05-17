package cn.wangz.atlas.model.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TypeUtils {

    public static List<Field> getAllFields(Class<?> clazz) {
        Field[] declaredFields = clazz.getDeclaredFields();

        Class<?> superclass = clazz.getSuperclass();
        ArrayList<Field> fields = new ArrayList<>(Arrays.asList(declaredFields));
        if (superclass != null) {
            fields.addAll(getAllFields(superclass));
        }
        return fields;
    }

    public static Object getFiledValue(Object object, Field field) throws IllegalAccessException {
        boolean accessible = field.isAccessible();
        field.setAccessible(true);
        Object filedValue = field.get(object);
        field.setAccessible(accessible);
        return filedValue;
    }


}
