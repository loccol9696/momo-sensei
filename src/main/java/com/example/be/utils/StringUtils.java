package com.example.be.utils;

public class StringUtils {

    public static String normalize(String source) {
        if (source == null) return "";
        return source.trim().toLowerCase().replaceAll("\\s+", " ");
    }

    public static boolean isValidString(String str) {
        return str != null && !str.isBlank();
    }
}
