package com.javaquery.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.javaquery.spring.repository.CustomerAttributeRepository;
import com.javaquery.spring.repository.CustomerRepository;
import com.javaquery.spring.service.CustomerAttributeService;
import com.javaquery.spring.service.CustomerService;
import com.javaquery.spring.service.ObjectMapperService;
import com.javaquery.spring.service.impl.CustomerAttributeServiceImpl;
import com.javaquery.spring.service.impl.CustomerServiceImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author vicky.thakor
 * @since 1.0.0
 */
@Configuration
@EnableAutoConfiguration
@EnableTransactionManagement
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "com.javaquery.spring.repository")
@EntityScan(basePackages = "com.javaquery.spring.model")
public class TestConfiguration {

    @Bean
    public CustomerService customerService(
            CustomerRepository customerRepository, ApplicationEventPublisher applicationEventPublisher) {
        return new CustomerServiceImpl(customerRepository, applicationEventPublisher);
    }

    @Bean
    public CustomerAttributeService customerAttributeService(
            CustomerAttributeRepository customerAttributeRepository,
            ApplicationEventPublisher applicationEventPublisher) {
        return new CustomerAttributeServiceImpl(customerAttributeRepository, applicationEventPublisher);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    @Qualifier("snakeCaseObjectMapper")
    public ObjectMapper snakeCaseObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        return mapper;
    }

    @Bean
    public ObjectMapperService objectMapperService(
            ObjectMapper objectMapper, @Qualifier("snakeCaseObjectMapper") ObjectMapper snakeCaseObjectMapper) {
        return new ObjectMapperService(objectMapper, snakeCaseObjectMapper);
    }
}
