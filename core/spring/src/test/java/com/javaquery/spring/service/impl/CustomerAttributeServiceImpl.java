package com.javaquery.spring.service.impl;

import com.javaquery.spring.model.CustomerAttribute;
import com.javaquery.spring.service.AbstractService;
import com.javaquery.spring.service.CustomerAttributeService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

/**
 * @author vicky.thakor
 * @since 2025-12-19
 */
@Service
public class CustomerAttributeServiceImpl extends AbstractService<CustomerAttribute, Long>
        implements CustomerAttributeService {

    public CustomerAttributeServiceImpl(
            JpaRepository<CustomerAttribute, Long> repository, ApplicationEventPublisher applicationEventPublisher) {
        super(repository, applicationEventPublisher);
    }
}
