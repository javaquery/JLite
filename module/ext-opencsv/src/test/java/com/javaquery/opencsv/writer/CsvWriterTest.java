package com.javaquery.opencsv.writer;

import static org.junit.jupiter.api.Assertions.*;

import com.javaquery.opencsv.model.Customer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * @author vicky.thakor
 * @since 2025-12-09
 */
public class CsvWriterTest {

    private File tempFile;

    @AfterEach
    public void cleanup() throws IOException {
        if (tempFile != null && tempFile.exists()) {
            Files.deleteIfExists(tempFile.toPath());
        }
    }

    @Test
    public void testWriteCsvWithNestedCollection() throws IOException {
        tempFile = File.createTempFile("customers", ".csv");
        CsvWriter.<Customer>builder()
                .headers(List.of("First Name", "Last Name", "Email", "Address Line 1", "City", "State"))
                .keys(List.of(
                        "firstName",
                        "lastName",
                        "email",
                        "addresses.addressLine1",
                        "addresses.city",
                        "addresses.state"))
                .data(Customer.fakeData(20, true))
                .toFile(tempFile)
                .write();

        // Verify file exists and has content
        assertTrue(tempFile.exists());
        assertTrue(tempFile.length() > 0);

        // Verify headers are written correctly
        try (BufferedReader reader = new BufferedReader(new FileReader(tempFile))) {
            String headerLine = reader.readLine();
            assertNotNull(headerLine);
            assertTrue(headerLine.contains("First Name"));
            assertTrue(headerLine.contains("Last Name"));
            assertTrue(headerLine.contains("Email"));
        }
    }

    @Test
    public void testWriteCsvWithCustomDelimiter() throws IOException {
        tempFile = File.createTempFile("customers_pipe", ".csv");

        CsvWriter.<Customer>builder()
                .headers(List.of("First Name", "Last Name", "Email", "Passport Number"))
                .keys(List.of("firstName", "lastName", "email", "passport.passportNumber"))
                .data(Customer.fakeData(20, false))
                .toFile(tempFile)
                .delimiter('|')
                .write();

        // Verify pipe delimiter is used
        try (BufferedReader reader = new BufferedReader(new FileReader(tempFile))) {
            String headerLine = reader.readLine();
            assertNotNull(headerLine);
            assertTrue(headerLine.contains("|"));
        }
    }

    @Test
    public void testWriteCsvWithoutHeader() throws IOException {
        tempFile = File.createTempFile("customers_noheader", ".csv");
        CsvWriter.<Customer>builder()
                .keys(List.of("firstName", "lastName", "email"))
                .data(Customer.fakeData(20, false))
                .toFile(tempFile)
                .includeHeader(false)
                .write();

        // Verify file exists
        assertTrue(tempFile.exists());

        // Count lines (should not have header)
        try (BufferedReader reader = new BufferedReader(new FileReader(tempFile))) {
            String firstLine = reader.readLine();
            assertNotNull(firstLine);
            // First line should be data, not headers
            assertFalse(firstLine.contains("First Name"));
        }
    }

    @Test
    public void testWriteCsvWithCustomQuoteChar() throws IOException {
        tempFile = File.createTempFile("customers_quote", ".csv");

        CsvWriter.<Customer>builder()
                .headers(List.of("First Name", "Last Name"))
                .keys(List.of("firstName", "lastName"))
                .data(Customer.fakeData(100, false))
                .toFile(tempFile)
                .quoteChar('\'')
                .write();

        assertTrue(tempFile.exists());
        assertTrue(tempFile.length() > 0);
    }

    @Test
    public void testWriteCsvWithEmptyData() throws IOException {
        tempFile = File.createTempFile("customers_empty", ".csv");

        // Should not throw exception, should just return without writing
        CsvWriter.<Customer>builder()
                .headers(List.of("First Name", "Last Name"))
                .keys(List.of("firstName", "lastName"))
                .data(new ArrayList<>())
                .toFile(tempFile)
                .write();

        // File should be empty
        assertEquals(0, tempFile.length());
    }

