package io.popcorn.spring.service;

import io.popcorn.spring.data.PageData;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.function.Supplier;

/**
 * @author javaquery
 * @since 1.0.0
 */
public interface IAbstractService<T, ID> {

    default T save(T entity) {
        throw new UnsupportedOperationException("Save operation is not supported.");
    }

    default <S extends T> List<S> saveAll(Iterable<S> entities){
        throw new UnsupportedOperationException("SaveAll operation is not supported.");
    }

    default T findById(ID id, Supplier<? extends RuntimeException> throwExceptionIfNotFound){
        throw new UnsupportedOperationException("FindById operation is not supported.");
    }

    default T deleteById(ID id, Supplier<? extends RuntimeException> throwExceptionIfNotFound){
        throw new UnsupportedOperationException("DeleteById operation is not supported.");
    }

    default void delete(T entity){
        throw new UnsupportedOperationException("Delete operation is not supported.");
    }

    default boolean existsById(ID id, Supplier<? extends RuntimeException> throwExceptionIfNotFound){
        throw new UnsupportedOperationException("ExistsById operation is not supported.");
    }

    default List<T> findAllById(Iterable<ID> ids){
        throw new UnsupportedOperationException("FindAllById operation is not supported.");
    }

    default PageData<T> findAll(Specification<T> specification, Pageable pageable){
        throw new UnsupportedOperationException("FindAll operation is not supported.");
    }

    default PageData<T> findAll(Pageable pageable){
        throw new UnsupportedOperationException("FindAll operation is not supported.");
    }

    default long count() {
        throw new UnsupportedOperationException("Count operation is not supported.");
    }
}
