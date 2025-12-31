package com.javaquery.http;

import com.javaquery.http.exception.HttpException;
import com.javaquery.http.handler.HttpRequestHandler;
import com.javaquery.http.handler.HttpResponseHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author javaquery
 * @since 1.0.0
 */
public class HttpErrorRequestTest {
    @Test
    public void performWrongProtocolRequest() {
        HttpRequest httpRequest = new HttpRequest.HttpRequestBuilder("GetRequest", HttpMethod.GET)
                .withHost("httxp://httpbin.org")
                .withEndPoint("/get")
                .build();

        HttpExecutionContext httpExecutionContext = new HttpExecutionContext();
        httpExecutionContext.addHttpRequestHandler(headerHttpRequestHandler());

        HttpClient httpClient = new HttpClient();
        httpClient.execute(httpExecutionContext, httpRequest, new HttpResponseHandler<Object>() {
            @Override
            public Object onResponse(HttpResponse httpResponse) {
                return null;
            }

            @Override
            public void onMaxRetryAttempted(HttpResponse httpResponse) {}
        });
    }

    @Test()
    public void performWrongURLSyntaxRequest() {
        Assertions.assertThrows(HttpException.class, () -> {
            new HttpRequest.HttpRequestBuilder("GetRequest", HttpMethod.GET)
                    .withHost("https://httpbin^org")
                    .withEndPoint("/get");
        });
    }

    private HttpRequestHandler headerHttpRequestHandler() {
        return new HttpRequestHandler() {
            @Override
            public void beforeRequest(HttpExecutionContext httpExecutionContext, HttpRequest httpRequest) {}

            @Override
            public void afterResponse(
                    HttpExecutionContext httpExecutionContext, HttpRequest httpRequest, HttpResponse httpResponse) {}

            @Override
            public void onError(
                    HttpExecutionContext httpExecutionContext, HttpRequest httpRequest, Exception exception) {
                Assertions.assertNotNull(exception);
            }
        };
    }
}
