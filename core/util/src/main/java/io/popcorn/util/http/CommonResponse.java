package io.popcorn.util.http;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @author vicky.thakor
 * @since 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResponse<T> implements Serializable {

    private final int statusCode;
    private final String message;
    private final T payload;
    private final List<String> errorMessages;
    private Integer page;
    private Integer limit;
    private Long total;

    private CommonResponse(int statusCode, String message, T payload, List<String> errorMessages) {
        this.statusCode = statusCode;
        this.message = message;
        this.payload = payload;
        this.errorMessages = errorMessages;
    }

    public static <T> CommonResponse<T> ok(T payload){
        return CommonResponse.of(HttpStatusCode.OK, payload);
    }

    public static <T> CommonResponse<T> of(HttpStatusCode statusCode, String message, T payload){
        return new CommonResponse<>(statusCode.value(), message, payload, Collections.emptyList());
    }

    public static <T> CommonResponse<T> of(HttpStatusCode statusCode, String message){
        return new CommonResponse<>(statusCode.value(), message, null, Collections.emptyList());
    }

    public static <T> CommonResponse<T> of(HttpStatusCode statusCode, T payload){
        return new CommonResponse<>(statusCode.value(), null, payload, Collections.emptyList());
    }

    public static <T> CommonResponse<T> of(HttpStatusCode statusCode, List<String> errorMessages){
        return new CommonResponse<>(statusCode.value(), null, null, errorMessages);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public T getPayload() {
        return payload;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public Integer getPage() {
        return page;
    }

    public CommonResponse<T> withPage(Integer page){
        this.page = page;
        return this;
    }

    public Integer getLimit() {
        return limit;
    }

    public CommonResponse<T> withLimit(Integer limit){
        this.limit = limit;
        return this;
    }

    public Long getTotal() {
        return total;
    }

    public CommonResponse<T> withTotal(Long total){
        this.total = total;
        return this;
    }
}