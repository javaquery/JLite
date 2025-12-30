package com.javaquery.http;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Holds http request and response object.
 *
 * @author javaquery
 * @since 1.0.0
 */
@Getter
@Setter
public class HttpRequestResponse {
    private HttpRequest httpRequest;
    private HttpResponse httpResponse;
    private int retriesAttempted = 0;
    private long executionStartTime;
    private long executionEndTime;

    /**
     * Instantiates a new Http request response.
     *
     * @param httpRequest the http request
     */
    public HttpRequestResponse(HttpRequest httpRequest){
        this.httpRequest = httpRequest;
        this.executionStartTime = System.currentTimeMillis();
    }

    /**
     * Set execution end time and executionTime (milliseconds) will be added in attributes.
     */
    public void setExecutionEndTime(){
        executionEndTime = System.currentTimeMillis();
    }

    /**
     * Gets attributes.
     *
     * @return the attributes
     */
    public Map<String, Object> getAttributes() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(StringPool.LOG_ACTION, StringPool.LOG_HTTP_REQUEST);
        if(executionStartTime != 0 && executionEndTime != 0){
            attributes.put(StringPool.EXECUTION_TIME_MS, (executionEndTime - executionStartTime));
        }
        attributes.put(StringPool.LOG_HTTP_REQUEST, httpRequest);
        if(Objects.nonNull(httpResponse)){
            attributes.put(StringPool.LOG_HTTP_RESPONSE, httpResponse);
        }
        attributes.put(StringPool.RETRIES_ATTEMPTED, retriesAttempted);
        return attributes;
    }
}
