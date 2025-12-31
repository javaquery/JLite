package com.javaquery.http.oauth;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.builder.api.DefaultApi10a;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;
import com.javaquery.http.HttpExecutionContext;
import com.javaquery.http.HttpRequest;
import com.javaquery.http.HttpResponse;
import com.javaquery.http.handler.HttpRequestHandler;
import com.javaquery.util.Is;

/**
 * @author javaquery
 * @since 1.0.6
 */
public class OAuth10HttpRequestHandler implements HttpRequestHandler {

    private final OAuthConfig oAuthConfig;
    private final OAuth10aService oAuth10aService;

    public OAuth10HttpRequestHandler(OAuthConfig oAuthConfig) {
        this.oAuthConfig = oAuthConfig;
        ServiceBuilder serviceBuilder = new ServiceBuilder(oAuthConfig.getConsumerKey())
                .apiSecret(oAuthConfig.getConsumerSecret())
                .callback(oAuthConfig.getCallbackUrl());
        Is.nonNullNonEmpty(oAuthConfig.getScope(), () -> serviceBuilder.withScope(oAuthConfig.getScope()));

        oAuth10aService = serviceBuilder.build(new DefaultApi10a() {
            @Override
            public String getRequestTokenEndpoint() {
                return oAuthConfig.getRequestTokenEndpoint();
            }

            @Override
            public String getAccessTokenEndpoint() {
                return oAuthConfig.getAccessTokenEndpoint();
            }

            @Override
            protected String getAuthorizationBaseUrl() {
                return oAuthConfig.getAuthorizationBaseUrl();
            }
        });
    }

    @Override
    public void beforeRequest(HttpExecutionContext httpExecutionContext, HttpRequest httpRequest) {
        String accessToken = Is.nonNullNonEmpty(oAuthConfig.getAccessToken()) ? oAuthConfig.getAccessToken() : "";
        String accessTokenSecret =
                Is.nonNullNonEmpty(oAuthConfig.getAccessTokenSecret()) ? oAuthConfig.getAccessTokenSecret() : "";

        OAuth1AccessToken oAuth1AccessToken = new OAuth1AccessToken(accessToken, accessTokenSecret);
        Verb verb = Verb.valueOf(httpRequest.getHttpMethod().name());

        OAuthRequest authRequest =
                new OAuthRequest(verb, httpRequest.httpRequestURI().toString());
        Is.nonNull(
                httpRequest.getHttpPayload(),
                () -> authRequest.setPayload(httpRequest.getHttpPayload().getPayload()));

        oAuth10aService.signRequest(oAuth1AccessToken, authRequest);
        for (String headerKey : authRequest.getHeaders().keySet()) {
            httpRequest.withHeader(headerKey, authRequest.getHeaders().get(headerKey));
        }
    }

    @Override
    public void afterResponse(
            HttpExecutionContext httpExecutionContext, HttpRequest httpRequest, HttpResponse httpResponse) {}

    @Override
    public void onError(HttpExecutionContext httpExecutionContext, HttpRequest httpRequest, Exception exception) {}
}
