package com.Nezdanchik.spbpu;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

public class JsonSerializer {

    public static String serializeToJson(Object obj) throws IllegalAccessException {
        return serializeToJson(obj, "");
    }

    public static String serializeToJson(Object obj, String indent) throws IllegalAccessException {
        if (obj == null) {
            return "null";
        }
        Class<?> clazz = obj.getClass();
        StringBuilder json = new StringBuilder();
        String nextIndent = indent + "  ";

        if (clazz.isArray()) {
            json.append("[\n");
            int length = Array.getLength(obj);
            for (int i = 0; i < length; i++) {
                json.append(nextIndent).append(serializeToJson(Array.get(obj, i), nextIndent));
                if (i < length - 1) {
                    json.append(",");
                }
                json.append("\n");
            }
            json.append(indent).append("]");
        } else if (obj instanceof Collection) {
            Collection<?> collection = (Collection<?>) obj;
            json.append("[\n");
            int i = 0;
            for (Object item : collection) {
                json.append(nextIndent).append(serializeToJson(item, nextIndent));
                if (i < collection.size() - 1) {
                    json.append(",");
                }
                json.append("\n");
                i++;
            }
            json.append(indent).append("]");
        } else if (obj instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) obj;
            json.append("{\n");
            int i = 0;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                json.append(nextIndent).append("\"").append(entry.getKey().toString()).append("\": ");
                json.append(serializeToJson(entry.getValue(), nextIndent));
                if (i < map.size() - 1) {
                    json.append(",");
                }
                json.append("\n");
                i++;
            }
            json.append(indent).append("}");
        } else if (clazz.isPrimitive() || obj instanceof String || obj instanceof Number || obj instanceof Boolean) {
            if (obj instanceof String) {
                json.append("\"").append(obj).append("\"");
            } else {
                json.append(obj.toString());
            }
        } else {
            json.append("{\n");
            Field[] fields = clazz.getDeclaredFields();
            boolean first = true;
            for (Field field : fields) {
                field.setAccessible(true); // Дает доступ к приватным полям
                if (!first) {
                    json.append(",\n");
                }
                json.append(nextIndent).append("\"").append(field.getName()).append("\": ");
                json.append(serializeToJson(field.get(obj), nextIndent));
                first = false;
            }
            json.append("\n").append(indent).append("}");
        }
        return json.toString();
    }
}


