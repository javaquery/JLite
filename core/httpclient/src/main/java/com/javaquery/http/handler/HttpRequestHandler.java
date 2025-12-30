package com.javaquery.http.handler;

import com.javaquery.http.HttpExecutionContext;
import com.javaquery.http.HttpRequest;
import com.javaquery.http.HttpResponse;

/**
 * Implement interface to intercept the http request.
 *
 * @author javaquery
 * @since 1.0.0
 */
public interface HttpRequestHandler {

    /**
     * Method will be called before http request.
     *
     * @param httpExecutionContext the http execution context
     * @param httpRequest          the http request
     */
    void beforeRequest(HttpExecutionContext httpExecutionContext, HttpRequest httpRequest);

    /**
     * Method will be called after http response received.
     *
     * @param httpExecutionContext the http execution context
     * @param httpRequest          the http request
     * @param httpResponse         the http response
     */
    void afterResponse(HttpExecutionContext httpExecutionContext, HttpRequest httpRequest, HttpResponse httpResponse);

    /**
     * Method will be called when exception occurs while processing http request.
     *
     * @param httpExecutionContext the http execution context
     * @param httpRequest          the http request
     * @param exception            the exception
     */
    void onError(HttpExecutionContext httpExecutionContext, HttpRequest httpRequest, Exception exception);
}
