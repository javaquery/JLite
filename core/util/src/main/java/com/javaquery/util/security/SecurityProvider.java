package com.javaquery.util.security;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author vicky.thakor
 * @since 1.2.8
 */
public class SecurityProvider {

    /**
     * Generates a Basic Authentication token for the given username and password.
     *
     * @param username The username.
     * @param password The password.
     * @return A Basic Authentication token.
     */
    public static String getBasicAuthToken(String username, String password) {
        String auth = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(auth.getBytes());
    }

    /**
     * Generates a Bearer Authentication token for the given token.
     *
     * @param token The token.
     * @return A Bearer Authentication token.
     */
    public static String getBearerAuthToken(String token) {
        return "Bearer " + token;
    }

    /**
     * Encodes the given data to Base64 format.
     *
     * @param data The data to encode.
     * @return The Base64 encoded string.
     */
    public static String encodeBase64(String data) {
        return Base64.getEncoder().encodeToString(data.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Decodes the given Base64 encoded string.
     *
     * @param base64Data The Base64 encoded string.
     * @return The decoded data.
     */
    public static String decodeBase64(String base64Data) {
        byte[] decodedBytes = Base64.getDecoder().decode(base64Data);
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }
}
