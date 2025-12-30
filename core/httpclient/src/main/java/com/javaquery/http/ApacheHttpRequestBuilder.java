package com.javaquery.http;

import com.javaquery.http.exception.HttpException;
import com.javaquery.util.Strings;
import com.javaquery.util.collection.Collections;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.AbstractContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class is responsible to wrap {@link HttpRequest} into apache http request.
 *
 * @author javaquery
 * @since 1.0.0
 */
class ApacheHttpRequestBuilder {
    private HttpUriRequest apacheHttpRequest;
    private final HttpRequest httpRequest;

    /**
     * Instantiates a new Apache http request builder.
     *
     * @param httpRequest the http request
     */
    public ApacheHttpRequestBuilder(HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    /**
     * Build GET, POST, PUT and DELETE http request
     *
     * @return the http uri request
     */
    public HttpUriRequest build() {
        if (httpRequest.getHttpMethod() == HttpMethod.GET) {
            if(Objects.nonNull(httpRequest.getHttpPayload())){
                HttpGetWithEntity httpGetWithEntity = new HttpGetWithEntity();
                httpGetWithEntity.setURI(httpRequest.httpRequestURI());
                httpGetWithEntity.setEntity(buildHttpEntity());
                apacheHttpRequest = httpGetWithEntity;
            }else{
                apacheHttpRequest = new HttpGet(httpRequest.httpRequestURI());
            }
        } else if (httpRequest.getHttpMethod() == HttpMethod.POST) {
            HttpPost httpPost = new HttpPost(httpRequest.httpRequestURI());
            httpPost.setEntity(buildHttpEntity());
            apacheHttpRequest = httpPost;
        } else if (httpRequest.getHttpMethod() == HttpMethod.PUT) {
            HttpPut httpPut = new HttpPut(httpRequest.httpRequestURI());
            httpPut.setEntity(buildHttpEntity());
            apacheHttpRequest = httpPut;
        } else if (httpRequest.getHttpMethod() == HttpMethod.DELETE) {
            apacheHttpRequest = new HttpDelete(httpRequest.httpRequestURI());
        }

        if (Collections.nonNullNonEmpty(httpRequest.getHeaders())) {
            httpRequest.getHeaders().forEach((key, value) -> apacheHttpRequest.setHeader(key, value));
        }
        return apacheHttpRequest;
    }

    /**
     * Credentials provider credentials provider.
     *
     * @return the credentials provider
     */
    protected CredentialsProvider credentialsProvider() {
        if (Strings.nonNullNonEmpty(httpRequest.getUsername())
                || Strings.nonNullNonEmpty(httpRequest.getPassword())) {
            UsernamePasswordCredentials usernamePasswordCredentials = new UsernamePasswordCredentials(httpRequest.getUsername(), httpRequest.getPassword());
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT), usernamePasswordCredentials);
            return credentialsProvider;
        }
        return null;
    }

    /**
     * Build payload of http request
     *
     * @return the HttpEntity
     */
    private HttpEntity buildHttpEntity() {
        if (Objects.nonNull(httpRequest.getHttpPayload())) {
            if (Objects.nonNull(httpRequest.getHttpPayload().getPayload())) {
                StringEntity stringEntity = new StringEntity(httpRequest.getHttpPayload().getPayload(), httpRequest.getHttpPayload().getCharset());
                httpRequest.withHeader(StringPool.CONTENT_TYPE, httpRequest.getHttpPayload().getContentType());
                return stringEntity;
            } else if (Collections.nonNullNonEmpty(httpRequest.getHttpPayload().getForm())) {
                if (StringPool.MULTIPART_FORM_DATA.equals(httpRequest.getHttpPayload().getContentType())
                        || httpRequest.getHttpPayload().getContentType().contains(StringPool.MULTIPART_FORM_DATA)) {
                    MultipartEntityBuilder builder = MultipartEntityBuilder.create();

                    httpRequest.getHttpPayload().getForm().forEach((key, value) -> {
                        if (value instanceof List) {
                            List values = (List) value;
                            for (Object o : values) {
                                builder.addPart(key, addFormPart(o));
                            }
                        } else {
                            builder.addPart(key, addFormPart(value));
                        }
                    });
                    return builder.build();
                } else {
                    List<NameValuePair> form = new ArrayList<>();
                    httpRequest.getHttpPayload().getForm().forEach((key, value) -> form.add(new BasicNameValuePair(key, (String) value)));
                    try {
                        return new UrlEncodedFormEntity(form, httpRequest.getHttpPayload().getCharset());
                    } catch (UnsupportedEncodingException e) {
                        throw new HttpException(e);
                    }
                }
            }
        }
        return null;
    }

    private AbstractContentBody addFormPart(Object value) {
        if (value instanceof File) {
            return new FileBody((File) value, ContentType.DEFAULT_BINARY);
        } else {
            return new StringBody((String) value, ContentType.MULTIPART_FORM_DATA);
        }
    }

    public static class HttpGetWithEntity extends HttpEntityEnclosingRequestBase {
        public final static String METHOD_NAME = "GET";

        @Override
        public String getMethod() {
            return METHOD_NAME;
        }
    }
}
