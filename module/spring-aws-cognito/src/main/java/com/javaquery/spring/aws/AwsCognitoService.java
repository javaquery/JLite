package com.javaquery.spring.aws;

import com.javaquery.dto.AccessRefreshToken;
import com.javaquery.util.Is;
import java.nio.charset.StandardCharsets;
import java.util.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

/**
 * Service for managing AWS Cognito user operations such as registration, confirmation,
 * login, password reset, and password change.
 * @author vicky.thakor
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class AwsCognitoService {

    private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
    private final AwsCognitoProperties awsCognitoProperties;
    private final CognitoIdentityProviderClient cognitoIdentityProviderClient;

    /**
     * Registers a new user in the Cognito User Pool.
     *
     * @param request the sign-up request containing user details
     * @return the sign-up response from Cognito
     */
    public SignUpResponse registerUser(CognitoSignUpRequest request) {
        if (!awsCognitoProperties.isAllowDuplicateEmails()) {
            boolean emailExists = isEmailAlreadyRegistered(request.getEmail());
            if (emailExists) {
                throw new RuntimeException("Email is already registered");
            }
        }

        List<AttributeType> attributeTypes = new ArrayList<>(3);
        Is.nonNullNonEmpty(
                request.getEmail(),
                () -> attributeTypes.add(AttributeType.builder()
                        .name("email")
                        .value(request.getEmail())
                        .build()));
        Is.nonNullNonEmpty(
                request.getName(),
                () -> attributeTypes.add(AttributeType.builder()
                        .name("name")
                        .value(request.getName())
                        .build()));
        Is.nonNullNonEmpty(
                request.getPhoneNumber(),
                () -> attributeTypes.add(AttributeType.builder()
                        .name("phone_number")
                        .value(request.getPhoneNumber())
                        .build()));

        if (Is.nonNullNonEmpty(request.getAttributes())) {
            request.getAttributes()
                    .forEach((key, value) -> attributeTypes.add(
                            AttributeType.builder().name(key).value(value).build()));
        }

        var signUpRequestBuilder = SignUpRequest.builder()
                .clientId(awsCognitoProperties.getClientId())
                .username(request.getUsername())
                .password(request.getPassword())
                .userAttributes(attributeTypes);

        if (Is.nonNullNonEmpty(awsCognitoProperties.getClientSecret())) {
            String secretHash = calculateSecretHash(request.getUsername());
            signUpRequestBuilder.secretHash(secretHash);
        }

        try {
            return cognitoIdentityProviderClient.signUp(signUpRequestBuilder.build());
        } catch (UsernameExistsException e) {
            log.error("Username already exists: {}", e.getMessage());
            throw e;
        } catch (InvalidPasswordException e) {
            log.error("Invalid password for user: {}", request.getUsername());
            throw e;
        } catch (Exception e) {
            log.error("Error during user registration: {}", e.getMessage(), e);
            throw new RuntimeException("Error during user registration");
        }
    }

    /**
     * Confirms user registration with the provided confirmation code.
     *
     * @param request the confirmation request containing username and confirmation code
     * @return the confirmation response from Cognito
     */
    public ConfirmSignUpResponse confirmUserRegistration(CognitoConfirmSignUpRequest request) {
        var confirmSignUpRequestBuilder = ConfirmSignUpRequest.builder()
                .clientId(awsCognitoProperties.getClientId())
                .username(request.getUsername())
                .confirmationCode(request.getConfirmationCode());

        if (Is.nonNullNonEmpty(awsCognitoProperties.getClientSecret())) {
            String secretHash = calculateSecretHash(request.getUsername());
            confirmSignUpRequestBuilder.secretHash(secretHash);
        }

        try {
            return cognitoIdentityProviderClient.confirmSignUp(confirmSignUpRequestBuilder.build());
        } catch (CodeMismatchException e) {
            log.error("Invalid confirmation code for user: {}", request.getUsername());
            throw e;
        } catch (ExpiredCodeException e) {
            log.error("Confirmation code expired for user: {}", request.getUsername());
            throw e;
        } catch (Exception e) {
            log.error("Error during user confirmation: {}", e.getMessage(), e);
            throw new RuntimeException("Error during user confirmation");
        }
    }

    /**
     * Resends the confirmation code to the specified user.
     *
     * @param username the username of the user to resend the confirmation code to
     * @return the resend confirmation code response from Cognito
     */
    public ResendConfirmationCodeResponse resendConfirmationCode(String username) {
        var resendConfirmationCodeRequestBuilder = ResendConfirmationCodeRequest.builder()
                .clientId(awsCognitoProperties.getClientId())
                .username(username);

        if (Is.nonNullNonEmpty(awsCognitoProperties.getClientSecret())) {
            String secretHash = calculateSecretHash(username);
            resendConfirmationCodeRequestBuilder.secretHash(secretHash);
        }

        try {
            return cognitoIdentityProviderClient.resendConfirmationCode(resendConfirmationCodeRequestBuilder.build());
        } catch (UserNotFoundException e) {
            log.error("User not found: {}", username);
            throw e;
        } catch (CodeDeliveryFailureException e) {
            log.error("Failed to resend confirmation code to user: {}", username);
            throw e;
        } catch (Exception e) {
            log.error("Error resending confirmation code: {}", e.getMessage(), e);
            throw new RuntimeException("Error resending confirmation code");
        }
    }

    /**
     * Logs in the user with the provided credentials.
     *
     * @param request the login request containing username and password
     * @return the authentication result containing tokens
     */
    public AccessRefreshToken loginUser(CognitoLoginRequest request) {
        var authRequestBuilder = InitiateAuthRequest.builder()
                .clientId(awsCognitoProperties.getClientId())
                .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
                .authParameters(new HashMap<>() {
                    {
                        put("USERNAME", request.getUsername());
                        put("PASSWORD", request.getPassword());
                        if (Is.nonNullNonEmpty(awsCognitoProperties.getClientSecret())) {
                            put("SECRET_HASH", calculateSecretHash(request.getUsername()));
                        }
                    }
                });

        try {
            var authResponse = cognitoIdentityProviderClient.initiateAuth(authRequestBuilder.build());
            return AccessRefreshToken.builder()
                    .accessToken(authResponse.authenticationResult().accessToken())
                    .refreshToken(authResponse.authenticationResult().refreshToken())
                    .expiresIn(authResponse.authenticationResult().expiresIn())
                    .tokenType(authResponse.authenticationResult().tokenType())
                    .build();
        } catch (UserNotConfirmedException e) {
            log.error("User not confirmed: {}", request.getUsername());
            throw e;
        } catch (NotAuthorizedException e) {
            log.error("Invalid credentials for user: {}", request.getUsername());
            throw e;
        } catch (UserNotFoundException e) {
            log.error("User not found: {}", request.getUsername());
            throw e;
        } catch (Exception e) {
            log.error("Error during user login: {}", e.getMessage(), e);
            throw new RuntimeException("Error during user login");
        }
    }

    /**
     * Initiates the password reset process by sending a reset code to the user's email or phone.
     *
     * @param request the forgot password request containing the username
     * @return the forgot password response from Cognito
     */
    public ForgotPasswordResponse sendPasswordResetVerificationCode(CognitoForgotPasswordRequest request) {
        var forgotPasswordRequestBuilder = ForgotPasswordRequest.builder()
                .clientId(awsCognitoProperties.getClientId())
                .username(request.getUsername());

        if (Is.nonNullNonEmpty(awsCognitoProperties.getClientSecret())) {
            String secretHash = calculateSecretHash(request.getUsername());
            forgotPasswordRequestBuilder.secretHash(secretHash);
        }

        try {
            return cognitoIdentityProviderClient.forgotPassword(forgotPasswordRequestBuilder.build());
        } catch (UserNotFoundException e) {
            log.error("User not found: {}", request.getUsername());
            throw e;
        } catch (Exception e) {
            log.error("Error initiating password reset: {}", e.getMessage(), e);
            throw new RuntimeException("Error initiating password reset");
        }
    }

    /**
     * Resets the user's password using the provided verification code and new password.
     *
     * @param request the reset password request containing username, confirmation code, and new password
     * @return the confirm forgot password response from Cognito
     */
    public ConfirmForgotPasswordResponse resetPasswordUsingVerificationCode(CognitoResetPasswordRequest request) {
        var confirmForgotPasswordRequestBuilder = ConfirmForgotPasswordRequest.builder()
                .clientId(awsCognitoProperties.getClientId())
                .username(request.getUsername())
                .confirmationCode(request.getConfirmationCode())
                .password(request.getNewPassword());

        if (Is.nonNullNonEmpty(awsCognitoProperties.getClientSecret())) {
            String secretHash = calculateSecretHash(request.getUsername());
            confirmForgotPasswordRequestBuilder.secretHash(secretHash);
        }

        try {
            return cognitoIdentityProviderClient.confirmForgotPassword(confirmForgotPasswordRequestBuilder.build());
        } catch (UserNotFoundException e) {
            log.error("User not found: {}", request.getUsername());
            throw e;
        } catch (CodeMismatchException e) {
            log.error("Invalid confirmation code for user: {}", request.getUsername());
            throw e;
        } catch (ExpiredCodeException e) {
            log.error("Confirmation code expired for user: {}", request.getUsername());
            throw e;
        } catch (InvalidPasswordException e) {
            log.error("Invalid password for user: {}", request.getUsername());
            throw e;
        } catch (Exception e) {
            log.error("Error resetting password: {}", e.getMessage(), e);
            throw new RuntimeException("Error resetting password");
        }
    }

    /**
     * Changes the user's password when authenticated.
     *
     * @param request the change password request containing access token, previous password, and new password
     * @return the change password response from Cognito
     */
    public ChangePasswordResponse changeUserPassword(CognitoChangePasswordRequest request) {
        var changePasswordRequest = ChangePasswordRequest.builder()
                .accessToken(request.getAccessToken())
                .previousPassword(request.getPreviousPassword())
                .proposedPassword(request.getProposedPassword())
                .build();

        try {
            return cognitoIdentityProviderClient.changePassword(changePasswordRequest);
        } catch (UserNotFoundException e) {
            log.error("User not found for the provided access token.");
            throw e;
        } catch (UserNotConfirmedException e) {
            log.error("User not confirmed for the provided access token.");
            throw e;
        } catch (NotAuthorizedException e) {
            log.error("Invalid access token or previous password.");
            throw e;
        } catch (InvalidPasswordException e) {
            log.error("Invalid new password.");
            throw e;
        } catch (Exception e) {
            log.error("Error changing password: {}", e.getMessage(), e);
            throw new RuntimeException("Error changing password");
        }
    }

    /**
     * Updates user attributes for the authenticated user.
     *
     * @param accessToken the access token of the authenticated user
     * @param attributes  a map of attribute names and their new values
     * @return the update user attributes response from Cognito
     */
    public UpdateUserAttributesResponse updateUserAttributes(String accessToken, Map<String, String> attributes) {
        if (Is.nullOrEmpty(accessToken) || Is.nullOrEmpty(attributes)) {
            return null;
        }
        List<AttributeType> attributeTypes = new ArrayList<>();
        attributes.forEach((key, value) -> attributeTypes.add(
                AttributeType.builder().name(key).value(value).build()));

        var updateUserAttributesRequest = UpdateUserAttributesRequest.builder()
                .accessToken(accessToken)
                .userAttributes(attributeTypes)
                .build();

        try {
            return cognitoIdentityProviderClient.updateUserAttributes(updateUserAttributesRequest);
        } catch (UserNotFoundException e) {
            log.error("User not found for the provided access token.");
            throw e;
        } catch (UserNotConfirmedException e) {
            log.error("User not confirmed for the provided access token.");
            throw e;
        } catch (NotAuthorizedException e) {
            log.error("Invalid access token.");
            throw e;
        } catch (Exception e) {
            log.error("Error updating user attributes: {}", e.getMessage(), e);
            throw new RuntimeException("Error updating user attributes");
        }
    }

    /**
     * Checks if the given email is already registered in the Cognito User Pool.
     *
     * @param email the email to check
     * @return true if the email is already registered, false otherwise
     */
    public boolean isEmailAlreadyRegistered(String email) {
        try {
            var listUsersRequest = ListUsersRequest.builder()
                    .userPoolId(awsCognitoProperties.getUserPoolId())
                    .filter("email = \"" + email + "\"")
                    .limit(1)
                    .build();

            var listUsersResponse = cognitoIdentityProviderClient.listUsers(listUsersRequest);
            return Is.nonNullNonEmpty(listUsersResponse.users());
        } catch (Exception ex) {
            log.error("Error checking if email exists: {}", ex.getMessage(), ex);
            return false;
        }
    }

    /**
     * Calculates the secret hash for the given username.
     *
     * @param username the username
     * @return the calculated secret hash
     */
    private String calculateSecretHash(String username) {
        try {
            String data = username + awsCognitoProperties.getClientId();
            Mac sha256Hmac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
            SecretKeySpec secretKey = new SecretKeySpec(
                    awsCognitoProperties.getClientSecret().getBytes(StandardCharsets.UTF_8), HMAC_SHA256_ALGORITHM);
            sha256Hmac.init(secretKey);
            byte[] hashBytes = sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (Exception ex) {
            log.error("Error calculating secret hash: {}", ex.getMessage(), ex);
            throw new RuntimeException("Error calculating secret hash");
        }
    }
}
