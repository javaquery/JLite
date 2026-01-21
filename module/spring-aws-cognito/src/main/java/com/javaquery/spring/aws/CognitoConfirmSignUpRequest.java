package com.javaquery.spring.aws;

/**
 * Implement this interface to create a Cognito Confirm Sign Up Request.
 * @author vicky.thakor
 * @since 1.0.0
 */
public interface CognitoConfirmSignUpRequest {
    /**
     * Username of the user who wants to confirm sign up
     * @return username
     */
    String getUsername();
    /**
     * Confirmation code sent to the user for sign up confirmation
     * @return confirmation code
     */
    String getConfirmationCode();
}
