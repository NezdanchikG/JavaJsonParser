package com.Nezdanchik.spbpu;

import java.util.ArrayList;
import java.util.List;

public class JsonTokenizer {
    private final String input;
    private int position = 0;

    public JsonTokenizer(String input) {
        this.input = input.trim();
    }

    public List<JsonToken> tokenize() throws Exception {
        List<JsonToken> tokens = new ArrayList<>();
        while (position < input.length()) {
            char current = input.charAt(position);
            switch (current) {
                case '{':
                    tokens.add(new JsonToken(JsonToken.Type.BracketObjectLeft, "{"));
                    position++;
                    break;
                case '}':
                    tokens.add(new JsonToken(JsonToken.Type.BracketObjectRight, "}"));
                    position++;
                    break;
                case '[':
                    tokens.add(new JsonToken(JsonToken.Type.BracketArrayLeft, "["));
                    position++;
                    break;
                case ']':
                    tokens.add(new JsonToken(JsonToken.Type.BracketArrayRight, "]"));
                    position++;
                    break;
                case ',':
                    tokens.add(new JsonToken(JsonToken.Type.Comma, ","));
                    position++;
                    break;
                case ':':
                    tokens.add(new JsonToken(JsonToken.Type.Colon, ":"));
                    position++;
                    break;
                case '"':
                    tokens.add(parseString());
                    break;
                case 't':
                case 'f':
                    tokens.add(parseBoolean());
                    break;
                case 'n':
                    tokens.add(parseNull());
                    break;
                default:
                    if (Character.isDigit(current) || current == '-') {
                        tokens.add(parseNumber());
                    } else if (Character.isWhitespace(current)) {
                        position++;
                    } else {
                        throw new IllegalArgumentException("Unexpected character: " + current);
                    }
                    break;
            }
        }
        return tokens;
    }

    private JsonToken parseString() {
        StringBuilder sb = new StringBuilder();
        position++;  // Пропустить начальные кавычки
        while (position < input.length()) {
            char current = input.charAt(position);
            if (current == '"') {
                position++;  // Пропустить закрывающие кавычки
                return new JsonToken(JsonToken.Type.String, sb.toString());
            } else if (current == '\\') {
                position++;
                if (position < input.length()) {
                    current = input.charAt(position);
                    switch (current) {
                        case 'n': sb.append('\n'); break;
                        case 't': sb.append('\t'); break;
                        case 'r': sb.append('\r'); break;
                        case 'b': sb.append('\b'); break;
                        case 'f': sb.append('\f'); break;
                        case '"': sb.append('\"'); break;
                        case '\\': sb.append('\\'); break;
                        default: sb.append(current); break;
                    }
                }
            } else {
                sb.append(current);
            }
            position++;
        }
        throw new IllegalArgumentException("Unterminated string");
    }


    private JsonToken parseNumber() {
        StringBuilder sb = new StringBuilder();
        while (position < input.length() && (Character.isDigit(input.charAt(position)) || input.charAt(position) == '.' || input.charAt(position) == '-')) {
            sb.append(input.charAt(position));
            position++;
        }
        return new JsonToken(JsonToken.Type.Number, sb.toString());
    }

    private JsonToken parseBoolean() throws Exception {
        if (input.startsWith("true", position)) {
            position += 4;
            return new JsonToken(JsonToken.Type.True, "true");
        } else if (input.startsWith("false", position)) {
            position += 5;
            return new JsonToken(JsonToken.Type.False, "false");
        } else {
            throw new IllegalArgumentException("Invalid boolean value");
        }
    }

    private JsonToken parseNull() throws Exception {
        if (input.startsWith("null", position)) {
            position += 4;
            return new JsonToken(JsonToken.Type.Null, "null");
        } else {
            throw new IllegalArgumentException("Invalid null value");
        }
    }
}
