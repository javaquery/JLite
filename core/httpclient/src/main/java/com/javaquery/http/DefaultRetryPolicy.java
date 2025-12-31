package com.javaquery.http;

import com.javaquery.http.retry.DefaultBackoffStrategy;
import com.javaquery.http.retry.DefaultRetryCondition;
import com.javaquery.http.retry.RetryPolicy;

/**
 * Use this class for default server outage retry policy.
 * Interval between retry id defined in {@link DefaultBackoffStrategy}
 *
 * @author javaquery
 * @since 1.0.0
 */
public class DefaultRetryPolicy {

    /**
     * Get retry policy.
     *
     * @param maxErrorRetry the max error retry
     * @return the retry policy
     */
    public static RetryPolicy get(int maxErrorRetry) {
        return new RetryPolicy(new DefaultRetryCondition(), new DefaultBackoffStrategy(), maxErrorRetry);
    }
}
