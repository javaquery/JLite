package com.javaquery.http;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.javaquery.http.exception.HttpException;
import com.javaquery.http.retry.RetryPolicy;
import com.javaquery.util.collection.Collections;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The build Http request using the class.
 *
 * @author javaquery
 * @since 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HttpRequest {

    private final String httpRequestName;
    private final HttpMethod httpMethod;
    private final String username;
    @JsonIgnore
    private final String password;
    private final URI host;
    private final int port;
    private final String endPoint;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, String> headers;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, String> queryParameters;
    private final HttpPayload httpPayload;
    private final RetryPolicy retryPolicy;

    /**
     * Instantiates a new Http request.
     *
     * @param httpRequestName    the http request name
     * @param httpMethod         the http method
     * @param httpRequestBuilder the http request builder
     */
    protected HttpRequest(String httpRequestName, HttpMethod httpMethod, HttpRequestBuilder httpRequestBuilder) {
        this.httpRequestName = httpRequestName;
        this.httpMethod = httpMethod;
        this.username = httpRequestBuilder.username;
        this.password = httpRequestBuilder.password;
        this.host = httpRequestBuilder.host;
        this.port = httpRequestBuilder.port;
        this.endPoint = httpRequestBuilder.endPoint;
        this.headers = httpRequestBuilder.headers;
        this.queryParameters = httpRequestBuilder.queryParameters;
        this.httpPayload = httpRequestBuilder.httpPayload;
        this.retryPolicy = httpRequestBuilder.retryPolicy;
    }

    /**
     * Gets http request name.
     *
     * @return the http request name
     */
    public String getHttpRequestName() {
        return httpRequestName;
    }

    /**
     * Gets http method.
     *
     * @return the http method
     */
    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    /**
     * Gets username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets host.
     *
     * @return the host
     */
    public URI getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    /**
     * Gets end point.
     *
     * @return the end point
     */
    public String getEndPoint() {
        return endPoint;
    }

    /**
     * Gets headers.
     *
     * @return the headers
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * With header http request.
     *
     * @param key   the key
     * @param value the value
     * @return the http request
     */
    public HttpRequest withHeader(String key, String value) {
        if (Collections.nullOrEmpty(headers)) {
            headers = new HashMap<>();
        }
        this.headers.put(key, value);
        return this;
    }

    /**
     * With headers http request.
     *
     * @param headers the headers
     * @return the http request
     */
    public HttpRequest withHeaders(Map<String, String> headers) {
        this.headers.putAll(headers);
        return this;
    }

    /**
     * With query parameter http request.
     *
     * @param key   the key
     * @param value the value
     * @return the http request
     */
    public HttpRequest withQueryParameter(String key, String value) {
        this.queryParameters.put(key, value);
        return this;
    }

    /**
     * With query parameter http request.
     *
     * @param queryParameters the query parameters
     * @return the http request
     */
    public HttpRequest withQueryParameter(Map<String, String> queryParameters) {
        this.queryParameters.putAll(queryParameters);
        return this;
    }

    /**
     * Gets query parameters.
     *
     * @return the query parameters
     */
    public Map<String, String> getQueryParameters() {
        return queryParameters;
    }

    /**
     * Gets http payload.
     *
     * @return the http payload
     */
    public HttpPayload getHttpPayload() {
        return httpPayload;
    }

    /**
     * Gets retry policy.
     *
     * @return the retry policy
     */
    public RetryPolicy getRetryPolicy() {
        return retryPolicy;
    }

    /**
     * Build http request complete URI with parameters
     *
     * @return the URI
     */
    public URI httpRequestURI() {
        try {
            URIBuilder uriBuilder = new URIBuilder(getHost());
            if (getPort() != 0) {
                uriBuilder.setPort(getPort());
            }
            uriBuilder.setPath(getEndPoint());
            if (Collections.nonNullNonEmpty(getQueryParameters())) {
                getQueryParameters().forEach(uriBuilder::addParameter);
            }
            return uriBuilder.build();
        } catch (URISyntaxException e) {
            throw new HttpException(e);
        }
    }

    /**
     * The type Http payload.
     * Use HttpPayload(String charset, String contentType, String payload) for String based payload.
     * Use HttpPayload(String charset, String contentType, Map&lt;String, Object&gt; form) for form based payload.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class HttpPayload {
        private final String charset;
        private final String contentType;
        private final String payload;
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private final Map<String, Object> form;

        /**
         * Instantiates a new Http payload.
         *
         * @param charset     the charset
         * @param contentType the content type
         * @param payload     the payload
         */
        public HttpPayload(String charset, String contentType, String payload) {
            this.charset = charset;
            this.contentType = contentType;
            this.payload = payload;
            this.form = null;
        }

        /**
         * Instantiates a new Http payload.
         *
         * @param charset     the charset
         * @param contentType the content type
         * @param form        the form
         */
        public HttpPayload(String charset, String contentType, Map<String, Object> form) {
            this.charset = charset;
            this.contentType = contentType;
            this.form = form;
            this.payload = null;
        }

        /**
         * Gets charset.
         *
         * @return the charset
         */
        public String getCharset() {
            return charset;
        }

        /**
         * Gets content type.
         *
         * @return the content type
         */
        public String getContentType() {
            return contentType;
        }

        /**
         * Gets payload.
         *
         * @return the payload
         */
        public String getPayload() {
            return payload;
        }

        /**
         * Gets form.
         *
         * @return the form
         */
        public Map<String, Object> getForm() {
            return form;
        }
    }

    /**
     * The type Http request builder.
     */
    public static final class HttpRequestBuilder {
        private final String httpRequestName;
        private final HttpMethod httpMethod;
        private String username;
        private String password;
        private URI host;
        private int port;
        private String endPoint;
        private Map<String, String> headers;
        private Map<String, String> queryParameters;
        private HttpPayload httpPayload;
        private RetryPolicy retryPolicy;

        /**
         * Instantiates a new Http request builder.
         *
         * @param httpRequestName the http request name
         * @param httpMethod      the http method
         */
        public HttpRequestBuilder(String httpRequestName, HttpMethod httpMethod) {
            this.httpRequestName = httpRequestName;
            this.httpMethod = httpMethod;
            this.headers = new HashMap<>();
            this.queryParameters = new LinkedHashMap<>();
        }

        /**
         * With username password http request builder.
         *
         * @param username the username
         * @param password the password
         * @return the http request builder
         */
        public HttpRequestBuilder withUsernamePassword(String username, String password) {
            this.username = username;
            this.password = password;
            return this;
        }

        /**
         * With host http request builder.
         *
         * @param host the host
         * @return the http request builder
         */
        public HttpRequestBuilder withHost(String host) {
            try {
                this.host = new URI(host);
            } catch (URISyntaxException e) {
                throw new HttpException(e);
            }
            return this;
        }

        public HttpRequestBuilder withPort(int port) {
            this.port = port;
            return this;
        }

        /**
         * With end point http request builder.
         *
         * @param endPoint the end point
         * @return the http request builder
         */
        public HttpRequestBuilder withEndPoint(String endPoint) {
            this.endPoint = endPoint;
            return this;
        }

        /**
         * With header http request builder.
         *
         * @param key   the key
         * @param value the value
         * @return the http request builder
         */
        public HttpRequestBuilder withHeader(String key, String value) {
            this.headers.put(key, value);
            return this;
        }

        /**
         * With headers http request builder.
         *
         * @param headers the headers
         * @return the http request builder
         */
        public HttpRequestBuilder withHeaders(Map<String, String> headers) {
            this.headers.putAll(headers);
            return this;
        }

        /**
         * With query parameter http request builder.
         *
         * @param key   the key
         * @param value the value
         * @return the http request builder
         */
        public HttpRequestBuilder withQueryParameter(String key, String value) {
            this.queryParameters.put(key, value);
            return this;
        }

        /**
         * With query parameter http request builder.
         *
         * @param queryParameters the query parameters
         * @return the http request builder
         */
        public HttpRequestBuilder withQueryParameter(Map<String, String> queryParameters) {
            this.queryParameters.putAll(queryParameters);
            return this;
        }

        /**
         * With http payload http request builder.
         *
         * @param httpPayload the http payload
         * @return the http request builder
         */
        public HttpRequestBuilder withHttpPayload(HttpPayload httpPayload) {
            this.httpPayload = httpPayload;
            return this;
        }

        /**
         * With retry policy http request builder.
         *
         * @param retryPolicy the retry policy
         * @return the http request builder
         */
        public HttpRequestBuilder withRetryPolicy(RetryPolicy retryPolicy) {
            this.retryPolicy = retryPolicy;
            return this;
        }

        /**
         * Build http request.
         *
         * @return the http request
         */
        public HttpRequest build() {
            return new HttpRequest(httpRequestName, httpMethod, this);
        }
    }
}
