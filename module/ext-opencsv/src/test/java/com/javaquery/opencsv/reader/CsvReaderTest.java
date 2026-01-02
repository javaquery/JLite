package com.javaquery.opencsv.reader;

import static org.junit.jupiter.api.Assertions.*;

import com.javaquery.helper.BatchProcessor;
import com.javaquery.opencsv.model.Customer;
import com.javaquery.opencsv.writer.CsvWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Comprehensive test cases for CsvReader
 * @author vicky.thakor
 * @since 1.0.0
 */
public class CsvReaderTest {

    private static final List<String> DEFAULT_HEADERS = List.of("First Name", "Last Name", "Email");
    private static final List<String> DEFAULT_KEYS = List.of("firstName", "lastName", "email");
    private File tempCsvFile;

    @BeforeEach
    public void setup() throws IOException {
        tempCsvFile = File.createTempFile("test_csv", ".csv");
    }

    @AfterEach
    public void cleanup() throws IOException {
        if (tempCsvFile != null && tempCsvFile.exists()) {
            Files.deleteIfExists(tempCsvFile.toPath());
        }
    }

    @Test
    public void testReadCsvWithBasicData() throws IOException {
        List<Customer> fakeData = createTestCsvFile(DEFAULT_HEADERS, DEFAULT_KEYS, ',', 3);

        List<Customer> allResults = new ArrayList<>();
        AtomicInteger batchCount = new AtomicInteger(0);

        CsvReader.<Customer>builder()
                .source(tempCsvFile)
                .rowTransformer((headers, rowValues, previousRow) -> Customer.builder()
                        .firstName(rowValues[0])
                        .lastName(rowValues[1])
                        .email(rowValues[2])
                        .build())
                .batchProcessor(batch -> {
                    allResults.addAll(batch);
                    batchCount.incrementAndGet();
                })
                .batchSize(2)
                .read();

        // Verify results
        assertEquals(3, allResults.size());
        assertEquals(fakeData.get(0).getFirstName(), allResults.get(0).getFirstName());
        assertEquals(fakeData.get(0).getLastName(), allResults.get(0).getLastName());
        assertEquals(fakeData.get(0).getEmail(), allResults.get(0).getEmail());

        assertEquals(fakeData.get(1).getFirstName(), allResults.get(1).getFirstName());
        assertEquals(fakeData.get(2).getFirstName(), allResults.get(2).getFirstName());

        // Should have 2 batches (2 items + 1 item)
        assertEquals(2, batchCount.get());
    }

    @Test
    public void testReadCsvWithLargeBatchSize() throws IOException {
        createTestCsvFile(List.of("Name", "Age"), List.of("firstName", "age"), ',', 2);

        List<Customer> allResults = new ArrayList<>();
        AtomicInteger batchCount = new AtomicInteger(0);

        CsvReader.<Customer>builder()
                .source(tempCsvFile)
                .rowTransformer((headers, rowValues, previousRow) -> Customer.builder()
                        .firstName(rowValues[0])
                        .age(Integer.parseInt(rowValues[1]))
                        .build())
                .batchProcessor(batch -> {
                    allResults.addAll(batch);
                    batchCount.incrementAndGet();
                })
                .batchSize(1000)
                .read();

        // With batch size larger than data, should have 1 batch
        assertEquals(2, allResults.size());
        assertEquals(1, batchCount.get());
    }

    @Test
    public void testReadCsvWithOnCompleteCallback() throws IOException {
        createTestCsvFile(List.of("Name", "Email"), List.of("firstName", "email"), ',', 3);

        AtomicInteger totalProcessed = new AtomicInteger(0);
        AtomicInteger totalBatches = new AtomicInteger(0);

        CsvReader.<Customer>builder()
                .source(tempCsvFile)
                .rowTransformer((headers, rowValues, previousRow) -> Customer.builder()
                        .firstName(rowValues[0])
                        .email(rowValues[1])
                        .build())
                .batchProcessor(new BatchProcessor<Customer>() {
                    @Override
                    public void onBatch(List<Customer> batch) {
                        // Process batch
                    }

                    @Override
                    public void onComplete(int total, int batches) {
                        totalProcessed.set(total);
                        totalBatches.set(batches);
                    }
                })
                .batchSize(2)
                .read();

        assertEquals(3, totalProcessed.get());
        assertEquals(2, totalBatches.get()); // 2 items + 1 item = 2 batches
    }

