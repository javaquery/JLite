package com.javaquery.http.exception;

/**
 * Custom Http exception for the library
 *
 * @author javaquery
 * @since 1.0.0
 */
public class HttpException extends RuntimeException {

    /**
     * Instantiates a new Http exception.
     *
     * @param e the e
     */
    public HttpException(Exception e) {
        super(e);
    }
}
