package com.Nezdanchik.spbpu;

public record JsonToken(com.Nezdanchik.spbpu.JsonToken.Type type, String value) {

    public enum Type {
        Number,
        String,
        Null,
        False,
        True,
        BracketObjectLeft,
        BracketObjectRight,
        BracketArrayLeft,
        BracketArrayRight,
        Comma,
        Colon,
        Space
    }


}