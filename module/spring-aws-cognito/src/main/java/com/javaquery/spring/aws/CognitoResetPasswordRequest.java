package com.javaquery.spring.aws;

/**
 * Implement this interface to create a Cognito Reset Password Request.
 * @author vicky.thakor
 * @since 1.0.0
 */
public interface CognitoResetPasswordRequest {
    /**
     * Username of the user who wants to reset the password
     * @return username
     */
    String getUsername();

    /**
     * Confirmation code sent to the user for password reset
     * @return confirmation code
     */
    String getConfirmationCode();

    /**
     * New password to be set for the user
     * @return new password
     */
    String getNewPassword();
}
