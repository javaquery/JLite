package com.javaquery.spring.aws;

/**
 * Implement this interface to create a Cognito Login Request.
 * @author vicky.thakor
 * @since 1.0.0
 */
public interface CognitoLoginRequest {
    /**
     * Username of the user trying to log in
     * @return username
     */
    String getUsername();

    /**
     * Password of the user trying to log in
     * @return password
     */
    String getPassword();
}
