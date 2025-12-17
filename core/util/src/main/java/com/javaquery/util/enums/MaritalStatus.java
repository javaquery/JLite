package com.javaquery.util.enums;

import com.javaquery.util.Is;

/**
 * @author vicky.thakor
 * @since 1.2.8
 */
public enum MaritalStatus {
    SINGLE,
    MARRIED,
    DIVORCED,
    WIDOWED,
    SEPARATED,
    UNKNOWN;

    public static MaritalStatus fromString(String value) {
        if (Is.nullOrEmpty(value)) return null;
        try {
            return MaritalStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
