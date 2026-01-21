package com.javaquery.spring.aws;

/**
 * Implement this interface to create a Cognito Change Password Request.
 * @author vicky.thakor
 * @since 1.0.0
 */
public interface CognitoChangePasswordRequest {
    /**
     * Access token of the user who wants to change the password
     * @return access token
     */
    String getAccessToken();

    /**
     * Previous password of the user
     * @return previous password
     */
    String getPreviousPassword();

    /**
     * Proposed new password of the user
     * @return proposed new password
     */
    String getProposedPassword();
}
