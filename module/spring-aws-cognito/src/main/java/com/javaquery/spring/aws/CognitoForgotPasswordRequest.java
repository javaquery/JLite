package com.javaquery.spring.aws;

/**
 * Implement this interface to create a Cognito Forgot Password Request.
 * @author vicky.thakor
 * @since 1.0.0
 */
public interface CognitoForgotPasswordRequest {
    /**
     * Username of the user who wants to reset the password
     * @return username
     */
    String getUsername();
}
