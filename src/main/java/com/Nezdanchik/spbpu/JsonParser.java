package com.Nezdanchik.spbpu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonParser {
    private List<JsonToken> tokens;
    private int position = 0;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // конструктор, принимающий токены
    public JsonParser(List<JsonToken> tokens) {
        this.tokens = tokens;
    }

    //  конструктор, принимающий строку JSON
    public JsonParser(String jsonString) throws Exception {
        JsonTokenizer tokenizer = new JsonTokenizer(jsonString);
        this.tokens = tokenizer.tokenize();  // Токенизация входной строки
    }

    // парсинг, работающий с токенами
    public Object parse() throws Exception {
        if (tokens.isEmpty()) {
            throw new IllegalStateException("No tokens available for parsing.");
        }
        try {
            position = 0;
            JsonToken current = peek();
            switch (current.type()) {
                case BracketObjectLeft:
                    return parseObject();
                case BracketArrayLeft:
                    return parseArray();
                case String:
                case Number:
                case True:
                case False:
                case Null:
                    return parseValue();  // Обрабатываем примитивы на верхнем уровне
                default:
                    throw new IllegalArgumentException("Invalid JSON format. Unexpected token: " + current.type());
            }
        } finally {
            position = 0;
        }
    }

    public Object parse(String jsonString) throws Exception {
        JsonTokenizer tokenizer = new JsonTokenizer(jsonString);
        this.tokens = tokenizer.tokenize();
        return parse();
    }

    public <T> T parse(Class<T> clazz) throws Exception {
        Object result = parse();
        if (clazz.isInstance(result)) {
            return clazz.cast(result);
        } else if (result instanceof Map) {
            return objectMapper.mapToObject((Map<String, Object>) result, clazz);
        }
        throw new IllegalArgumentException("Cannot cast parsed object to " + clazz.getName());
    }

    private Object parseObject() throws Exception {
        expect(JsonToken.Type.BracketObjectLeft);
        Map<String, Object> obj = new HashMap<>();
        while (peek().type() != JsonToken.Type.BracketObjectRight) {
            JsonToken keyToken = expect(JsonToken.Type.String);
            expect(JsonToken.Type.Colon);
            Object value = parseValue();
            obj.put(keyToken.value(), value);
            if (peek().type() == JsonToken.Type.Comma) {
                expect(JsonToken.Type.Comma);
            }
        }
        expect(JsonToken.Type.BracketObjectRight);
        return obj;
    }

    Object parseArray() throws Exception {
        expect(JsonToken.Type.BracketArrayLeft);
        List<Object> array = new ArrayList<>();
        while (peek().type() != JsonToken.Type.BracketArrayRight) {
            array.add(parseValue());
            if (peek().type() == JsonToken.Type.Comma) {
                expect(JsonToken.Type.Comma);
            } else if (peek().type() != JsonToken.Type.BracketArrayRight) {
                throw new IllegalArgumentException("Expected ',' or ']', but found: " + peek().value());
            }
        }
        expect(JsonToken.Type.BracketArrayRight);
        return array;
    }

    private Object parseValue() throws Exception {
        JsonToken token = peek();
        switch (token.type()) {
            case Number:
                expect(JsonToken.Type.Number);
                return parseNumber(token);
            case String:
                expect(JsonToken.Type.String);
                return token.value();
            case True:
            case False:
                expect(token.type());
                return Boolean.parseBoolean(token.value());
            case Null:
                expect(JsonToken.Type.Null);
                return null;
            case BracketObjectLeft:
                return parseObject();
            case BracketArrayLeft:
                return parseArray();
            default:
                throw new IllegalArgumentException("Unexpected value type: " + token.value());
        }
    }

    public static Number parseNumber(JsonToken token) {
        String content = token.value();
        if (content.contains(".") || content.contains("E") || content.contains("e")) {
            return Double.parseDouble(content);
        }
        return Integer.parseInt(content);
    }

    private JsonToken expect(JsonToken.Type expectedType) throws Exception {
        if (position >= tokens.size()) {
            throw new IllegalArgumentException("Unexpected end of input, expected " + expectedType);
        }
        JsonToken token = tokens.get(position++);
        if (token.type() != expectedType) {
            throw new IllegalArgumentException("Expected " + expectedType + " but found " + token.type());
        }
        return token;
    }

    private JsonToken peek() throws Exception {
        if (position >= tokens.size()) {
            throw new IllegalArgumentException("Unexpected end of input");
        }
        return tokens.get(position);
    }

    public <T> T parseByKey(String key, Class<T> type) throws Exception {
        Object result = parse();
        Object value = findValueByKey(result, key);
        if (value == null) {
            throw new IllegalArgumentException("Key not found: " + key);
        }
        return convertToType(value, type);
    }

    private <T> T convertToType(Object value, Class<T> type) throws Exception {
        if (type.isInstance(value)) {
            return type.cast(value);
        } else if (value instanceof Number) {
            return convertNumberToType((Number) value, type);
        } else if (value instanceof String && type == String.class) {
            return type.cast(value);
        } else if (value instanceof Boolean && (type == Boolean.class || type == boolean.class)) {
            return type.cast(value);
        }
        throw new ClassCastException("Cannot cast the object of type " + value.getClass().getSimpleName() + " to " + type.getSimpleName());
    }

    private <T> T convertNumberToType(Number number, Class<T> type) {
        if (type == Integer.class || type == int.class) {
            return type.cast((int) number.doubleValue());
        } else if (type == Double.class || type == double.class) {
            return type.cast(number.doubleValue());
        } else if (type == Float.class || type == float.class) {
            return type.cast(number.floatValue());
        } else if (type == Long.class || type == long.class) {
            return type.cast(number.longValue());
        } else if (type == Short.class || type == short.class) {
            return type.cast((short) number.doubleValue());
        } else if (type == Byte.class || type == byte.class) {
            return type.cast((byte) number.doubleValue());
        }
        throw new IllegalArgumentException("Unsupported number conversion to " + type.getSimpleName());
    }

    private Object findValueByKey(Object current, String key) {
        if (current instanceof Map<?, ?> map) {
            if (map.containsKey(key)) {
                return map.get(key);
            }
            for (Object value : map.values()) {
                Object found = findValueByKey(value, key);
                if (found != null) return found;
            }
        } else if (current instanceof List<?>) {
            for (Object item : (List<?>) current) {
                Object found = findValueByKey(item, key);
                if (found != null) return found;
            }
        }
        return null;
    }
}
