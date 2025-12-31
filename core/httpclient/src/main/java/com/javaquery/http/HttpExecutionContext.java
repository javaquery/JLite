package com.javaquery.http;

import com.javaquery.http.handler.HttpRequestHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Http execution context to hold extra data/information for http request and response.
 * Use this class to provide {@link HttpRequestHandler}.
 *
 * @author javaquery
 * @since 1.0.0
 */
public class HttpExecutionContext {

    private Map<String, Object> metaData;
    private List<HttpRequestHandler> httpRequestHandlers;

    /**
     * Instantiates a new Http execution context.
     */
    public HttpExecutionContext() {
        this.metaData = new HashMap<>();
        this.httpRequestHandlers = new ArrayList<>();
    }

    /**
     * Gets meta data.
     *
     * @return the meta data
     */
    public Map<String, Object> getMetaData() {
        return metaData;
    }

    /**
     * Get meta object.
     *
     * @param key          the key
     * @param defaultValue the default value
     * @return the object
     */
    public Object getMeta(String key, Object defaultValue) {
        return metaData.getOrDefault(key, defaultValue);
    }

    /**
     * Sets meta data.
     *
     * @param metaData the meta data
     */
    public void setMetaData(Map<String, Object> metaData) {
        this.metaData = metaData;
    }

    /**
     * Add meta data.
     *
     * @param key   the key
     * @param value the value
     */
    public void addMetaData(String key, Object value) {
        this.metaData.put(key, value);
    }

    /**
     * Gets http request handlers.
     *
     * @return the http request handlers
     */
    public List<HttpRequestHandler> getHttpRequestHandlers() {
        return httpRequestHandlers;
    }

    /**
     * Sets http request handlers.
     *
     * @param httpRequestHandlers the http request handlers
     */
    public void setHttpRequestHandlers(List<HttpRequestHandler> httpRequestHandlers) {
        this.httpRequestHandlers = httpRequestHandlers;
    }

    /**
     * Add http request handler.
     *
     * @param httpRequestHandler the http request handler
     */
    public void addHttpRequestHandler(HttpRequestHandler httpRequestHandler) {
        this.httpRequestHandlers.add(httpRequestHandler);
    }
}
