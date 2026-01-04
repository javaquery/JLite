package com.javaquery.spring.firebase;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Test configuration for Spring Boot test context
 *
 * @author vicky.thakor
 * @since 1.0.0
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.javaquery.spring.firebase")
public class TestConfiguration {}
