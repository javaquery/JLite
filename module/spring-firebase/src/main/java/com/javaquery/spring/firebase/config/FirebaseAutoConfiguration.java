package com.javaquery.spring.firebase.config;

import com.google.firebase.FirebaseApp;
import com.javaquery.spring.firebase.FirebaseConfiguration;
import com.javaquery.spring.firebase.FirebaseService;
import com.javaquery.spring.firebase.FirestoreService;
import com.javaquery.spring.firebase.GoogleCloudPlatform;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Auto-configuration for Firebase services.
 * This configuration will automatically be picked up by Spring Boot when the module is on the classpath.
 *
 * @author vicky.thakor
 * @since 1.0.0
 */
@Configuration
@AutoConfiguration
@ConditionalOnClass(FirebaseApp.class)
@Import({GoogleCloudPlatform.class, FirebaseConfiguration.class})
public class FirebaseAutoConfiguration {

    /**
     * Creates a FirebaseService bean if one doesn't already exist.
     *
     * @return FirebaseService instance
     */
    @Bean
    @ConditionalOnMissingBean
    public FirebaseService firebaseService() {
        return new FirebaseService();
    }

    /**
     * Creates a FirestoreService bean if one doesn't already exist.
     *
     * @return FirestoreService instance
     */
    @Bean
    @ConditionalOnMissingBean
    public FirestoreService firestoreService() {
        return new FirestoreService();
    }
}
