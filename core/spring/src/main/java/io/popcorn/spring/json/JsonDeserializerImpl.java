package io.popcorn.spring.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

/**
 * Json deserializer that trims leading and trailing whitespace from string values during JSON deserialization.
 * @author vicky.thakor
 * @since 1.0.0
 */
@JsonComponent
public class JsonDeserializerImpl extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();
        return value == null ? null : value.trim();
    }
}
