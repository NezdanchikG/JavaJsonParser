package com.Nezdanchik.spbpu;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ObjectMapper {




    public <T> T mapToObject(Map<String, Object> map, Class<T> clazz) throws Exception {
        T obj = clazz.getDeclaredConstructor().newInstance();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);  // сделать приватные поля доступными
            Object value = map.get(field.getName());

            if (value == null) {
                continue;
            }

            Class<?> fieldType = field.getType();
            Object convertedValue = convertValueToFieldType(value, fieldType, field);
            field.set(obj, convertedValue);
        }
        return obj;
    }

    private Object convertValueToFieldType(Object value, Class<?> fieldType, Field field) throws Exception {
        if (fieldType.isInstance(value)) {
            return value; // типы совпадают
        }

        if (value instanceof Map<?, ?> && !Map.class.isAssignableFrom(fieldType)) {
            // рекурсивное преобразование для вложенных объектов
            return mapToObject((Map<String, Object>) value, fieldType);
        } else if (value instanceof List<?> && List.class.isAssignableFrom(fieldType)) {
            // специальная обработка для полей типа List
            return convertList((List<?>) value, field);
        } else if (value instanceof Number) {
            //преобразование числовых типов, если значение - число
            return convertNumber((Number) value, fieldType);
        } else if (value instanceof Boolean && (fieldType == boolean.class || fieldType == Boolean.class)) {
            return value;  // присваивание для булевых значений
        }

        throw new IllegalArgumentException("Cannot assign value of type " + value.getClass() + " to field of type " + fieldType);
    }

    private List<Object> convertList(List<?> originalList, Field field) throws Exception {
        List<Object> newList = new ArrayList<>();
        Class<?> genericType = getGenericType(field);
        for (Object item : originalList) {
            newList.add(convertValueToFieldType(item, genericType, field));
        }
        return newList;
    }

    private Class<?> getGenericType(Field field) {
        ParameterizedType listType = (ParameterizedType) field.getGenericType();
        return (Class<?>) listType.getActualTypeArguments()[0];
    }

    private Object convertNumber(Number number, Class<?> fieldType) {
        if (fieldType == int.class || fieldType == Integer.class) {
            return number.intValue();
        } else if (fieldType == double.class || fieldType == Double.class) {
            return number.doubleValue();
        } else if (fieldType == float.class || fieldType == Float.class) {
            return number.floatValue();
        } else if (fieldType == long.class || fieldType == Long.class) {
            return number.longValue();
        } else if (fieldType == short.class || fieldType == Short.class) {
            return number.shortValue();
        } else if (fieldType == byte.class || fieldType == Byte.class) {
            return number.byteValue();
        }
        return number;
    }
}
