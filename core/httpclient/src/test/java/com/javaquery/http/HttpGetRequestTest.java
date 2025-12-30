package com.javaquery.http;

import com.javaquery.http.handler.HttpRequestHandler;
import com.javaquery.http.handler.HttpResponseHandler;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author javaquery
 * @since 1.0.0
 */
public class HttpGetRequestTest {

    @Test
    public void performGetRequest(){
        HttpRequest httpRequest = new HttpRequest.HttpRequestBuilder("InventoryUpdate", HttpMethod.GET)
                .withHost("https://www.google.com")
                .build();

        HttpExecutionContext httpExecutionContext = new HttpExecutionContext();

        HttpClient httpClient = new HttpClient();

        httpClient.execute(httpExecutionContext, httpRequest, new HttpResponseHandler<Object>() {
            @Override
            public Object onResponse(HttpResponse httpResponse) {
                Assertions.assertNotNull(httpResponse.getBody());
                return null;
            }

            @Override
            public void onMaxRetryAttempted(HttpResponse httpResponse) {

            }
        });
    }

    @Test
    public void performJSONGetRequest(){
        HttpRequest httpRequest = new HttpRequest.HttpRequestBuilder("GetRequest", HttpMethod.GET)
                .withHost("https://httpbin.org")
                .withEndPoint("/get")
                .withHeader("echo", "echo")
                .withHeaders(dummyHeaderParameters())
                .withQueryParameter("utm_source", "javaquery")
                .withQueryParameter(dummyHeaderParameters())
                .build();

        Map<String, Object> metaData = new HashMap<>(1);
        metaData.put("this", "that");
        HttpExecutionContext httpExecutionContext = new HttpExecutionContext();
        httpExecutionContext.setMetaData(metaData);
        httpExecutionContext.addMetaData("meta", "data");

        httpExecutionContext.addHttpRequestHandler(headerHttpRequestHandler());

        HttpClient httpClient = new HttpClient();
        httpClient.execute(httpExecutionContext, httpRequest, new HttpResponseHandler<Object>() {
            @Override
            public Object onResponse(HttpResponse httpResponse) {
                Assertions.assertEquals(200, httpResponse.getStatusCode());
                Assertions.assertNotNull(httpResponse.getHeaders());

                JSONObject jsonObject = httpResponse.getJSONObjectBody();
                Assertions.assertNotNull(jsonObject);

                JSONObject headers = jsonObject.optJSONObject("headers");
                Assertions.assertEquals("faca52e4-1e15-11ec-9621-0242ac130002", headers.optString("Request-Id"));
                Assertions.assertEquals("echo", headers.optString("Echo"));
                Assertions.assertEquals("dummy", headers.optString("Dummy"));

                JSONObject args = jsonObject.optJSONObject("args");
                Assertions.assertEquals("javaquery", args.optString("utm_source"));
                Assertions.assertEquals("dummy", args.optString("dummy"));
                Assertions.assertEquals("Request-Param", args.optString("Request-Param"));
                return null;
            }

            @Override
            public void onMaxRetryAttempted(HttpResponse httpResponse) {

            }
        });
    }

    @Test
    public void performRetryGetRequest(){
        HttpRequest httpRequest = new HttpRequest.HttpRequestBuilder("GetRequest", HttpMethod.GET)
                .withHost("https://httpbin.org")
                .withEndPoint("/status/500")
                .withRetryPolicy(DefaultRetryPolicy.get(5))
                .build();

        HttpExecutionContext httpExecutionContext = new HttpExecutionContext();

        HttpClient httpClient = new HttpClient();
        httpClient.execute(httpExecutionContext, httpRequest, new HttpResponseHandler<Object>() {
            @Override
            public Object onResponse(HttpResponse httpResponse) {
                Assertions.assertEquals(500, httpResponse.getStatusCode());
                return null;
            }

            @Override
            public void onMaxRetryAttempted(HttpResponse httpResponse) {
                Assertions.assertEquals(500, httpResponse.getStatusCode());
            }
        });
    }

    @Test
    public void performGetRedirect(){
        HttpRequest httpRequest = new HttpRequest.HttpRequestBuilder("GetRequest", HttpMethod.GET)
                .withHost("https://httpbin.org")
                .withEndPoint("/redirect-to")
                .withQueryParameter("url", "https://httpbin.org/get?dummy=dummy")
                .build();

        HttpExecutionContext httpExecutionContext = new HttpExecutionContext();

        HttpClient httpClient = new HttpClient();
        httpClient.execute(httpExecutionContext, httpRequest, new HttpResponseHandler<Object>() {
            @Override
            public Object onResponse(HttpResponse httpResponse) {
                Assertions.assertEquals(200, httpResponse.getStatusCode());

                JSONObject jsonObject = httpResponse.getJSONObjectBody();
                Assertions.assertNotNull(jsonObject);

                JSONObject headers = jsonObject.optJSONObject("headers");
                Assertions.assertEquals("httpbin.org", headers.optString("Host"));

                JSONObject args = jsonObject.optJSONObject("args");
                Assertions.assertEquals("dummy", args.optString("dummy"));

                return null;
            }

            @Override
            public void onMaxRetryAttempted(HttpResponse httpResponse) {
                Assertions.assertEquals(500, httpResponse.getStatusCode());
            }
        });
    }

    private HttpRequestHandler headerHttpRequestHandler(){
        return new HttpRequestHandler() {
            @Override
            public void beforeRequest(HttpExecutionContext httpExecutionContext, HttpRequest httpRequest) {
                httpRequest.withHeader("Request-Id", "faca52e4-1e15-11ec-9621-0242ac130002");
                httpRequest.withQueryParameter("Request-Param", "Request-Param");
            }

            @Override
            public void afterResponse(HttpExecutionContext httpExecutionContext, HttpRequest httpRequest, HttpResponse httpResponse) {
                Assertions.assertNotNull(httpResponse);
                Assertions.assertNotNull(httpExecutionContext.getMetaData());
                Assertions.assertEquals("data", httpExecutionContext.getMeta("meta", null));
                Assertions.assertEquals("that", httpExecutionContext.getMeta("this", null));
                Assertions.assertEquals("defaultValue", httpExecutionContext.getMeta("no-key", "defaultValue"));
            }

            @Override
            public void onError(HttpExecutionContext httpExecutionContext, HttpRequest httpRequest, Exception exception) {

            }
        };
    }

    public Map<String, String> dummyHeaderParameters(){
        Map<String, String> map = new HashMap<>();
        map.put("dummy", "dummy");
        return map;
    }
}
