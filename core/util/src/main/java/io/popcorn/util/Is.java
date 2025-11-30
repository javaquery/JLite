package io.popcorn.util;

import io.popcorn.util.collection.Collections;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * Wrapper class for {@link Objects}, {@link Strings} and {@link Collections} utility classes.
 * @author vicky.thakor
 * @since 1.0.0
 */
public class Is {

    /**
     * Returns {@code true} if the provided reference is {@code null} otherwise returns {@code false}.
     *
     * @param obj a reference to be checked against {@code null}
     * @return {@code true} if the provided reference is {@code null} otherwise {@code false}
     */
    public static boolean isNull(Object obj) {
        return Objects.isNull(obj);
    }

    /**
     * Execute code if the provided reference is {@code null}.
     *
     * @param obj a reference to be checked against {@code null}
     * @param executableFunction lambda function given executed if the provided reference is {@code null}.
     */
    public static void isNull(Object obj, ExecutableFunction executableFunction){
        if(isNull(obj)){
            executableFunction.execute();
        }
    }

    /**
     * Returns {@code true} if the provided reference is non-{@code null} otherwise returns {@code
     * false}.
     *
     * @param obj a reference to be checked against {@code null}
     * @return {@code true} if the provided reference is non-{@code null} otherwise {@code false}
     */
    public static boolean nonNull(Object obj) {
        return Objects.nonNull(obj);
    }

    /**
     * Execute code if the provided reference is non-{@code null}.
     *
     * @param obj a reference to be checked against {@code null}
     * @param executableFunction lambda function given executed if the provided reference is non-{@code null}.
     */
    public static void nonNull(Object obj, ExecutableFunction executableFunction){
        if(nonNull(obj)){
            executableFunction.execute();
        }
    }

    /**
     * Returns {@code true} if the provided String is {@code null} or empty otherwise returns {@code
     * false}.
     *
     * @param str a String to be checked against {@code null} or empty
     * @return {@code true} if the provided String is {@code null} or empty otherwise {@code false}
     */
    public static boolean nullOrEmpty(String str) {
        return Strings.nullOrEmpty(str);
    }

    /**
     * Execute code if the provided String is {@code null} or empty.
     * @param str a String to be checked against {@code null} or empty
     * @param executableFunction lambda function given executed if the provided String is {@code null} or empty.
     */
    public static void nullOrEmpty(String str, ExecutableFunction executableFunction){
        Strings.nullOrEmpty(str, executableFunction);
    }

    /**
     * Checks if the provided String is {@code null} or empty.
     * @param str a String to be checked against {@code null} or empty
     * @param defaultValue - if the provided String is {@code null} or empty then this value will be returned.
     * @return provided String if non-{@code null} and non-empty otherwise defaultValue
     */
    public static String nullOrEmpty(String str, String defaultValue){
        return Strings.nullOrEmpty(str, defaultValue);
    }

    /**
     * Returns {@code true} if the provided String is non-{@code null} and non-empty otherwise returns
     * {@code false}.
     *
     * @param str a String to be checked against non-{@code null} and non-empty
     * @return {@code true} if the provided String is non-{@code null} and non-empty otherwise {@code
     *     false}
     */
    public static boolean nonNullNonEmpty(String str) {
        return Strings.nonNullNonEmpty(str);
    }

    /**
     * Execute code if the provided String is non-{@code null} and non-empty.
     * @param str a String to be checked against non-{@code null} and non-empty
     * @param executableFunction lambda function given executed if the provided String is non-{@code null} and non-empty.
     */
    public static void nonNullNonEmpty(String str, ExecutableFunction executableFunction) {
        Strings.nonNullNonEmpty(str, executableFunction);
    }

    /**
     * Checks if the provided String is non-{@code null} and non-empty.
     * @param str a String to be checked against non-{@code null} and non-empty
     * @param defaultValue - if the provided String is non-{@code null} and non-empty then this value will be returned.
     * @return provided String if non-{@code null} and non-empty otherwise defaultValue
     */
    public static String nonNullNonEmpty(String str, String defaultValue){
        return Strings.nonNullNonEmpty(str, defaultValue);
    }

    /**
     * Returns {@code true} if the provided Collection [List, Set] is {@code null} or empty otherwise
     * returns {@code false}.
     *
     * @param collection a Collection [List, Set] to be checked against {@code null} or empty
     * @return {@code true} if the provided Collection [List, Set] is {@code null} or empty otherwise
     *     {@code false}
     */
    public static boolean nullOrEmpty(Collection<?> collection) {
        return Collections.nullOrEmpty(collection);
    }

    /**
     * Execute code if the provided Collection [List, Set] is {@code null} or empty.
     * @param collection a Collection [List, Set] to be checked against {@code null} or empty
     * @param executableFunction lambda function given executed if the provided Collection [List, Set] is {@code null} or empty.
     */
    public static void nullOrEmpty(Collection<?> collection, ExecutableFunction executableFunction) {
        Collections.nullOrEmpty(collection, executableFunction);
    }

    /**
     * Returns {@code true} if the provided Collection [List, Set] is non-{@code null} and non-empty
     * otherwise returns {@code false}.
     *
     * @param collection a Collection [List, Set] to be checked against non-{@code null} and non-empty
     * @return {@code true} if the provided Collection [List, Set] is non-{@code null} and non-empty
     *     otherwise {@code false}
     */
    public static boolean nonNullNonEmpty(Collection<?> collection) {
        return Collections.nonNullNonEmpty(collection);
    }

    /**
     * Execute code if the provided Collection [List, Set] is non-{@code null} and non-empty.
     * @param collection collection a Collection [List, Set] to be checked against non-{@code null} and non-empty
     * @param executableFunction lambda function given executed if the provided Collection [List, Set] is non-{@code null} and non-empty.
     */
    public static void nonNullNonEmpty(Collection<?> collection, ExecutableFunction executableFunction){
        Collections.nonNullNonEmpty(collection, executableFunction);
    }

    /**
     * Returns {@code true} if the provided Map is {@code null} or empty otherwise returns {@code
     * false}.
     *
     * @param map a Map to be checked against {@code null} or empty
     * @return {@code true} if the provided Map is {@code null} and empty otherwise * returns {@code
     *     false}
     */
    public static boolean nullOrEmpty(Map<?, ?> map) {
        return Collections.nullOrEmpty(map);
    }

    /**
     * Execute code if the provided Map is {@code null} or empty
     * @param map a Map to be checked against {@code null} or empty
     * @param executableFunction lambda function given executed if the provided Map is {@code null} or empty
     */
    public static void nullOrEmpty(Map<?, ?> map, ExecutableFunction executableFunction){
        Collections.nullOrEmpty(map, executableFunction);
    }

    /**
     * Returns {@code true} if the provided Map is non-{@code null} and non-empty otherwise returns
     * {@code false}.
     *
     * @param map a Map to be checked against non-{@code null} and non-empty
     * @return {@code true} if the provided Map is non-{@code null} and non-empty otherwise {@code
     *     false}
     */
    public static boolean nonNullNonEmpty(Map<?, ?> map) {
        return Collections.nonNullNonEmpty(map);
    }

    /**
     * Execute code if the provided Map is non-{@code null} and non-empty
     * @param map  a Map to be checked against non-{@code null} and non-empty
     * @param executableFunction lambda function given executed if the provided Map is non-{@code null} and non-empty
     */
    public static void nonNullNonEmpty(Map<?, ?> map, ExecutableFunction executableFunction){
        Collections.nonNullNonEmpty(map, executableFunction);
    }
}
