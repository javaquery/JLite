package com.javaquery.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a field is exportable with a specified key.
 * @author vicky.thakor
 * @since 1.2.8
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Exportable {
    /**
     * The key to be used for exporting the field.
     * @return the export key
     */
    String key();

    /**
     * Indicates if the field should be treated as a formula.
     * @return true if the field is a formula, false otherwise
     */
    boolean isFormula() default false;

    /**
     * Indicates if the field contains rich text.
     * @return true if the field is rich text, false otherwise
     */
    boolean isRichText() default false;
}
