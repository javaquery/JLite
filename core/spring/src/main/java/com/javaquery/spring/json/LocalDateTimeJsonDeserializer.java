package com.javaquery.spring.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import org.springframework.boot.jackson.JsonComponent;

/**
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
                return parseLocalDateTime(dateTimeString, ZoneId.systemDefault());
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

    /**
     * Parses the given date string into a LocalDateTime using the specified DateTimeFormat and ZoneId.
     *
     * @param date   the date string to parse
     * @param zoneId the ZoneId to consider during parsing
     * @return the parsed LocalDateTime, or null if parsing fails
     */
    private LocalDateTime parseLocalDateTime(String date, ZoneId zoneId) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            // Try to parse as ZonedDateTime first (for patterns with timezone info)
            try {
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(date, formatter.withZone(zoneId));
                return zonedDateTime.toLocalDateTime();
            } catch (Exception e) {
                // If that fails, try parsing as LocalDateTime directly
                return LocalDateTime.parse(date, formatter);
            }
        } catch (Exception e) {
            /* Silent exception - matches your original behavior */
        }
        return null;
    }
}
