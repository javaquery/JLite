package com.javaquery.http.retry;

import com.javaquery.http.HttpRequestResponse;
import java.util.Objects;

/**
 * The default retry condition you can use for server outage error.
 * Http status code 500 and 503 are supported for now.
 *
 * @author javaquery
 * @since 1.0.0
 */
public class DefaultRetryCondition implements RetryPolicy.RetryCondition {

    @Override
    public boolean shouldRetry(HttpRequestResponse httpRequestResponse, int retriesAttempted) {
        return isServiceOutage(httpRequestResponse);
    }

    private boolean isServiceOutage(HttpRequestResponse httpRequestResponse) {
        if (Objects.isNull(httpRequestResponse) || Objects.isNull(httpRequestResponse.getHttpResponse())) {
            return false;
        }
        return httpRequestResponse.getHttpResponse().getStatusCode() == 500
                || httpRequestResponse.getHttpResponse().getStatusCode() == 503;
    }
}
