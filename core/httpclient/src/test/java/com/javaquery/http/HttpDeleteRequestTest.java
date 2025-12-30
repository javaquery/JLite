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

public class HttpDeleteRequestTest {

    @Test
    public void performDeleteRequest(){
        HttpRequest httpRequest = new HttpRequest.HttpRequestBuilder("DeleteRequest", HttpMethod.DELETE)
                .withHost("https://httpbin.org")
                .withEndPoint("/delete")
                .withQueryParameter("utm_source", "javaquery")
                .build();

        HttpExecutionContext httpExecutionContext = new HttpExecutionContext();
        httpExecutionContext.addMetaData("meta", "data");
        httpExecutionContext.addHttpRequestHandler(headerHttpRequestHandler());

        HttpClient httpClient = new HttpClient();
        httpClient.execute(httpExecutionContext, httpRequest, new HttpResponseHandler<Object>() {
            @Override
            public Object onResponse(HttpResponse httpResponse) {
                Assertions.assertEquals(200, httpResponse.getStatusCode());

                JSONObject jsonObject = httpResponse.getJSONObjectBody();
                Assertions.assertNotNull(jsonObject);

                JSONObject headers = jsonObject.optJSONObject("headers");
                Assertions.assertEquals("faca52e4-1e15-11ec-9621-0242ac130002", headers.optString("Request-Id"));

                JSONObject args = jsonObject.optJSONObject("args");
                Assertions.assertEquals("javaquery", args.optString("utm_source"));
                return null;
            }

            @Override
            public void onMaxRetryAttempted(HttpResponse httpResponse) {}
        });
    }

    private HttpRequestHandler headerHttpRequestHandler(){
        return new HttpRequestHandler() {
            @Override
            public void beforeRequest(HttpExecutionContext httpExecutionContext, HttpRequest httpRequest) {
                httpRequest.withHeader("Request-Id", "faca52e4-1e15-11ec-9621-0242ac130002");
            }

            @Override
            public void afterResponse(HttpExecutionContext httpExecutionContext, HttpRequest httpRequest, HttpResponse httpResponse) {
                Assertions.assertNotNull(httpResponse);
            }

            @Override
            public void onError(HttpExecutionContext httpExecutionContext, HttpRequest httpRequest, Exception exception) {

            }
        };
    }
}
