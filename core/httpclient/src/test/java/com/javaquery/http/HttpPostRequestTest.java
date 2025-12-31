package com.javaquery.http;

import com.javaquery.http.handler.HttpRequestHandler;
import com.javaquery.http.handler.HttpResponseHandler;
import com.javaquery.util.io.Files;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author javaquery
 * @since 1.0.0
 */
public class HttpPostRequestTest {

    public static final Logger LOGGER = LoggerFactory.getLogger(HttpPostRequestTest.class);
    private static final String SAMPLE_POST_PAYLOAD = "{\"javaquery-key\":\"javaquery-value\"}";

    @Test
    public void performJSONPostRequest() {
        HttpRequest httpRequest = new HttpRequest.HttpRequestBuilder("PostRequest", HttpMethod.POST)
                .withHost("https://httpbin.org")
                .withEndPoint("/post")
                .withQueryParameter("utm_source", "javaquery")
                .withHttpPayload(new HttpRequest.HttpPayload(StringPool.UTF8, "application/json", SAMPLE_POST_PAYLOAD))
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

                JSONObject headers = jsonObject.optJSONObject("headers");
                Assertions.assertEquals("faca52e4-1e15-11ec-9621-0242ac130002", headers.optString("Request-Id"));

                JSONObject args = jsonObject.optJSONObject("args");
                Assertions.assertEquals("javaquery", args.optString("utm_source"));

                String body = jsonObject.optString("data");
                Assertions.assertEquals(SAMPLE_POST_PAYLOAD, body);
                return null;
            }

            @Override
            public void onMaxRetryAttempted(HttpResponse httpResponse) {}
        });
    }

    @Test
    public void performFormPostRequest() {
        Map<String, Object> form = new HashMap<>();
        form.put("form-key", "form-value");

        HttpRequest httpRequest = new HttpRequest.HttpRequestBuilder("PostRequest", HttpMethod.POST)
                .withHost("https://httpbin.org")
                .withEndPoint("/post")
                .withQueryParameter("utm_source", "javaquery")
                .withHttpPayload(
                        new HttpRequest.HttpPayload(StringPool.UTF8, "application/x-www-form-urlencoded", form))
                .build();

        HttpExecutionContext httpExecutionContext = new HttpExecutionContext();
        httpExecutionContext.setHttpRequestHandlers(Collections.singletonList(headerHttpRequestHandler()));

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

                JSONObject body = jsonObject.optJSONObject("form");
                Assertions.assertEquals("form-value", body.optString("form-key"));
                return null;
            }

            @Override
            public void onMaxRetryAttempted(HttpResponse httpResponse) {}
        });
    }

    @Test
    public void performFormPostRequestWithFile() {
        File file = new File(System.getProperty("java.io.tmpdir") + File.separatorChar + "dummy.txt");
        Files.writeToFile(file, "Hello World!");

        Map<String, Object> form = new HashMap<>();
        form.put("form-key", "form-value");
        form.put("files", Collections.singletonList(file));

        HttpRequest httpRequest = new HttpRequest.HttpRequestBuilder("PostRequest", HttpMethod.POST)
                .withHost("https://httpbin.org")
                .withEndPoint("/post")
                .withQueryParameter("utm_source", "javaquery")
                .withHttpPayload(new HttpRequest.HttpPayload(StringPool.UTF8, StringPool.MULTIPART_FORM_DATA, form))
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

                JSONObject headers = jsonObject.optJSONObject("headers");
                Assertions.assertEquals("faca52e4-1e15-11ec-9621-0242ac130002", headers.optString("Request-Id"));

                JSONObject args = jsonObject.optJSONObject("args");
                Assertions.assertEquals("javaquery", args.optString("utm_source"));

                JSONObject body = jsonObject.optJSONObject("form");
                Assertions.assertEquals("form-value", body.optString("form-key"));

                JSONObject files = jsonObject.optJSONObject("files");
                Assertions.assertEquals("Hello World!", files.optString("files"));
                return null;
            }

            @Override
            public void onMaxRetryAttempted(HttpResponse httpResponse) {}
        });
    }

    @Test
    public void performPostRequestWithoutPayload() {
        HttpRequest httpRequest = new HttpRequest.HttpRequestBuilder("PostRequest", HttpMethod.POST)
                .withHost("https://httpbin.org")
                .withEndPoint("/post")
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

                String github = jsonObject.optString("github");
                Assertions.assertNotNull(github);

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
                JSONObject jsonResponse = httpResponse.getJSONObjectBody();
                jsonResponse.put("github", "https://github.com/javaquery/httpclient");

                StringEntity updatedResponse = new StringEntity(jsonResponse.toString(), StringPool.UTF8);
                httpResponse.updateHttpResponse(updatedResponse);
            }

            @Override
            public void onError(
                    HttpExecutionContext httpExecutionContext, HttpRequest httpRequest, Exception exception) {}
        };
    }
}
