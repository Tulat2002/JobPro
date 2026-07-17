package com.devanktu.jobpro.utils;


public class SlugUtils {

    public static String toSlug(String input) {
        if (input == null) return null;
        return input.trim()
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-");
    }
}
