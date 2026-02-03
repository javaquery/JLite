package com.javaquery.spring.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.jackson.JsonComponent;

/**
 * Json deserializer that trims leading and trailing whitespace from string values during JSON deserialization.
 * @author vicky.thakor
 * @since 1.0.0
 */
@JsonComponent
@ConditionalOnProperty(
        name = "javaquery.jackson.deserializer.string-trim.enabled",
        havingValue = "true",
        matchIfMissing = true)
public class StringJsonDeserializer extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();
        return value == null ? null : value.trim();
    }
}
