package com.javaquery.spring.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author vicky.thakor
 * @since 1.0.1
 */
@Service
public class ObjectMapperService {

    private final ObjectMapper objectMapper;
    private final ObjectMapper snakeCaseObjectMapper;

    public ObjectMapperService(
            ObjectMapper objectMapper, @Qualifier("snakeCaseObjectMapper") ObjectMapper snakeCaseObjectMapper) {
        this.objectMapper = objectMapper;
        this.snakeCaseObjectMapper = snakeCaseObjectMapper;
    }

    /**
     * Convert object to JSON string.
     *
     * @param obj the object to convert
     * @return the JSON string representation of the object, or null if conversion fails
     */
    public String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Convert object to JSON string with snake_case property names.
     *
     * @param obj the object to convert
     * @return the JSON string representation of the object in snake_case, or null if conversion fails
     */
    public String toSnakeCaseJson(Object obj) {
        try {
            return snakeCaseObjectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Convert an object from one type to another.
     *
     * @param fromValue   the object to convert
     * @param toValueType the target class type
     * @param <T>         the type of the target class
     * @return the converted object
     */
    public <T> T convertValue(Object fromValue, Class<T> toValueType) {
        return objectMapper.convertValue(fromValue, toValueType);
    }

    /**
     * Read a JSON string and convert it to an object of the specified type.
     *
     * @param string      the JSON string to read
     * @param toValueType the target class type
     * @param <T>         the type of the target class
     * @return the converted object, or null if conversion fails
     */
    public <T> T readValue(String string, Class<T> toValueType) {
        try {
            return objectMapper.readValue(string, toValueType);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    /**
     * Read a JSON string with snake_case property names and convert it to an object of the specified type.
     *
     * @param string      the JSON string to read
     * @param toValueType the target class type
     * @param <T>         the type of the target class
     * @return the converted object, or null if conversion fails
     */
    public <T> T readSnakeCaseValue(String string, Class<T> toValueType) {
        try {
            return snakeCaseObjectMapper.readValue(string, toValueType);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
