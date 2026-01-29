package com.javaquery.spring.firebase.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.javaquery.spring.firebase.FirebaseService;
import com.javaquery.spring.firebase.FirestoreService;
import org.junit.jupiter.api.Test;

/**
 * Test for FirebaseAutoConfiguration to verify auto-configuration setup.
 *
 * @author vicky.thakor
 * @since 1.0.0
 */
class FirebaseAutoConfigurationTest {

    @Test
    void shouldHaveAutoConfigurationClass() {
        assertThat(FirebaseAutoConfiguration.class).isNotNull();
    }

    @Test
    void shouldHaveFirebaseServiceBeanMethod() throws NoSuchMethodException {
        assertThat(FirebaseAutoConfiguration.class.getMethod("firebaseService")).isNotNull();
    }

    @Test
    void shouldHaveFirestoreServiceBeanMethod() throws NoSuchMethodException {
        assertThat(FirebaseAutoConfiguration.class.getMethod("firestoreService"))
                .isNotNull();
    }

    @Test
    void shouldCreateFirebaseServiceInstance() {
        FirebaseService service = new FirebaseService();
        assertThat(service).isNotNull();
    }

    @Test
    void shouldCreateFirestoreServiceInstance() {
        FirestoreService service = new FirestoreService();
        assertThat(service).isNotNull();
    }
}
