package com.javaquery.spring.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.javaquery.util.time.DatePattern;
import com.javaquery.util.time.LocalDates;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import org.springframework.boot.jackson.JsonComponent;

/**
 * Custom JSON deserializer for LocalDateTime that handles multiple date formats.
 * @author vicky.thakor
 * @since 1.0.0
 */
@JsonComponent
public class LocalDateTimeJsonDeserializer extends JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String dateTimeString = p.getText().trim();

        try {
            // Try parsing as ISO datetime with 'T': "2025-07-25T10:30:45"
            return LocalDateTime.parse(dateTimeString);
        } catch (DateTimeParseException e1) {
            try {
                // Try parsing with space separator: "2025-07-25 10:30:45"
                return LocalDates.parseLocalDateTime(dateTimeString, DatePattern.Y_M_D__HMS);
            } catch (DateTimeParseException e2) {
                try {
                    // Try parsing as date only: "2025-07-25"
                    return LocalDate.parse(dateTimeString).atStartOfDay();
                } catch (DateTimeParseException e3) {
                    throw new IOException(
                            "Unable to parse date/datetime: " + dateTimeString
                                    + ". Expected formats: 'yyyy-MM-dd'T'HH:mm:ss', 'yyyy-MM-dd HH:mm:ss', or 'yyyy-MM-dd'",
                            e3);
                }
            }
        }
    }
}
