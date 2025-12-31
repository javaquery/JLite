package com.javaquery.util.security;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test cases for {@link SecurityProvider}
 *
 * @author vicky.thakor
 * @since 1.2.8
 */
public class TestSecurityProvider {

    @Test
    public void test_getBasicAuthToken_validCredentials() {
        String username = "admin";
        String password = "password123";
        String expectedAuth = "admin:password123";
        String expectedToken = "Basic " + Base64.getEncoder().encodeToString(expectedAuth.getBytes());

        String actualToken = SecurityProvider.getBasicAuthToken(username, password);

        Assertions.assertEquals(expectedToken, actualToken);
    }

    @Test
    public void test_getBasicAuthToken_withSpecialCharacters() {
        String username = "user@example.com";
        String password = "p@ssw0rd!#$";
        String expectedAuth = "user@example.com:p@ssw0rd!#$";
        String expectedToken = "Basic " + Base64.getEncoder().encodeToString(expectedAuth.getBytes());

        String actualToken = SecurityProvider.getBasicAuthToken(username, password);

        Assertions.assertEquals(expectedToken, actualToken);
    }

    @Test
    public void test_getBasicAuthToken_emptyUsername() {
        String username = "";
        String password = "password";
        String expectedAuth = ":password";
        String expectedToken = "Basic " + Base64.getEncoder().encodeToString(expectedAuth.getBytes());

        String actualToken = SecurityProvider.getBasicAuthToken(username, password);

        Assertions.assertEquals(expectedToken, actualToken);
    }

    @Test
    public void test_getBasicAuthToken_emptyPassword() {
        String username = "admin";
        String password = "";
        String expectedAuth = "admin:";
        String expectedToken = "Basic " + Base64.getEncoder().encodeToString(expectedAuth.getBytes());

        String actualToken = SecurityProvider.getBasicAuthToken(username, password);

        Assertions.assertEquals(expectedToken, actualToken);
    }

    @Test
    public void test_getBasicAuthToken_nullUsername() {
        String username = null;
        String password = "password";
        String expectedAuth = "null:password";
        String expectedToken = "Basic " + Base64.getEncoder().encodeToString(expectedAuth.getBytes());

        String actualToken = SecurityProvider.getBasicAuthToken(username, password);

        Assertions.assertEquals(expectedToken, actualToken);
    }

    @Test
    public void test_getBasicAuthToken_nullPassword() {
        String username = "admin";
        String password = null;
        String expectedAuth = "admin:null";
        String expectedToken = "Basic " + Base64.getEncoder().encodeToString(expectedAuth.getBytes());

        String actualToken = SecurityProvider.getBasicAuthToken(username, password);

        Assertions.assertEquals(expectedToken, actualToken);
    }

    @Test
    public void test_getBearerAuthToken_validToken() {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
        String expectedToken = "Bearer " + token;

        String actualToken = SecurityProvider.getBearerAuthToken(token);

        Assertions.assertEquals(expectedToken, actualToken);
    }

    @Test
    public void test_getBearerAuthToken_emptyToken() {
        String token = "";
        String expectedToken = "Bearer ";

        String actualToken = SecurityProvider.getBearerAuthToken(token);

        Assertions.assertEquals(expectedToken, actualToken);
    }

    @Test
    public void test_getBearerAuthToken_nullToken() {
        String token = null;
        String expectedToken = "Bearer null";

        String actualToken = SecurityProvider.getBearerAuthToken(token);

        Assertions.assertEquals(expectedToken, actualToken);
    }

    @Test
    public void test_encodeBase64_simpleString() {
        String data = "Hello World!";
        String expectedEncoded = Base64.getEncoder().encodeToString(data.getBytes(StandardCharsets.UTF_8));

        String actualEncoded = SecurityProvider.encodeBase64(data);

        Assertions.assertEquals(expectedEncoded, actualEncoded);
        Assertions.assertEquals("SGVsbG8gV29ybGQh", actualEncoded);
    }

    @Test
    public void test_encodeBase64_emptyString() {
        String data = "";
        String expectedEncoded = "";

        String actualEncoded = SecurityProvider.encodeBase64(data);

        Assertions.assertEquals(expectedEncoded, actualEncoded);
    }

    @Test
    public void test_encodeBase64_specialCharacters() {
        String data = "Test@#$%^&*()123";
        String expectedEncoded = Base64.getEncoder().encodeToString(data.getBytes(StandardCharsets.UTF_8));

        String actualEncoded = SecurityProvider.encodeBase64(data);

        Assertions.assertEquals(expectedEncoded, actualEncoded);
    }

