package com.javaquery.util.enums;

import com.javaquery.util.Is;

/**
 * @author vicky.thakor
 * @since 1.2.8
 */
public enum Gender {
    MALE,
    FEMALE,
    OTHER;

    public static Gender fromString(String value) {
        if (Is.nullOrEmpty(value)) return null;
        try {
            return Gender.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
