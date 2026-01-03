package com.javaquery.spring.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.javaquery.util.Is;
import java.util.concurrent.TimeUnit;

/**
 * @author vicky.thakor
 * @since 1.0.0
 */
public class FirebaseHelper {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String PHONE_NUMBER_CLAIM = "phone_number";
    private static final String EMAIL_CLAIM = "email";

    public static FirebaseToken getFirebaseToken(String bearerToken) throws Exception {
        if (Is.nonNullNonEmpty(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            String token = bearerToken.substring(7);
            return FirebaseAuth.getInstance().verifyIdTokenAsync(token).get(3, TimeUnit.SECONDS); // timeout
        }
        throw new RuntimeException(ExceptionMessage.UNAUTHORIZED_ACCESS);
    }

    public static String getMobileNumber(FirebaseToken decodedToken) {
        Object phoneNumber = decodedToken.getClaims().get(PHONE_NUMBER_CLAIM);
        if (Is.nonNull(phoneNumber)) {
            return phoneNumber.toString();
        }
        return null;
    }

    public static String getEmail(FirebaseToken decodedToken) {
        Object email = decodedToken.getClaims().get(EMAIL_CLAIM);
        if (Is.nonNull(email)) {
            return email.toString();
        }
        return null;
    }
}
