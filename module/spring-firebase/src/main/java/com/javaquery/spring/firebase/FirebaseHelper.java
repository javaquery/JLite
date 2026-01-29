package com.javaquery.spring.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.javaquery.util.Is;
import java.util.concurrent.TimeUnit;

/**
 * Firebase helper class for token verification and claim extraction.
 *
 * @author vicky.thakor
 * @since 1.0.0
 */
public class FirebaseHelper {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String PHONE_NUMBER_CLAIM = "phone_number";
    private static final String EMAIL_CLAIM = "email";

    /**
     * Verify the Firebase ID token from the bearer token.
     *
     * @param bearerToken the bearer token containing the Firebase ID token
     * @return the decoded FirebaseToken
     * @throws Exception if token verification fails or token is invalid
     */
    public static FirebaseToken getFirebaseToken(String bearerToken) throws Exception {
        if (Is.nonNullNonEmpty(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            String token = bearerToken.substring(7);
            return FirebaseAuth.getInstance().verifyIdTokenAsync(token).get(3, TimeUnit.SECONDS); // timeout
        }
        throw new RuntimeException(ExceptionMessage.UNAUTHORIZED_ACCESS);
    }

    /**
     * Extract the mobile number from the decoded FirebaseToken.
     *
     * @param decodedToken the decoded FirebaseToken
     * @return the mobile number if present, otherwise null
     */
    public static String getMobileNumber(FirebaseToken decodedToken) {
        Object phoneNumber = decodedToken.getClaims().get(PHONE_NUMBER_CLAIM);
        if (Is.nonNull(phoneNumber)) {
            return phoneNumber.toString();
        }
        return null;
    }

    /**
     * Extract the email from the decoded FirebaseToken.
     *
     * @param decodedToken the decoded FirebaseToken
     * @return the email if present, otherwise null
     */
    public static String getEmail(FirebaseToken decodedToken) {
        Object email = decodedToken.getClaims().get(EMAIL_CLAIM);
        if (Is.nonNull(email)) {
            return email.toString();
        }
        return null;
    }
}
