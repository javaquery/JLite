package com.javaquery.spring.repository;

import com.javaquery.spring.model.CustomerAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author vicky.thakor
 * @since 1.0.0
 */
@Repository
public interface CustomerAttributeRepository extends JpaRepository<CustomerAttribute, Long> {}
