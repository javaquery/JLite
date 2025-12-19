package com.javaquery.spring.service.impl;

import com.javaquery.spring.model.Customer;
import com.javaquery.spring.repository.CustomerRepository;
import com.javaquery.spring.service.AbstractService;
import com.javaquery.spring.service.CustomerService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * @author vicky.thakor
 * @since 1.0.0
 */
@Service
public class CustomerServiceImpl extends AbstractService<Customer, Long> implements CustomerService {

    public CustomerServiceImpl(CustomerRepository repository, ApplicationEventPublisher applicationEventPublisher) {
        super(repository, applicationEventPublisher);
    }

    // clear all customers, used for testing purpose
    @Override
    public void deleteAll() {
        repository.deleteAll();
    }
}
