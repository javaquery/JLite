package com.javaquery.spring.service;

import com.javaquery.spring.model.Customer;

/**
 * @author vicky.thakor
 * @since 1.0.0
 */
public interface CustomerService extends IAbstractService<Customer, Long> {
    void deleteAll();
}