    @Test
    public void testWriteCsvThrowsExceptionWhenHeadersMissing() {
        tempFile = new File("test_no_headers.csv");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            CsvWriter.<Customer>builder()
                    .keys(List.of("firstName", "lastName"))
                    .data(Customer.fakeData(20, false))
                    .toFile(tempFile)
                    .includeHeader(true)
                    .write();
        });

        assertEquals("Headers must be provided when includeHeader is true", exception.getMessage());
    }

    @Test
    public void testWriteCsvThrowsExceptionWhenHeadersIsEmpty() {
        tempFile = new File("test_no_headers.csv");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            CsvWriter.<Customer>builder()
                    .headers(Collections.emptyList())
                    .keys(List.of("firstName", "lastName"))
                    .data(Customer.fakeData(20, false))
                    .toFile(tempFile)
                    .includeHeader(true)
                    .write();
        });

        assertEquals("Headers must be provided when includeHeader is true", exception.getMessage());
    }

    @Test
    public void testWriteCsvThrowsExceptionWhenKeysMissing() {
        tempFile = new File("test_no_keys.csv");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            CsvWriter.<Customer>builder()
                    .data(Customer.fakeData(1, true))
                    .toFile(tempFile)
                    .includeHeader(false)
                    .write();
        });

        assertEquals("Keys must be provided when includeHeader is false", exception.getMessage());
    }

    @Test
    public void testWriteCsvThrowsExceptionWhenKeysIsEmpty() {
        tempFile = new File("test_no_keys.csv");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            CsvWriter.<Customer>builder()
                    .keys(Collections.emptyList())
                    .data(Customer.fakeData(1, true))
                    .toFile(tempFile)
                    .includeHeader(false)
                    .write();
        });

        assertEquals("Keys must be provided when includeHeader is false", exception.getMessage());
    }

    @Test
    public void testWriteCsvThrowsExceptionWhenHeadersAndKeysSizeMismatch() {
        tempFile = new File("test_size_mismatch.csv");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            CsvWriter.<Customer>builder()
                    .headers(List.of("First Name", "Last Name", "Email"))
                    .keys(List.of("firstName", "lastName"))
                    .data(Customer.fakeData(1, false))
                    .toFile(tempFile)
                    .write();
        });

        assertEquals("Headers and Keys size must be same", exception.getMessage());
    }

    @Test
    public void testWriteCsvWithSimpleFields() throws IOException {
        tempFile = File.createTempFile("customers_simple", ".csv");

        CsvWriter.<Customer>builder()
                .headers(List.of("First Name", "Last Name", "Email"))
                .keys(List.of("firstName", "lastName", "email"))
                .data(Customer.fakeData(30, false))
                .toFile(tempFile)
                .write();

        assertTrue(tempFile.exists());

        // Verify content
        List<String> lines = Files.readAllLines(tempFile.toPath());
        assertTrue(lines.size() > 1); // At least header + 1 data row
        assertEquals("\"First Name\",\"Last Name\",\"Email\"", lines.get(0));
    }

    @Test
    public void testWriteCsvWithCustomLineEnd() throws IOException {
        tempFile = File.createTempFile("customers_lineend", ".csv");

        CsvWriter.<Customer>builder()
                .headers(List.of("First Name"))
                .keys(List.of("firstName"))
                .data(Customer.fakeData(20, false))
                .toFile(tempFile)
                .lineEnd("\r\n")
                .write();

        assertTrue(tempFile.exists());

        // Read as bytes to check line ending
        String content = Files.readString(tempFile.toPath());
        assertTrue(content.contains("\r\n"));
    }

    @Test
    public void testWriteCsvWithCustomEscapeChar() throws IOException {
        tempFile = File.createTempFile("customers_escape", ".csv");

        // Create a customer with data that needs escaping (contains quotes)
        Customer customer = Customer.builder()
                .firstName("John \"Johnny\"")
                .lastName("O'Brien")
                .email("john@example.com")
                .build();

        CsvWriter.<Customer>builder()
                .headers(List.of("First Name", "Last Name", "Email"))
                .keys(List.of("firstName", "lastName", "email"))
                .data(List.of(customer))
                .toFile(tempFile)
                .escapeChar('\\')
                .write();

        assertTrue(tempFile.exists());
        assertTrue(tempFile.length() > 0);

        // Verify the file was created and contains escaped content
        String content = Files.readString(tempFile.toPath());
        assertNotNull(content);
        assertTrue(content.contains("First Name"));
    }

    @Test
    public void testWriteCsvWithNullKey() throws IOException {
        tempFile = File.createTempFile("customers_pipe", ".csv");

        List<Customer> customers = Customer.fakeData(20, false);
        for (Customer customer : customers) {
            customer.setPassport(null);
        }

        CsvWriter.<Customer>builder()
                .headers(List.of("First Name", "Last Name", "Email", "Passport Number"))
                .keys(List.of("firstName", "lastName", "email", "passport.passportNumber"))
                .data(customers)
                .toFile(tempFile)
                .write();

        // Verify pipe delimiter is used
        try (BufferedReader reader = new BufferedReader(new FileReader(tempFile))) {
            String headerLine = reader.readLine();
            assertNotNull(headerLine);
        }
    }

    @Test
    public void testWriteCsvWithUnknownKey() throws IOException {
        tempFile = File.createTempFile("customers_pipe", ".csv");

        List<Customer> customers = Customer.fakeData(20, false);
        for (Customer customer : customers) {
            customer.setPassport(null);
        }

        CsvWriter.<Customer>builder()
                .headers(List.of("First Name", "Last Name", "Email", "Passport Number", "Country"))
                .keys(List.of("firstName", "lastName", "email", "invalid.key", "invalidKey"))
                .data(customers)
                .toFile(tempFile)
                .write();

        // Verify pipe delimiter is used
        try (BufferedReader reader = new BufferedReader(new FileReader(tempFile))) {
            String headerLine = reader.readLine();
            assertNotNull(headerLine);
        }
    }
}
