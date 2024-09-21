package com.Nezdanchik.spbpu;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class JsonParserTest {

    @Test
    public void testParseEmptyObject() throws Exception {
        List<JsonToken> tokens = List.of(
                new JsonToken(JsonToken.Type.BracketObjectLeft, "{"),
                new JsonToken(JsonToken.Type.BracketObjectRight, "}")
        );
        JsonParser parser = new JsonParser(tokens);
        Map<String, Object> result = (Map<String, Object>) parser.parse();
        assertTrue(result.isEmpty());
    }

    @Test
    public void testParseSimpleObject() throws Exception {
        List<JsonToken> tokens = List.of(
                new JsonToken(JsonToken.Type.BracketObjectLeft, "{"),
                new JsonToken(JsonToken.Type.String, "key"),
                new JsonToken(JsonToken.Type.Colon, ":"),
                new JsonToken(JsonToken.Type.String, "value"),
                new JsonToken(JsonToken.Type.BracketObjectRight, "}")
        );
        JsonParser parser = new JsonParser(tokens);
        Map<String, Object> result = (Map<String, Object>) parser.parse();
        assertEquals("value", result.get("key"));
    }

    @Test
    public void testParseArray() throws Exception {
        List<JsonToken> tokens = List.of(
                new JsonToken(JsonToken.Type.BracketArrayLeft, "["),
                new JsonToken(JsonToken.Type.Number, "1"),
                new JsonToken(JsonToken.Type.Comma, ","),
                new JsonToken(JsonToken.Type.Number, "2"),
                new JsonToken(JsonToken.Type.BracketArrayRight, "]")
        );
        JsonParser parser = new JsonParser(tokens);
        List<Object> result = (List<Object>) parser.parseArray();
        assertEquals(2, result.size());
        assertEquals(1, result.get(0));
        assertEquals(2, result.get(1));
    }

    @Test
    public void testParseNestedObject() throws Exception {
        List<JsonToken> tokens = List.of(
                new JsonToken(JsonToken.Type.BracketObjectLeft, "{"),
                new JsonToken(JsonToken.Type.String, "key"),
                new JsonToken(JsonToken.Type.Colon, ":"),
                new JsonToken(JsonToken.Type.BracketObjectLeft, "{"),
                new JsonToken(JsonToken.Type.String, "nestedKey"),
                new JsonToken(JsonToken.Type.Colon, ":"),
                new JsonToken(JsonToken.Type.Number, "123"),
                new JsonToken(JsonToken.Type.BracketObjectRight, "}"),
                new JsonToken(JsonToken.Type.BracketObjectRight, "}")
        );
        JsonParser parser = new JsonParser(tokens);
        Map<String, Object> result = (Map<String, Object>) parser.parse();
        Map<String, Object> nested = (Map<String, Object>) result.get("key");
        assertEquals(123, nested.get("nestedKey"));
    }

    @Test
    public void testParseBooleanAndNullValues() throws Exception {
        List<JsonToken> tokens = List.of(
                new JsonToken(JsonToken.Type.BracketObjectLeft, "{"),
                new JsonToken(JsonToken.Type.String, "key1"),
                new JsonToken(JsonToken.Type.Colon, ":"),
                new JsonToken(JsonToken.Type.True, "true"),
                new JsonToken(JsonToken.Type.Comma, ","),
                new JsonToken(JsonToken.Type.String, "key2"),
                new JsonToken(JsonToken.Type.Colon, ":"),
                new JsonToken(JsonToken.Type.Null, "null"),
                new JsonToken(JsonToken.Type.BracketObjectRight, "}")
        );

        JsonParser parser = new JsonParser(tokens);
        Map<String, Object> result = (Map<String, Object>) parser.parse();
        assertEquals(true, result.get("key1"));
        assertNull(result.get("key2"));
    }

    @Test
    public void testParseInvalidJson() {
        List<JsonToken> tokens = List.of(
                new JsonToken(JsonToken.Type.BracketObjectLeft, "{"),
                new JsonToken(JsonToken.Type.String, "key")
                // Отсутствует двоеточие и значение
        );
        JsonParser parser = new JsonParser(tokens);
        Exception exception = assertThrows(IllegalArgumentException.class, parser::parse);
        assertEquals("Unexpected end of input, expected Colon", exception.getMessage());
    }
}

