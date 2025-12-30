package com.javaquery.http.retry;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.javaquery.http.HttpRequestResponse;
import com.javaquery.http.StringPool;
import lombok.Getter;

/**
 * The type Retry policy.
 *
 * @author javaquery
 * @since 1.0.0
 */
@Getter
public final class RetryPolicy {

    @JsonIgnore
    private final RetryCondition retryCondition;

    @JsonIgnore
    private final BackoffStrategy backOffStrategy;

    private final int maxErrorRetry;

    private boolean retryTillSuccess = false;

    /**
     * Instantiates a new Retry policy.
     *
     * @param retryCondition  the retry condition
     * @param backOffStrategy the back off strategy
     * @param maxErrorRetry   the max error retry
     */
    public RetryPolicy(RetryCondition retryCondition, BackoffStrategy backOffStrategy, int maxErrorRetry) {
        if (maxErrorRetry < 0) {
            throw new IllegalArgumentException(StringPool.ERROR_MAX_RETRY_VALUE);
        }
        this.retryCondition = retryCondition;
        this.backOffStrategy = backOffStrategy;
        this.maxErrorRetry = maxErrorRetry;
    }

    /**
     * Retry till succeed boolean.
     *
     * @return the boolean
     */
    public boolean isRetryTillSucceed() {
        return retryTillSuccess;
    }

    /**
     * Retry till success.
     */
    public void retryTillSuccess() {
        this.retryTillSuccess = true;
    }

    /**
     * Implement interface to provide retry condition on http response.
     */
    public interface RetryCondition {
        /**
         * Should retry boolean.
         *
         * @param httpRequestResponse the http request response
         * @param retriesAttempted    the retries attempted
         * @return the boolean
         */
        boolean shouldRetry(HttpRequestResponse httpRequestResponse, int retriesAttempted);
    }

    /**
     * Implement interface to define back off strategy for sub-sequent delayed retries.
     */
    public interface BackoffStrategy {
        /**
         * Delay before next retry long.
         *
         * @param httpRequestResponse the http request response
         * @param retriesAttempted    the retries attempted
         * @return the long
         */
        long delayBeforeNextRetry(HttpRequestResponse httpRequestResponse, int retriesAttempted);
    }
}
