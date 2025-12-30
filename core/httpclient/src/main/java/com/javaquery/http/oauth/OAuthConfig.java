package com.javaquery.http.oauth;

import lombok.Builder;
import lombok.Getter;

/**
 * @author javaquery
 * @since 1.0.6
 */
@Getter
@Builder
public class OAuthConfig {
    private String consumerKey;
    private String consumerSecret;
    private String accessToken;
    private String accessTokenSecret;
    private String requestTokenEndpoint;
    private String accessTokenEndpoint;
    private String authorizationBaseUrl;
    private String callbackUrl;
    private String scope;
}
