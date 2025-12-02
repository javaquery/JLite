package io.popcorn.util;

import io.popcorn.util.collection.Collections;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * {@code Assert} is a collection of utility methods that helps asserting conditions and throw
 * exception if condition fails
 *
 * @author Harshit
 * @since 1.0.0
 */
public final class Assert {

    private Assert() {}

    /**
     * <em>Assert</em> that {@code object} is {@code not null}.
     *
     * @param object object to check
     * @param <T> Type of the exception to be thrown
     * @param exceptionSupplier supplier of a exception to be thrown
     * @throws T if {@code object} is {@code null}
     * @throws NullPointerException if {@code object} is {@code null} and {@code exceptionSupplier} is
     *     {@code null}
     */
    public static <T extends Throwable> void nonNull(Object object, Supplier<T> exceptionSupplier) throws T {
        if (Objects.isNull(object)) {
            throw exceptionSupplier.get();
        }
    }

    /**
     * <em>Assert</em> that {@code object} is {@code null}.
     *
     * @param object object to check
     * @param <T> Type of the exception to be thrown
     * @param exceptionSupplier supplier of a exception to throw
     * @throws T if {@code object} is {@code not null}
     * @throws NullPointerException if {@code object} is {@code not null} and {@code
     *     exceptionSupplier} is {@code null}
     */
    public static <T extends Throwable> void isNull(Object object, Supplier<T> exceptionSupplier) throws T {
        if (java.util.Objects.nonNull(object)) {
            throw exceptionSupplier.get();
        }
    }

    /**
     * <em>Assert</em> that {@code expression} is {@code true}.
     *
     * @param expression expression to check
     * @param <T> Type of the exception to be thrown
     * @param exceptionSupplier supplier of a exception to throw
     * @throws T if {@code expression} is {@code false}
     * @throws NullPointerException if {@code expression} is {@code false} and {@code
     *     exceptionSupplier} is {@code null}
     */
    public static <T extends Throwable> void isTrue(boolean expression, Supplier<T> exceptionSupplier) throws T {
        if (!expression) {
            throw exceptionSupplier.get();
        }
    }

    /**
     * <em>Assert</em> that {@code expression} is {@code false}.
     *
     * @param expression expression to check
     * @param <T> Type of the exception to be thrown
     * @param exceptionSupplier supplier of a exception to throw
     * @throws T if {@code expression} is {@code true}
     * @throws NullPointerException if {@code expression} is {@code true} and {@code
     *     exceptionSupplier} is {@code null}
     */
    public static <T extends Throwable> void isFalse(boolean expression, Supplier<T> exceptionSupplier) throws T {
        if (expression) {
            throw exceptionSupplier.get();
        }
    }

    /**
     * <em>Assert</em> that {@code collection} is {@code not null} and {@code not empty}.
     *
     * @param collection collection to check
     * @param <T> Type of the exception to be thrown
     * @param exceptionSupplier supplier of a exception to throw
     * @throws T if {@code collection} is {@code null} or {@code empty}
     * @throws NullPointerException if {@code collection} is {@code null} or {@code empty} and {@code
     *     exceptionSupplier} is {@code null}
     */
    public static <T extends Throwable> void nonNullNonEmpty(Collection<?> collection, Supplier<T> exceptionSupplier)
            throws T {
        if (io.popcorn.util.collection.Collections.nullOrEmpty(collection)) {
            throw exceptionSupplier.get();
        }
    }

    /**
     * <em>Assert</em> that {@code collection} is {@code null} or {@code empty}.
     *
     * @param collection collection to check
     * @param <T> Type of the exception to be thrown
     * @param exceptionSupplier supplier of a exception to throw
     * @throws T if {@code collection} is {@code not null} and {@code not empty}
     * @throws NullPointerException if {@code collection} is {@code not null} and {@code not empty}
     *     and {@code exceptionSupplier} is {@code null}
     */
    public static <T extends Throwable> void nullOrEmpty(Collection<?> collection, Supplier<T> exceptionSupplier)
            throws T {
        if (Collections.nonNullNonEmpty(collection)) {
            throw exceptionSupplier.get();
        }
    }

    /**
     * <em>Assert</em> that {@code map} is {@code not null} and {@code not empty}.
     *
     * @param map map to check
     * @param <T> Type of the exception to be thrown
     * @param exceptionSupplier supplier of a exception to throw
     * @throws T if {@code map} is {@code null} or {@code empty}
     * @throws NullPointerException if {@code map} is {@code null} or {@code empty} and {@code
     *     exceptionSupplier} is {@code null}
     */
    public static <T extends Throwable> void nonNullNonEmptyMap(Map<?, ?> map, Supplier<T> exceptionSupplier) throws T {
        if (Collections.nullOrEmpty(map)) {
            throw exceptionSupplier.get();
        }
    }

    /**
     * <em>Assert</em> that {@code map} is {@code null} or {@code empty}.
     *
     * @param map map to check
     * @param <T> Type of the exception to be thrown
     * @param exceptionSupplier supplier of a exception to throw
     * @throws T if {@code map} is {@code not null} and {@code not empty}
     * @throws NullPointerException if {@code map} is {@code not null} and {@code not empty} and
     *     {@code exceptionSupplier} is {@code null}
     */
    public static <T extends Throwable> void nullOrEmptyMap(Map<?, ?> map, Supplier<T> exceptionSupplier) throws T {
        if (Collections.nonNullNonEmpty(map)) {
            throw exceptionSupplier.get();
        }
    }
}
