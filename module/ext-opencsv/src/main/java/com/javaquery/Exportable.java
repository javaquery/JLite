package com.javaquery;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a field is exportable with a specified key.
 * @author vicky.thakor
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Exportable {
    /**
     * The key to be used for exporting the field.
     * @return the export key
     */
    String key();
}
