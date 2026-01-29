package com.javaquery.spring.firebase;

import com.javaquery.spring.firebase.config.FirebaseAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Test configuration for Spring Boot test context
 *
 * @author vicky.thakor
 * @since 1.0.0
 */
@Configuration
@EnableAutoConfiguration
@Import(FirebaseAutoConfiguration.class)
public class TestConfiguration {}
