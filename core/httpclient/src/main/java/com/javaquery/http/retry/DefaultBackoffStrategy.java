package com.javaquery.http.retry;

import com.javaquery.http.HttpRequestResponse;

/**
 * The default backoff strategy you can use to delay request by 3, 6, 12, 48 seconds.
 * Maximum 120 will be permitted.
 *
 * @author javaquery
 * @since 1.0.0
 */
public class DefaultBackoffStrategy implements RetryPolicy.BackoffStrategy {

    private static final int BASE_SLIP_TIME_IN_MILLISECONDS = 3000;
    private static final int MAX_BACKOFF_IN_MILLISECONDS = 120 * 1000;

    @Override
    public long delayBeforeNextRetry(HttpRequestResponse httpRequestResponse, int retriesAttempted) {
        long delay = (1L << retriesAttempted) * BASE_SLIP_TIME_IN_MILLISECONDS;
        delay = Math.min(delay, MAX_BACKOFF_IN_MILLISECONDS);
        return delay;
    }
}
