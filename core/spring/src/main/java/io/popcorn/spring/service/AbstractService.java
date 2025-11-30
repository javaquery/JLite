package io.popcorn.spring.service;

import io.popcorn.spring.data.PageData;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.function.Supplier;

/**
 * Abstract service class providing common CRUD operations.
 *
 * @author vicky.thakor
 * @since 1.0.0
 */
public abstract class AbstractService<T, ID> {

    protected final JpaRepository<T, ID> repository;
    protected final JpaSpecificationExecutor<T> specificationExecutor;
    protected final ApplicationEventPublisher applicationEventPublisher;

    protected AbstractService(JpaRepository<T, ID> repository, ApplicationEventPublisher applicationEventPublisher) {
        this.repository = repository;
        this.applicationEventPublisher = applicationEventPublisher;
        if (repository instanceof JpaSpecificationExecutor) {
            this.specificationExecutor = (JpaSpecificationExecutor<T>) repository;
        } else {
            this.specificationExecutor = null;
        }
    }

    /**
     * Saves a given entity.
     *
     * @param entity the entity to save
     * @return the saved entity
     */
    public T save(T entity) {
        return repository.save(entity);
    }

    /**
     * Saves all given entities.
     *
     * @param entities the entities to save
     * @return the saved entities
     */
    public <S extends T> List<S> saveAll(Iterable<S> entities) {
        return repository.saveAll(entities);
    }

    /**
     * Finds an entity by its ID.
     * - throwExceptionIfNotFound: If provided, the supplier will be used to throw an exception if the entity is not found.
     *
     * @param id the ID of the entity
     * @param throwExceptionIfNotFound a supplier for the exception to be thrown if the entity is not found
     * @return the found entity or null if not found and no exception supplier is provided
     */
    public T findById(ID id, Supplier<? extends RuntimeException> throwExceptionIfNotFound) {
        if(throwExceptionIfNotFound == null) {
            return repository.findById(id).orElse(null);
        }
        return repository.findById(id).orElseThrow(throwExceptionIfNotFound);
    }

    /**
     * Deletes an entity by its ID.
     * - throwExceptionIfNotFound: If provided, the supplier will be used to throw an exception if the entity is not found.
     *
     * @param id the ID of the entity to delete
     * @param throwExceptionIfNotFound a supplier for the exception to be thrown if the entity is not found
     * @return the deleted entity
     */
    public T deleteById(ID id, Supplier<? extends RuntimeException> throwExceptionIfNotFound) {
        T entity = findById(id, throwExceptionIfNotFound);
        repository.deleteById(id);
        return entity;
    }

    /**
     * Deletes a given entity.
     *
     * @param entity the entity to delete
     */
    public void delete(T entity) {
        repository.delete(entity);
    }

    /**
     * Checks if an entity exists by its ID.
     * - throwExceptionIfNotFound: If provided, the supplier will be used to throw an exception if the entity does not exist.
     *
     * @param id the ID of the entity
     * @param throwExceptionIfNotFound a supplier for the exception to be thrown if the entity does not exist
     * @return true if the entity exists, false otherwise
     */
    public boolean existsById(ID id, Supplier<? extends RuntimeException> throwExceptionIfNotFound) {
        boolean exists = repository.existsById(id);
        if (!exists && throwExceptionIfNotFound != null) {
            throw throwExceptionIfNotFound.get();
        }
        return exists;
    }

    /**
     * Finds all entities by their IDs.
     *
     * @param ids the IDs of the entities
     * @return the found entities
     */
    public List<T> findAllById(Iterable<ID> ids) {
        return repository.findAllById(ids);
    }

    /**
     * Finds all entities matching the given specification with pagination.
     *
     * @param specification the specification to filter entities
     * @param pageable the pagination information
     * @return a PageData object containing the paginated results
     */
    public PageData<T> findAll(Specification<T> specification, Pageable pageable){
        if(specificationExecutor == null){
            throw new UnsupportedOperationException("Repository does not support Specifications.");
        }
        var page = specificationExecutor.findAll(specification, pageable);
        return new PageData<>(page.getTotalElements(), page.getTotalPages(), page.getNumber(), page.getSize(), page.getContent());
    }

    /**
     * Finds all entities with pagination.
     *
     * @param pageable the pagination information
     * @return a PageData object containing the paginated results
     */
    public PageData<T> findAll(Pageable pageable){
        var page = repository.findAll(pageable);
        return new PageData<>(page.getTotalElements(), page.getTotalPages(), page.getNumber(), page.getSize(), page.getContent());
    }

    /**
     * Counts the total number of entities.
     *
     * @return the total number of entities
     */
    public long count() {
        return repository.count();
    }
}
