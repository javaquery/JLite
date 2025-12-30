package com.javaquery.http;

import com.javaquery.http.handler.HttpResponseHandler;
import com.javaquery.http.retry.RetryPolicy;
import com.javaquery.util.collection.Collections;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static net.logstash.logback.marker.Markers.appendEntries;

/**
 * The Http client responsible for making http requests.
 *
 * @author javaquery
 * @since 1.0.0
 */
public class HttpClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClient.class);

    private static final int MAX_BACKOFF_IN_MILLISECONDS = 5 * 60 * 1000;

    /**
     * Execute.
     *
     * @param <R>                  the type parameter
     * @param httpExecutionContext the http execution context
     * @param httpRequest          the http request
     * @param httpResponseHandler  the http response handler
     * @return the r
     */
    public <R> R execute(HttpExecutionContext httpExecutionContext, HttpRequest httpRequest, HttpResponseHandler<R> httpResponseHandler) {
        final Map<String, String> originalHeaders = new HashMap<>(httpRequest.getHeaders());
        final Map<String, String> originalQueryParameters = new LinkedHashMap<>(httpRequest.getQueryParameters());
        HttpRequestResponse httpRequestResponse = new HttpRequestResponse(httpRequest);

        R responseHandlerResult = doExecute(httpExecutionContext, httpRequestResponse, httpResponseHandler);

        if (Objects.nonNull(httpRequest.getRetryPolicy())) {
            while (shouldRetry(httpRequest.getRetryPolicy(), httpRequestResponse)) {
                sleepFor(httpRequest.getRetryPolicy(), httpRequestResponse, httpRequestResponse.getRetriesAttempted());
                int retriesAttempted = httpRequestResponse.getRetriesAttempted();
                httpRequestResponse = new HttpRequestResponse(httpRequest);
                httpRequestResponse.setRetriesAttempted(retriesAttempted + 1);
                httpRequest.withHeaders(originalHeaders);
                httpRequest.withQueryParameter(originalQueryParameters);
                responseHandlerResult = doExecute(httpExecutionContext, httpRequestResponse, httpResponseHandler);
            }
            if (httpRequestResponse.getRetriesAttempted() == httpRequest.getRetryPolicy().getMaxErrorRetry()) {
                httpResponseHandler.onMaxRetryAttempted(httpRequestResponse.getHttpResponse());
            }
        }

        return responseHandlerResult;
    }

    /**
     * @param httpExecutionContext the http execution context
     * @param httpRequestResponse the http request response
     * @param httpResponseHandler the http response handler
     * @param <R> result type
     * @return result
     */
    private <R> R doExecute(HttpExecutionContext httpExecutionContext, HttpRequestResponse httpRequestResponse, HttpResponseHandler<R> httpResponseHandler) {
        HttpRequest httpRequest = httpRequestResponse.getHttpRequest();
        try {
            beforeRequest(httpExecutionContext, httpRequest);

            ApacheHttpRequestBuilder apacheHttpRequestBuilder = new ApacheHttpRequestBuilder(httpRequest);
            HttpUriRequest apacheHttpRequest = apacheHttpRequestBuilder.build();

            CloseableHttpClient closeableHttpClient = HttpClients.custom()
                    .setDefaultCredentialsProvider(apacheHttpRequestBuilder.credentialsProvider())
                    .build();
            org.apache.http.HttpResponse closeableHttpResponse = closeableHttpClient.execute(apacheHttpRequest);
            httpRequestResponse.setExecutionEndTime();
            HttpResponse httpResponse = new HttpResponse(closeableHttpResponse);
            afterResponse(httpExecutionContext, httpRequest, httpResponse);
            httpRequestResponse.setHttpResponse(httpResponse);
        } catch (Exception exception) {
            LOGGER.error(exception.getMessage(), exception);
            onError(httpExecutionContext, httpRequest, exception);
        } finally {
            LOGGER.info(appendEntries(httpRequestResponse.getAttributes()), null);
        }

        if (Objects.nonNull(httpResponseHandler) && shouldExecuteHttpResponseHandler(httpRequestResponse)) {
            return httpResponseHandler.onResponse(httpRequestResponse.getHttpResponse());
        }
        return null;
    }

    /**
     * process before request handler
     * @param httpExecutionContext the http execution context
     * @param httpRequest the http request
     */
    private void beforeRequest(HttpExecutionContext httpExecutionContext, HttpRequest httpRequest) {
        if (Collections.nonNullNonEmpty(httpExecutionContext.getHttpRequestHandlers())) {
            httpExecutionContext.getHttpRequestHandlers().forEach(httpRequestHandler -> httpRequestHandler.beforeRequest(httpExecutionContext, httpRequest));
        }
    }

    /**
     * process after response handler
     * @param httpExecutionContext the http execution context
     * @param httpRequest the http request
     * @param httpResponse the http response
     */
    private void afterResponse(HttpExecutionContext httpExecutionContext, HttpRequest httpRequest, HttpResponse httpResponse) {
        if (Collections.nonNullNonEmpty(httpExecutionContext.getHttpRequestHandlers())) {
            httpExecutionContext.getHttpRequestHandlers().forEach(httpRequestHandler -> httpRequestHandler.afterResponse(httpExecutionContext, httpRequest, httpResponse));
        }
    }

    /**
     * process error handler
     * @param httpExecutionContext the http execution context
     * @param httpRequest the http request
     * @param exception the exception
     */
    private void onError(HttpExecutionContext httpExecutionContext, HttpRequest httpRequest, Exception exception) {
        if (Collections.nonNullNonEmpty(httpExecutionContext.getHttpRequestHandlers())) {
            httpExecutionContext.getHttpRequestHandlers().forEach(httpRequestHandler -> httpRequestHandler.onError(httpExecutionContext, httpRequest, exception));
        }
    }

    /**
     * check when to execute response handler
     * @param httpRequestResponse the http request response
     * @return result true or false
     */
    private boolean shouldExecuteHttpResponseHandler(HttpRequestResponse httpRequestResponse) {
        return Objects.isNull(httpRequestResponse.getHttpRequest().getRetryPolicy())
                || !shouldRetry(httpRequestResponse.getHttpRequest().getRetryPolicy(), httpRequestResponse);
    }

    /**
     * check should we retry the request based on retry policy
     * @param retryPolicy the retry policy
     * @param httpRequestResponse the http request response
     * @return result true or false
     */
    private boolean shouldRetry(RetryPolicy retryPolicy, HttpRequestResponse httpRequestResponse) {
        boolean shouldRetryAttempted = retryPolicy.isRetryTillSucceed() || retryPolicy.getMaxErrorRetry() > httpRequestResponse.getRetriesAttempted();
        boolean shouldRetryCustomCondition = retryPolicy.getRetryCondition().shouldRetry(httpRequestResponse, httpRequestResponse.getRetriesAttempted());
        return shouldRetryAttempted && shouldRetryCustomCondition;
    }

    /**
     * Sleep for given milliseconds between retries
     * @param retryPolicy the retry policy
     * @param httpRequestResponse the http request response
     * @param retriesAttempted retries attempted
     */
    private void sleepFor(RetryPolicy retryPolicy, HttpRequestResponse httpRequestResponse, int retriesAttempted) {
        long delay = retryPolicy.getBackOffStrategy().delayBeforeNextRetry(httpRequestResponse, retriesAttempted);
        delay = Math.min(delay, MAX_BACKOFF_IN_MILLISECONDS);
        try {
            TimeUnit.MILLISECONDS.sleep(delay);
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
