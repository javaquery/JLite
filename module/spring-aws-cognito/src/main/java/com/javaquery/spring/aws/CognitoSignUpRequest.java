package com.javaquery.spring.aws;

import java.util.Map;

/**
 * Implement this interface to create a Cognito Register Request.s
 * @author vicky.thakor
 * @since 1.0.0
 */
public interface CognitoSignUpRequest {
    /**
     * Username for sign up.
     * @return username
     */
    String getUsername();

    /**
     * Password for sign up.
     * @return password
     */
    String getPassword();

    /**
     * User's email.
     * @return email
     */
    String getEmail();

    /**
     * User's phone number.
     * @return phone number
     */
    String getPhoneNumber();

    /**
     * User's full name.
     * @return name
     */
    String getName();

    /**
     * Set additional attributes for the user.
     * @see <a href="https://docs.aws.amazon.com/cognito/latest/developerguide/user-pool-settings-attributes.html">Cognito User Pool Attributes</a>
     * @return attributes
     */
    Map<String, String> getAttributes();
}
