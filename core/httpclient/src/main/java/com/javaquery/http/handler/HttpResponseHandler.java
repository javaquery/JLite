package com.javaquery.http.handler;

import com.javaquery.http.HttpResponse;

/**
 * Implement interface to process http response received.
 *
 * @param <R> the type parameter
 * @author javaquery
 * @since 1.0.0
 */
public interface HttpResponseHandler<R> {
    /**
     * Method will be called when http response received for http request
     *
     * @param httpResponse the http response
     * @return the r
     */
    R onResponse(HttpResponse httpResponse);

    /**
     * Method will be called when maximum retries performed on request as per the {@link com.javaquery.http.retry.RetryPolicy}.
     *
     * @param httpResponse the http response
     */
    void onMaxRetryAttempted(HttpResponse httpResponse);
}