    @Test
    public void testReadCsvWithNullTransform() throws IOException {
        // Test when rowTransformer returns null (skips row)
        createTestCsvFile("Name,Age", "Alice,30", "Bob,invalid", "Charlie,25");

        List<Customer> allResults = new ArrayList<>();

        CsvReader.<Customer>builder()
                .source(tempCsvFile)
                .rowTransformer((headers, rowValues, previousRow) -> {
                    Customer customer =
                            Customer.builder().firstName(rowValues[0]).build();
                    try {
                        customer.setAge(Integer.parseInt(rowValues[1]));
                        return customer;
                    } catch (NumberFormatException e) {
                        // Skip invalid rows
                        return null;
                    }
                })
                .batchProcessor(new com.javaquery.helper.BatchProcessor<Customer>() {
                    @Override
                    public void onBatch(List<Customer> batch) {
                        allResults.addAll(batch);
                    }
                })
                .batchSize(10)
                .read();

        // Should only have 2 valid results (Bob's row was skipped)
        assertEquals(2, allResults.size());
        assertEquals("Alice", allResults.get(0).getFirstName());
        assertEquals("Charlie", allResults.get(1).getFirstName());
    }

    @Test
    public void testReadCsvWithEmptyFile() throws IOException {
        // Create empty CSV file with only headers
        createTestCsvFile(List.of("Name", "Email"), List.of("firstName", "email"), ',', 0);

        List<Customer> allResults = new ArrayList<>();
        AtomicInteger batchCount = new AtomicInteger(0);

        CsvReader.<Customer>builder()
                .source(tempCsvFile)
                .rowTransformer((headers, rowValues, previousRow) -> Customer.builder()
                        .firstName(rowValues[0])
                        .email(rowValues[1])
                        .build())
                .batchProcessor(batch -> {
                    allResults.addAll(batch);
                    batchCount.incrementAndGet();
                })
                .read();

        // Should have no results and no batches processed
        assertEquals(0, allResults.size());
        assertEquals(0, batchCount.get());
    }

    @Test
    public void testReadCsvWithExactBatchSize() throws IOException {
        // Test when data size exactly matches batch size
        createTestCsvFile(List.of("Name", "Email"), List.of("firstName", "email"), ',', 3);

        List<Customer> allResults = new ArrayList<>();
        AtomicInteger batchCount = new AtomicInteger(0);

        CsvReader.<Customer>builder()
                .source(tempCsvFile)
                .rowTransformer((headers, rowValues, previousRow) -> Customer.builder()
                        .firstName(rowValues[0])
                        .email(rowValues[1])
                        .build())
                .batchProcessor(batch -> {
                    allResults.addAll(batch);
                    batchCount.incrementAndGet();
                })
                .batchSize(3)
                .read();

        assertEquals(3, allResults.size());
        assertEquals(1, batchCount.get()); // Exactly 1 batch
    }

    @Test
    public void testReadCsvWithSingleRow() throws IOException {
        List<Customer> fakeData = createTestCsvFile(List.of("Name", "Email"), List.of("firstName", "email"), ',', 1);

        List<Customer> allResults = new ArrayList<>();

        CsvReader.<Customer>builder()
                .source(tempCsvFile)
                .rowTransformer((headers, rowValues, previousRow) -> Customer.builder()
                        .firstName(rowValues[0])
                        .email(rowValues[1])
                        .build())
                .batchProcessor(allResults::addAll)
                .batchSize(10)
                .read();

        assertEquals(1, allResults.size());
        assertEquals(fakeData.get(0).getFirstName(), allResults.get(0).getFirstName());
    }

