package com.javaquery.spring.service;

import com.javaquery.spring.data.PageData;
import java.util.List;
import java.util.function.Supplier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * Abstract service interface providing common CRUD operations.
 * @author javaquery
 * @since 1.0.0
 */
public interface IAbstractService<T, ID> {

    /**
     * Saves a given entity.
     *
     * @param entity the entity to save
     * @return the saved entity
     */
    default T save(T entity) {
        throw new UnsupportedOperationException("Save operation is not supported.");
    }

    /**
     * Saves all given entities.
     *
     * @param entities the entities to save
     * @return the saved entities
     */
    default <S extends T> List<S> saveAll(Iterable<S> entities) {
        throw new UnsupportedOperationException("SaveAll operation is not supported.");
    }

    /**
     * Finds an entity by its ID.
     * - throwExceptionIfNotFound: If provided, the supplier will be used to throw an exception if the entity is not found.
     *
     * @param id the ID of the entity
     * @param throwExceptionIfNotFound a supplier for the exception to be thrown if the entity is not found
     * @return the found entity or null if not found and no exception supplier is provided
     */
    default T findById(ID id, Supplier<? extends RuntimeException> throwExceptionIfNotFound) {
        throw new UnsupportedOperationException("FindById operation is not supported.");
    }

    /**
     * Deletes an entity by its ID.
     * - throwExceptionIfNotFound: If provided, the supplier will be used to throw an exception if the entity is not found.
     *
     * @param id the ID of the entity to delete
     * @param throwExceptionIfNotFound a supplier for the exception to be thrown if the entity is not found
     * @return the deleted entity
     */
    default T deleteById(ID id, Supplier<? extends RuntimeException> throwExceptionIfNotFound) {
        throw new UnsupportedOperationException("DeleteById operation is not supported.");
    }

    /**
     * Deletes a given entity.
     *
     * @param entity the entity to delete
     */
    default void delete(T entity) {
        throw new UnsupportedOperationException("Delete operation is not supported.");
    }

    /**
     * Checks if an entity exists by its ID.
     * - throwExceptionIfNotFound: If provided, the supplier will be used to throw an exception if the entity does not exist.
     *
     * @param id the ID of the entity
     * @param throwExceptionIfNotFound a supplier for the exception to be thrown if the entity does not exist
     * @return true if the entity exists, false otherwise
     */
    default boolean existsById(ID id, Supplier<? extends RuntimeException> throwExceptionIfNotFound) {
        throw new UnsupportedOperationException("ExistsById operation is not supported.");
    }

    /**
     * Finds all entities by their IDs.
     *
     * @param ids the IDs of the entities
     * @return the found entities
     */
    default List<T> findAllById(Iterable<ID> ids) {
        throw new UnsupportedOperationException("FindAllById operation is not supported.");
    }

    /**
     * Finds all entities matching the given specification with pagination.
     *
     * @param specification the specification to filter entities
     * @param pageable the pagination information
     * @return a PageData object containing the paginated results
     */
    default PageData<T> findAll(Specification<T> specification, Pageable pageable) {
        throw new UnsupportedOperationException("FindAll operation is not supported.");
    }

    /**
     * Finds all entities matching the given specification.
     *
     * @param specification the specification to filter entities
     * @return the found entities
     */
    default List<T> findAll(Specification<T> specification) {
        throw new UnsupportedOperationException("FindAll operation is not supported.");
    }

    /**
     * Finds all entities with pagination.
     *
     * @param pageable the pagination information
     * @return a PageData object containing the paginated results
     */
    default PageData<T> findAll(Pageable pageable) {
        throw new UnsupportedOperationException("FindAll operation is not supported.");
    }

    /**
     * Counts the total number of entities.
     *
     * @return the total number of entities
     */
    default long count() {
        throw new UnsupportedOperationException("Count operation is not supported.");
    }
}