    @Test
    public void test_encodeBase64_unicodeCharacters() {
        String data = "Hello 你好 مرحبا";
        String expectedEncoded = Base64.getEncoder().encodeToString(data.getBytes(StandardCharsets.UTF_8));

        String actualEncoded = SecurityProvider.encodeBase64(data);

        Assertions.assertEquals(expectedEncoded, actualEncoded);
    }

    @Test
    public void test_encodeBase64_nullData() {
        String data = null;

        Assertions.assertThrows(NullPointerException.class, () -> {
            SecurityProvider.encodeBase64(data);
        });
    }

    @Test
    public void test_decodeBase64_simpleString() {
        String encodedData = "SGVsbG8gV29ybGQh";
        String expectedDecoded = "Hello World!";

        String actualDecoded = SecurityProvider.decodeBase64(encodedData);

        Assertions.assertEquals(expectedDecoded, actualDecoded);
    }

    @Test
    public void test_decodeBase64_emptyString() {
        String encodedData = "";
        String expectedDecoded = "";

        String actualDecoded = SecurityProvider.decodeBase64(encodedData);

        Assertions.assertEquals(expectedDecoded, actualDecoded);
    }

    @Test
    public void test_decodeBase64_specialCharacters() {
        String originalData = "Test@#$%^&*()123";
        String encodedData = Base64.getEncoder().encodeToString(originalData.getBytes(StandardCharsets.UTF_8));

        String actualDecoded = SecurityProvider.decodeBase64(encodedData);

        Assertions.assertEquals(originalData, actualDecoded);
    }

    @Test
    public void test_decodeBase64_unicodeCharacters() {
        String originalData = "Hello 你好 مرحبا";
        String encodedData = Base64.getEncoder().encodeToString(originalData.getBytes(StandardCharsets.UTF_8));

        String actualDecoded = SecurityProvider.decodeBase64(encodedData);

        Assertions.assertEquals(originalData, actualDecoded);
    }

    @Test
    public void test_decodeBase64_invalidBase64String() {
        String invalidEncodedData = "This is not base64!!!";

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            SecurityProvider.decodeBase64(invalidEncodedData);
        });
    }

    @Test
    public void test_decodeBase64_nullData() {
        String encodedData = null;

        Assertions.assertThrows(NullPointerException.class, () -> {
            SecurityProvider.decodeBase64(encodedData);
        });
    }

    @Test
    public void test_encodeDecodeBase64_roundTrip() {
        String originalData = "This is a test message with special chars: @#$%^&*()";

        String encoded = SecurityProvider.encodeBase64(originalData);
        String decoded = SecurityProvider.decodeBase64(encoded);

        Assertions.assertEquals(originalData, decoded);
    }

    @Test
    public void test_encodeDecodeBase64_roundTrip_longString() {
        String originalData = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. "
                + "Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. "
                + "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris.";

        String encoded = SecurityProvider.encodeBase64(originalData);
        String decoded = SecurityProvider.decodeBase64(encoded);

        Assertions.assertEquals(originalData, decoded);
    }

    @Test
    public void test_basicAuthToken_startsWithBasic() {
        String token = SecurityProvider.getBasicAuthToken("user", "pass");

        Assertions.assertTrue(token.startsWith("Basic "));
    }

    @Test
    public void test_bearerAuthToken_startsWithBearer() {
        String token = SecurityProvider.getBearerAuthToken("sometoken");

        Assertions.assertTrue(token.startsWith("Bearer "));
    }

    @Test
    public void test_basicAuthToken_canBeDecoded() {
        String username = "testuser";
        String password = "testpass";
        String token = SecurityProvider.getBasicAuthToken(username, password);

        // Extract the base64 part (remove "Basic " prefix)
        String base64Part = token.substring(6);
        String decoded = SecurityProvider.decodeBase64(base64Part);

        Assertions.assertEquals(username + ":" + password, decoded);
    }

    @Test
    public void test_encodeBase64_multilineString() {
        String data = "Line 1\nLine 2\nLine 3";
        String encoded = SecurityProvider.encodeBase64(data);
        String decoded = SecurityProvider.decodeBase64(encoded);

        Assertions.assertEquals(data, decoded);
    }

    @Test
    public void test_encodeBase64_jsonString() {
        String jsonData = "{\"name\":\"John\",\"age\":30,\"city\":\"New York\"}";
        String encoded = SecurityProvider.encodeBase64(jsonData);
        String decoded = SecurityProvider.decodeBase64(encoded);

        Assertions.assertEquals(jsonData, decoded);
    }
}