    @Test
    public void testReadCsvWithQuotedValues() throws IOException {
        // Test CSV with quoted values containing special characters
        createTestCsvFile(
                "Name,Description", "\"Alice\",\"A person with, commas\"", "\"Bob\",\"A person with \"\"quotes\"\"\"");

        List<Customer> allResults = new ArrayList<>();

        CsvReader.<Customer>builder()
                .source(tempCsvFile)
                .rowTransformer((headers, rowValues, previousRow) -> Customer.builder()
                        .firstName(rowValues[0])
                        .about(rowValues[1])
                        .build())
                .batchProcessor(batch -> allResults.addAll(batch))
                .read();

        assertEquals(2, allResults.size());
        assertEquals("Alice", allResults.get(0).getFirstName());
        assertTrue(allResults.get(0).getAbout().contains("commas"));
    }

    @Test
    public void testReadCsvThrowsExceptionWhenSourceMissing() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            CsvReader.<Customer>builder()
                    .rowTransformer((headers, rowValues, previousRow) -> new Customer())
                    .batchProcessor(batch -> {})
                    .read();
        });

        assertEquals("Source file must be provided", exception.getMessage());
    }

    @Test
    public void testReadCsvThrowsExceptionWhenTransformerMissing() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            CsvReader.<Customer>builder()
                    .source(tempCsvFile)
                    .batchProcessor(batch -> {})
                    .read();
        });

        assertEquals("Row transformer must be provided", exception.getMessage());
    }

    @Test
    public void testReadCsvThrowsExceptionWhenBatchProcessorMissing() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            CsvReader.<Customer>builder()
                    .source(tempCsvFile)
                    .rowTransformer((headers, rowValues, previousRow) -> new Customer())
                    .read();
        });

        assertEquals("Batch processor must be provided", exception.getMessage());
    }

    @Test
    public void testReadCsvWithMultipleBatchesVerifyBatchSizes() throws IOException {
        createTestCsvFile(List.of("Name", "Email"), List.of("firstName", "email"), ',', 7);

        List<Integer> batchSizes = new ArrayList<>();
        CsvReader.<Customer>builder()
                .source(tempCsvFile)
                .rowTransformer((headers, rowValues, previousRow) -> Customer.builder()
                        .firstName(rowValues[0])
                        .email(rowValues[1])
                        .build())
                .batchProcessor(batch -> batchSizes.add(batch.size()))
                .batchSize(3)
                .read();

        // Should have 3 batches: 3, 3, 1
        assertEquals(3, batchSizes.size());
        assertEquals(3, batchSizes.get(0));
        assertEquals(3, batchSizes.get(1));
        assertEquals(1, batchSizes.get(2));
    }

    @Test
    public void testReadCsvWithHeaderAccess() throws IOException {
        createTestCsvFile(List.of("FirstName", "LastName", "Email"), List.of("firstName", "lastName", "email"), ',', 1);

        List<String> headersCaptured = new ArrayList<>();

        CsvReader.<Customer>builder()
                .source(tempCsvFile)
                .rowTransformer((headers, rowValues, previousRow) -> {
                    if (headersCaptured.isEmpty()) {
                        // Capture headers on first row
                        Collections.addAll(headersCaptured, headers);
                    }
                    return Customer.builder()
                            .firstName(rowValues[0])
                            .lastName(rowValues[1])
                            .email(rowValues[2])
                            .build();
                })
                .batchProcessor(batch -> {
                    // Process batch
                })
                .read();

        // Verify headers were passed correctly
        assertEquals(3, headersCaptured.size());
        assertEquals("FirstName", headersCaptured.get(0));
        assertEquals("LastName", headersCaptured.get(1));
        assertEquals("Email", headersCaptured.get(2));
    }

    @Test
    public void testReadCsvWithCustomDelimiter() throws IOException {
        // Test with pipe delimiter
        List<Customer> fakeData = createTestCsvFile(List.of("Name", "Email"), List.of("firstName", "email"), '|', 3);

        List<Customer> allResults = new ArrayList<>();

        CsvReader.<Customer>builder()
                .source(tempCsvFile)
                .delimiter('|')
                .rowTransformer((headers, rowValues, previousRow) -> Customer.builder()
                        .firstName(rowValues[0])
                        .email(rowValues[1])
                        .build())
                .batchProcessor(allResults::addAll)
                .read();

        assertEquals(3, allResults.size());
        assertEquals(fakeData.get(0).getFirstName(), allResults.get(0).getFirstName());
        assertEquals(fakeData.get(0).getEmail(), allResults.get(0).getEmail());
    }

    @Test
    public void testReadCsvWithTabDelimiter() throws IOException {
        // Test with tab delimiter
        List<Customer> fakeData =
                createTestCsvFile(List.of("Name", "Email", "Age"), List.of("firstName", "email", "age"), '\t', 2);

        List<Customer> allResults = new ArrayList<>();

        CsvReader.<Customer>builder()
                .source(tempCsvFile)
                .delimiter('\t')
                .rowTransformer((headers, rowValues, previousRow) -> Customer.builder()
                        .firstName(rowValues[0])
                        .email(rowValues[1])
                        .age(Integer.parseInt(rowValues[2]))
                        .build())
                .batchProcessor(allResults::addAll)
                .read();

        assertEquals(2, allResults.size());
        assertEquals(fakeData.get(0).getFirstName(), allResults.get(0).getFirstName());
        assertEquals(fakeData.get(0).getAge(), allResults.get(0).getAge());
    }

    @Test
    public void testReadCsvWithSemicolonDelimiter() throws IOException {
        // Test with semicolon delimiter (common in European locales)
        List<Customer> fakeData = createTestCsvFile(List.of("Name", "Email"), List.of("firstName", "email"), ';', 2);

        List<Customer> allResults = new ArrayList<>();

        CsvReader.<Customer>builder()
                .source(tempCsvFile)
                .delimiter(';')
                .rowTransformer((headers, rowValues, previousRow) -> Customer.builder()
                        .firstName(rowValues[0])
                        .email(rowValues[1])
                        .build())
                .batchProcessor(allResults::addAll)
                .read();

        assertEquals(2, allResults.size());
        assertEquals(fakeData.get(0).getEmail(), allResults.get(0).getEmail());
    }

    @Test
    public void testReadCsvWithCustomQuoteChar() throws IOException {
        // Test with single quote as quote character
        createTestCsvFile("Name,Description", "Alice,'Contains, comma'", "Bob,'Contains ''quote'''");

        List<Customer> allResults = new ArrayList<>();

        CsvReader.<Customer>builder()
                .source(tempCsvFile)
                .quoteChar('\'')
                .rowTransformer((headers, rowValues, previousRow) -> Customer.builder()
                        .firstName(rowValues[0])
                        .about(rowValues[1])
                        .build())
                .batchProcessor(allResults::addAll)
                .read();

        assertEquals(2, allResults.size());
        assertEquals("Alice", allResults.get(0).getFirstName());
        assertTrue(allResults.get(0).getAbout().contains("comma"));
        assertEquals("Bob", allResults.get(1).getFirstName());
    }

    @Test
    public void testReadCsvWithCustomEscapeChar() throws IOException {
        // Test with custom escape character
        createTestCsvFile("Name,Description", "Alice,\"Contains \\\"escaped\\\" quotes\"", "Bob,\"Normal text\"");

        List<Customer> allResults = new ArrayList<>();

        CsvReader.<Customer>builder()
                .source(tempCsvFile)
                .escapeChar('\\')
                .rowTransformer((headers, rowValues, previousRow) -> Customer.builder()
                        .firstName(rowValues[0])
                        .about(rowValues[1])
                        .build())
                .batchProcessor(allResults::addAll)
                .read();

        assertEquals(2, allResults.size());
        assertEquals("Alice", allResults.get(0).getFirstName());
    }

    @Test
    public void testReadCsvWithSkipLines() throws IOException {
        // Create CSV with comment lines at the top
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempCsvFile))) {
            writer.write("# This is a comment line");
            writer.newLine();
            writer.write("# Another comment");
            writer.newLine();
            writer.write("Name,Email");
            writer.newLine();
            writer.write("Alice,alice@example.com");
            writer.newLine();
            writer.write("Bob,bob@example.com");
            writer.newLine();
        }

        List<Customer> allResults = new ArrayList<>();

        CsvReader.<Customer>builder()
                .source(tempCsvFile)
                .skipLines(2)
                .rowTransformer((headers, rowValues, previousRow) -> Customer.builder()
                        .firstName(rowValues[0])
                        .email(rowValues[1])
                        .build())
                .batchProcessor(allResults::addAll)
                .read();

        assertEquals(2, allResults.size());
        assertEquals("Alice", allResults.get(0).getFirstName());
        assertEquals("alice@example.com", allResults.get(0).getEmail());
    }

    @Test
    public void testReadCsvWithSkipLinesNegativeValue() {
        // Test that negative skipLines throws exception
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            CsvReader.<Customer>builder()
                    .source(tempCsvFile)
                    .skipLines(-1)
                    .rowTransformer((headers, rowValues, previousRow) -> new Customer())
                    .batchProcessor(batch -> {})
                    .read();
        });

        assertEquals("Skip lines must be non-negative", exception.getMessage());
    }

    @Test
    public void testReadCsvWithSkipLinesZero() throws IOException {
        // Test that skipLines(0) works correctly
        createTestCsvFile(List.of("Name", "Email"), List.of("firstName", "email"), ',', 2);

        List<Customer> allResults = new ArrayList<>();

        CsvReader.<Customer>builder()
                .source(tempCsvFile)
                .skipLines(0)
                .rowTransformer((headers, rowValues, previousRow) -> Customer.builder()
                        .firstName(rowValues[0])
                        .email(rowValues[1])
                        .build())
                .batchProcessor(allResults::addAll)
                .read();

        assertEquals(2, allResults.size());
    }

    @Test
    public void testReadCsvWithPreviousRow() throws IOException {
        // Test using previousRow in transformer
        createTestCsvFile(List.of("Name", "Age"), List.of("firstName", "age"), ',', 3);

        List<Customer> allResults = new ArrayList<>();
        AtomicInteger rowsWithPreviousRow = new AtomicInteger(0);

        CsvReader.<Customer>builder()
                .source(tempCsvFile)
                .rowTransformer((headers, rowValues, previousRow) -> {
                    if (previousRow != null) {
                        rowsWithPreviousRow.incrementAndGet();
                    }
                    return Customer.builder()
                            .firstName(rowValues[0])
                            .age(Integer.parseInt(rowValues[1]))
                            .build();
                })
                .batchProcessor(allResults::addAll)
                .read();

        assertEquals(3, allResults.size());
        // First row has no previous, so 2 rows should have previous row
        assertEquals(2, rowsWithPreviousRow.get());
    }

    @Test
    public void testReadCsvWithPreviousRowCumulative() throws IOException {
        // Test accumulating data using previousRow
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempCsvFile))) {
            writer.write("Name,Value");
            writer.newLine();
            writer.write("Alice,10");
            writer.newLine();
            writer.write("Bob,20");
            writer.newLine();
            writer.write("Charlie,30");
            writer.newLine();
        }

        List<Customer> allResults = new ArrayList<>();

        CsvReader.<Customer>builder()
                .source(tempCsvFile)
                .rowTransformer((headers, rowValues, previousRow) -> {
                    int currentValue = Integer.parseInt(rowValues[1]);
                    int previousAge = (previousRow != null && previousRow.getAge() != null) ? previousRow.getAge() : 0;

                    return Customer.builder()
                            .firstName(rowValues[0])
                            .age(previousAge + currentValue) // Cumulative sum
                            .build();
                })
                .batchProcessor(allResults::addAll)
                .read();

        assertEquals(3, allResults.size());
        assertEquals(10, allResults.get(0).getAge()); // 0 + 10
        assertEquals(30, allResults.get(1).getAge()); // 10 + 20
        assertEquals(60, allResults.get(2).getAge()); // 30 + 30
    }

    @Test
    public void testReadCsvFileNotFound() {
        File nonExistentFile = new File("non_existent_file_12345.csv");

        IOException exception = assertThrows(IOException.class, () -> CsvReader.<Customer>builder()
                .source(nonExistentFile)
                .rowTransformer((headers, rowValues, previousRow) -> new Customer())
                .batchProcessor(batch -> {})
                .read());

        assertNotNull(exception);
    }

    @Test
    public void testReadCsvWithCombinedCustomizations() throws IOException {
        // Test multiple customizations together
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempCsvFile))) {
            writer.write("# Comment line to skip");
            writer.newLine();
            writer.write("Name|Email|Age");
            writer.newLine();
            writer.write("Alice|alice@test.com|25");
            writer.newLine();
            writer.write("Bob|bob@test.com|30");
            writer.newLine();
            writer.write("Charlie|charlie@test.com|35");
            writer.newLine();
        }

        List<Customer> allResults = new ArrayList<>();
        AtomicInteger totalProcessed = new AtomicInteger(0);

        CsvReader.<Customer>builder()
                .source(tempCsvFile)
                .delimiter('|')
                .skipLines(1)
                .batchSize(2)
                .rowTransformer((headers, rowValues, previousRow) -> Customer.builder()
                        .firstName(rowValues[0])
                        .email(rowValues[1])
                        .age(Integer.parseInt(rowValues[2]))
                        .build())
                .batchProcessor(new com.javaquery.helper.BatchProcessor<Customer>() {
                    @Override
                    public void onBatch(List<Customer> batch) {
                        allResults.addAll(batch);
                    }

                    @Override
                    public void onComplete(int total, int batches) {
                        totalProcessed.set(total);
                    }
                })
                .read();

        assertEquals(3, allResults.size());
        assertEquals(3, totalProcessed.get());
        assertEquals("Alice", allResults.get(0).getFirstName());
        assertEquals(25, allResults.get(0).getAge());
    }

    @Test
    public void testReadCsvWithAllNullTransforms() throws IOException {
        // Test when all rows return null
        createTestCsvFile(List.of("Name", "Email"), List.of("firstName", "email"), ',', 3);

        List<Customer> allResults = new ArrayList<>();
        AtomicInteger batchCount = new AtomicInteger(0);
        AtomicInteger totalProcessed = new AtomicInteger(0);

        CsvReader.<Customer>builder()
                .source(tempCsvFile)
                .rowTransformer((headers, rowValues, previousRow) -> null) // Always return null
                .batchProcessor(new com.javaquery.helper.BatchProcessor<Customer>() {
                    @Override
                    public void onBatch(List<Customer> batch) {
                        allResults.addAll(batch);
                        batchCount.incrementAndGet();
                    }

                    @Override
                    public void onComplete(int total, int batches) {
                        totalProcessed.set(total);
                    }
                })
                .read();

        // No items should be processed
        assertEquals(0, allResults.size());
        assertEquals(0, batchCount.get());
        assertEquals(0, totalProcessed.get());
    }

    @Test
    public void testReadCsvBuilderChaining() throws IOException {
        // Test that builder pattern returns correct instance for chaining
        createTestCsvFile(List.of("Name", "Email"), List.of("firstName", "email"), ',', 1);

        List<Customer> allResults = new ArrayList<>();

        CsvReader<Customer> reader = CsvReader.<Customer>builder()
                .source(tempCsvFile)
                .delimiter(',')
                .quoteChar('"')
                .escapeChar('\\')
                .skipLines(0)
                .batchSize(10)
                .rowTransformer((headers, rowValues, previousRow) -> Customer.builder()
                        .firstName(rowValues[0])
                        .email(rowValues[1])
                        .build())
                .batchProcessor(allResults::addAll);

        assertNotNull(reader);
        reader.read();
        assertEquals(1, allResults.size());
    }

    @Test
    public void testReadCsvWithLargeFile() throws IOException {
        // Test with larger dataset to ensure memory efficiency
        List<Customer> fakeData =
                createTestCsvFile(List.of("Name", "Email", "Age"), List.of("firstName", "email", "age"), ',', 10000);

        List<Customer> allResults = new ArrayList<>();
        AtomicInteger batchCount = new AtomicInteger(0);

        CsvReader.<Customer>builder()
                .source(tempCsvFile)
                .batchSize(500)
                .rowTransformer((headers, rowValues, previousRow) -> Customer.builder()
                        .firstName(rowValues[0])
                        .email(rowValues[1])
                        .age(Integer.parseInt(rowValues[2]))
                        .build())
                .batchProcessor(new com.javaquery.helper.BatchProcessor<Customer>() {
                    @Override
                    public void onBatch(List<Customer> batch) {
                        allResults.addAll(batch);
                        batchCount.incrementAndGet();
                    }
                })
                .read();

        assertEquals(10000, allResults.size());
        assertEquals(20, batchCount.get()); // 10000 / 500 = 20 batches
    }

    @Test
    public void testReadCsvWithBatchSize1() throws IOException {
        // Test with batch size of 1 (extreme case)
        createTestCsvFile(List.of("Name", "Email"), List.of("firstName", "email"), ',', 3);

        List<Customer> allResults = new ArrayList<>();
        AtomicInteger batchCount = new AtomicInteger(0);

        CsvReader.<Customer>builder()
                .source(tempCsvFile)
                .batchSize(1)
                .rowTransformer((headers, rowValues, previousRow) -> Customer.builder()
                        .firstName(rowValues[0])
                        .email(rowValues[1])
                        .build())
                .batchProcessor(batch -> {
                    allResults.addAll(batch);
                    batchCount.incrementAndGet();
                })
                .read();

        assertEquals(3, allResults.size());
        assertEquals(3, batchCount.get()); // Each item in its own batch
    }

    @Test
    public void testReadCsvWithEmptyRows() throws IOException {
        // Test handling of empty rows in CSV
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempCsvFile))) {
            writer.write("Name,Email");
            writer.newLine();
            writer.write("Alice,alice@test.com");
            writer.newLine();
            writer.write(","); // Empty row
            writer.newLine();
            writer.write("Bob,bob@test.com");
            writer.newLine();
        }

        List<Customer> allResults = new ArrayList<>();

        CsvReader.<Customer>builder()
                .source(tempCsvFile)
                .rowTransformer((headers, rowValues, previousRow) -> {
                    // Skip empty names
                    if (rowValues[0] == null || rowValues[0].trim().isEmpty()) {
                        return null;
                    }
                    return Customer.builder()
                            .firstName(rowValues[0])
                            .email(rowValues[1])
                            .build();
                })
                .batchProcessor(allResults::addAll)
                .read();

        // Should only have 2 valid results
        assertEquals(2, allResults.size());
        assertEquals("Alice", allResults.get(0).getFirstName());
        assertEquals("Bob", allResults.get(1).getFirstName());
    }

    // Helper method to create test CSV files
    private void createTestCsvFile(String... lines) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempCsvFile))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    private List<Customer> createTestCsvFile(List<String> headers, List<String> keys, char delimiter, int rowCount) {
        List<Customer> customers = Customer.fakeData(rowCount, false);
        try {
            CsvWriter.<Customer>builder()
                    .headers(headers)
                    .keys(keys)
                    .data(customers)
                    .toFile(tempCsvFile)
                    .delimiter(delimiter)
                    .write();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return customers;
    }
}
