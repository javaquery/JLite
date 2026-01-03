package com.javaquery.spring.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import javax.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

/**
 * @author vicky.thakor
 * @since 1.0.0
 */
@Configuration
public class FirebaseConfiguration {

    private final GoogleCredentials googleCredentials;

    public FirebaseConfiguration(GoogleCredentials googleCredentials) {
        this.googleCredentials = googleCredentials;
    }

    @PostConstruct
    public void initialize() {
        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseOptions.Builder optionsBuilder = FirebaseOptions.builder().setCredentials(googleCredentials);
            FirebaseApp.initializeApp(optionsBuilder.build());
        }
    }
}
