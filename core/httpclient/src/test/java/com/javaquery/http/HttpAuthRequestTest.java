package com.javaquery.http;

import com.javaquery.http.handler.HttpRequestHandler;
import com.javaquery.http.handler.HttpResponseHandler;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author javaquery
 * @since 1.0.0
 */
public class HttpAuthRequestTest {

    @Test
    public void performBasicAuthRequest() {
        HttpRequest httpRequest = new HttpRequest.HttpRequestBuilder("GetRequest", HttpMethod.GET)
                .withHost("https://httpbin.org")
                .withEndPoint("/basic-auth/test/test")
                .withUsernamePassword("test", "test")
                .withQueryParameter("utm_source", "javaquery")
                .build();

        HttpExecutionContext httpExecutionContext = new HttpExecutionContext();
        httpExecutionContext.addHttpRequestHandler(headerHttpRequestHandler());

        HttpClient httpClient = new HttpClient();
        httpClient.execute(httpExecutionContext, httpRequest, new HttpResponseHandler<Object>() {
            @Override
            public Object onResponse(HttpResponse httpResponse) {
                Assertions.assertEquals(200, httpResponse.getStatusCode());

                JSONObject jsonObject = httpResponse.getJSONObjectBody();
                Assertions.assertNotNull(jsonObject);
                Assertions.assertTrue(jsonObject.optBoolean("authenticated"));
                Assertions.assertEquals("test", jsonObject.optString("user"));
                return null;
            }

            @Override
            public void onMaxRetryAttempted(HttpResponse httpResponse) {}
        });
    }

    private HttpRequestHandler headerHttpRequestHandler() {
        return new HttpRequestHandler() {
            @Override
            public void beforeRequest(HttpExecutionContext httpExecutionContext, HttpRequest httpRequest) {
                httpRequest.withHeader("Request-Id", "faca52e4-1e15-11ec-9621-0242ac130002");
            }

            @Override
            public void afterResponse(
                    HttpExecutionContext httpExecutionContext, HttpRequest httpRequest, HttpResponse httpResponse) {
                Assertions.assertNotNull(httpResponse);
            }

            @Override
            public void onError(
                    HttpExecutionContext httpExecutionContext, HttpRequest httpRequest, Exception exception) {}
        };
    }
}
