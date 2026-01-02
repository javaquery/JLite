package com.javaquery.poi.excel.reader;

import static org.junit.jupiter.api.Assertions.*;

import com.javaquery.helper.BatchProcessor;
import com.javaquery.poi.excel.model.Customer;
import com.javaquery.poi.excel.writer.ExcelWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Comprehensive test cases for ExcelReader
 * @author vicky.thakor
 * @since 1.0.0
 */
public class ExcelReaderTest {

    private static final List<String> DEFAULT_HEADERS = List.of("First Name", "Last Name", "Email");
    private static final List<String> DEFAULT_KEYS = List.of("firstName", "lastName", "email");
    private File tempExcelFile;

    @BeforeEach
    public void setup() throws IOException {
        tempExcelFile = File.createTempFile("test_excel", ".xlsx");
    }

    @AfterEach
    public void cleanup() throws IOException {
        if (tempExcelFile != null && tempExcelFile.exists()) {
            Files.deleteIfExists(tempExcelFile.toPath());
        }
    }

    @Test
    public void testReadExcelWithBasicData() throws IOException {
        List<Customer> fakeData = createTestExcelFile(DEFAULT_HEADERS, DEFAULT_KEYS, 3);

        List<Customer> allResults = new ArrayList<>();
        AtomicInteger batchCount = new AtomicInteger(0);

        ExcelReader.<Customer>builder()
                .source(tempExcelFile)
                .rowTransformer((headers, currentRow, previousRow) -> Customer.builder()
                        .firstName(getCellValueAsString(currentRow.getCell(0)))
                        .lastName(getCellValueAsString(currentRow.getCell(1)))
                        .email(getCellValueAsString(currentRow.getCell(2)))
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
    public void testReadExcelWithLargeBatchSize() throws IOException {
        // Manually create Excel file with proper types
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet1");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Name");
            headerRow.createCell(1).setCellValue("Age");

            Row dataRow1 = sheet.createRow(1);
            dataRow1.createCell(0).setCellValue("Alice");
            dataRow1.createCell(1).setCellValue(25);

            Row dataRow2 = sheet.createRow(2);
            dataRow2.createCell(0).setCellValue("Bob");
            dataRow2.createCell(1).setCellValue(30);

            try (FileOutputStream fos = new FileOutputStream(tempExcelFile)) {
                workbook.write(fos);
            }
        }

        List<Customer> allResults = new ArrayList<>();
        AtomicInteger batchCount = new AtomicInteger(0);

        ExcelReader.<Customer>builder()
                .source(tempExcelFile)
                .rowTransformer((headers, currentRow, previousRow) -> Customer.builder()
                        .firstName(getCellValueAsString(currentRow.getCell(0)))
                        .age(getCellValueAsInt(currentRow.getCell(1)))
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
    public void testReadExcelWithOnCompleteCallback() throws IOException {
        createTestExcelFile(List.of("Name", "Email"), List.of("firstName", "email"), 3);

        AtomicInteger totalProcessed = new AtomicInteger(0);
        AtomicInteger totalBatches = new AtomicInteger(0);

        ExcelReader.<Customer>builder()
                .source(tempExcelFile)
                .rowTransformer((headers, currentRow, previousRow) -> Customer.builder()
                        .firstName(getCellValueAsString(currentRow.getCell(0)))
                        .email(getCellValueAsString(currentRow.getCell(1)))
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
    public void testReadExcelWithNullTransform() throws IOException {
        // Test when rowTransformer returns null (skips row)
        createTestExcelFile("Name", "Age", "Alice", "30", "Bob", "invalid", "Charlie", "25");

        List<Customer> allResults = new ArrayList<>();

        ExcelReader.<Customer>builder()
                .source(tempExcelFile)
                .rowTransformer((headers, currentRow, previousRow) -> {
                    Customer customer = Customer.builder()
                            .firstName(getCellValueAsString(currentRow.getCell(0)))
                            .build();
                    try {
                        Integer age = getCellValueAsInt(currentRow.getCell(1));
                        if (age != null) {
                            customer.setAge(age);
                            return customer;
                        }
                        return null;
                    } catch (Exception e) {
                        // Skip invalid rows
                        return null;
                    }
                })
                .batchProcessor(new BatchProcessor<Customer>() {
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
    public void testReadExcelWithEmptyFile() throws IOException {
        // Create empty Excel file with only headers (no data rows)
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet1");
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Name");
            headerRow.createCell(1).setCellValue("Email");

            try (FileOutputStream fos = new FileOutputStream(tempExcelFile)) {
                workbook.write(fos);
            }
        }

        List<Customer> allResults = new ArrayList<>();
        AtomicInteger batchCount = new AtomicInteger(0);

        ExcelReader.<Customer>builder()
                .source(tempExcelFile)
                .rowTransformer((headers, currentRow, previousRow) -> Customer.builder()
                        .firstName(getCellValueAsString(currentRow.getCell(0)))
                        .email(getCellValueAsString(currentRow.getCell(1)))
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
    public void testReadExcelWithExactBatchSize() throws IOException {
        // Test when data size exactly matches batch size
        createTestExcelFile(List.of("Name", "Email"), List.of("firstName", "email"), 3);

        List<Customer> allResults = new ArrayList<>();
        AtomicInteger batchCount = new AtomicInteger(0);

        ExcelReader.<Customer>builder()
                .source(tempExcelFile)
                .rowTransformer((headers, currentRow, previousRow) -> Customer.builder()
                        .firstName(getCellValueAsString(currentRow.getCell(0)))
                        .email(getCellValueAsString(currentRow.getCell(1)))
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
    public void testReadExcelWithSingleRow() throws IOException {
        List<Customer> fakeData = createTestExcelFile(List.of("Name", "Email"), List.of("firstName", "email"), 1);

        List<Customer> allResults = new ArrayList<>();

        ExcelReader.<Customer>builder()
                .source(tempExcelFile)
                .rowTransformer((headers, currentRow, previousRow) -> Customer.builder()
                        .firstName(getCellValueAsString(currentRow.getCell(0)))
                        .email(getCellValueAsString(currentRow.getCell(1)))
                        .build())
                .batchProcessor(allResults::addAll)
                .batchSize(10)
                .read();

        assertEquals(1, allResults.size());
        assertEquals(fakeData.get(0).getFirstName(), allResults.get(0).getFirstName());
    }

    @Test
    public void testReadExcelThrowsExceptionWhenSourceMissing() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ExcelReader.<Customer>builder()
                    .rowTransformer((headers, currentRow, previousRow) -> new Customer())
                    .batchProcessor(batch -> {})
                    .read();
        });

        assertEquals("Source file must be provided", exception.getMessage());
    }

    @Test
    public void testReadExcelThrowsExceptionWhenSourceNotExists() {
        File nonExistentFile = new File("non_existent_file_12345.xlsx");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ExcelReader.<Customer>builder()
                    .source(nonExistentFile)
                    .rowTransformer((headers, currentRow, previousRow) -> new Customer())
                    .batchProcessor(batch -> {})
                    .read();
        });

        assertTrue(exception.getMessage().contains("Source file does not exist"));
    }

    @Test
    public void testReadExcelThrowsExceptionWhenTransformerMissing() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ExcelReader.<Customer>builder()
                    .source(tempExcelFile)
                    .batchProcessor(batch -> {})
                    .read();
        });

        assertEquals("Row transformer must be provided", exception.getMessage());
    }

    @Test
    public void testReadExcelThrowsExceptionWhenBatchProcessorMissing() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ExcelReader.<Customer>builder()
                    .source(tempExcelFile)
                    .rowTransformer((headers, currentRow, previousRow) -> new Customer())
                    .read();
        });

        assertEquals("Batch processor must be provided", exception.getMessage());
    }

    @Test
    public void testReadExcelWithMultipleBatchesVerifyBatchSizes() throws IOException {
        createTestExcelFile(List.of("Name", "Email"), List.of("firstName", "email"), 7);

        List<Integer> batchSizes = new ArrayList<>();
        ExcelReader.<Customer>builder()
                .source(tempExcelFile)
                .rowTransformer((headers, currentRow, previousRow) -> Customer.builder()
                        .firstName(getCellValueAsString(currentRow.getCell(0)))
                        .email(getCellValueAsString(currentRow.getCell(1)))
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
    public void testReadExcelWithHeaderAccess() throws IOException {
        createTestExcelFile(List.of("FirstName", "LastName", "Email"), List.of("firstName", "lastName", "email"), 1);

        List<String> headersCaptured = new ArrayList<>();

        ExcelReader.<Customer>builder()
                .source(tempExcelFile)
                .rowTransformer((headers, currentRow, previousRow) -> {
                    if (headersCaptured.isEmpty()) {
                        // Capture headers on first row
                        for (int i = 0; i < headers.getLastCellNum(); i++) {
                            headersCaptured.add(getCellValueAsString(headers.getCell(i)));
                        }
                    }
                    return Customer.builder()
                            .firstName(getCellValueAsString(currentRow.getCell(0)))
                            .lastName(getCellValueAsString(currentRow.getCell(1)))
                            .email(getCellValueAsString(currentRow.getCell(2)))
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
    public void testReadExcelWithSkipRows() throws IOException {
        // Create Excel with rows to skip at the top
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet1");

            // Add comment rows
            Row comment1 = sheet.createRow(0);
            comment1.createCell(0).setCellValue("# This is a comment line");

            Row comment2 = sheet.createRow(1);
            comment2.createCell(0).setCellValue("# Another comment");

            // Add header row
            Row headerRow = sheet.createRow(2);
            headerRow.createCell(0).setCellValue("Name");
            headerRow.createCell(1).setCellValue("Email");

            // Add data rows
            Row dataRow1 = sheet.createRow(3);
            dataRow1.createCell(0).setCellValue("Alice");
            dataRow1.createCell(1).setCellValue("alice@example.com");

            Row dataRow2 = sheet.createRow(4);
            dataRow2.createCell(0).setCellValue("Bob");
            dataRow2.createCell(1).setCellValue("bob@example.com");

            try (FileOutputStream fos = new FileOutputStream(tempExcelFile)) {
                workbook.write(fos);
            }
        }

        List<Customer> allResults = new ArrayList<>();

        ExcelReader.<Customer>builder()
                .source(tempExcelFile)
                .skipRows(2)
                .rowTransformer((headers, currentRow, previousRow) -> Customer.builder()
                        .firstName(getCellValueAsString(currentRow.getCell(0)))
                        .email(getCellValueAsString(currentRow.getCell(1)))
                        .build())
                .batchProcessor(allResults::addAll)
                .read();

        assertEquals(2, allResults.size());
        assertEquals("Alice", allResults.get(0).getFirstName());
        assertEquals("alice@example.com", allResults.get(0).getEmail());
    }

    @Test
    public void testReadExcelWithSkipRowsNegativeValue() {
        // Test that negative skipRows throws exception
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ExcelReader.<Customer>builder()
                    .source(tempExcelFile)
                    .skipRows(-1)
                    .rowTransformer((headers, currentRow, previousRow) -> new Customer())
                    .batchProcessor(batch -> {})
                    .read();
        });

        assertEquals("Skip rows must be non-negative", exception.getMessage());
    }

    @Test
    public void testReadExcelWithSkipRowsZero() throws IOException {
        // Test that skipRows(0) works correctly
        createTestExcelFile(List.of("Name", "Email"), List.of("firstName", "email"), 2);

        List<Customer> allResults = new ArrayList<>();

        ExcelReader.<Customer>builder()
                .source(tempExcelFile)
                .skipRows(0)
                .rowTransformer((headers, currentRow, previousRow) -> Customer.builder()
                        .firstName(getCellValueAsString(currentRow.getCell(0)))
                        .email(getCellValueAsString(currentRow.getCell(1)))
                        .build())
                .batchProcessor(allResults::addAll)
                .read();

        assertEquals(2, allResults.size());
    }

    @Test
    public void testReadExcelWithPreviousRow() throws IOException {
        // Test using previousRow in transformer
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet1");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Name");
            headerRow.createCell(1).setCellValue("Age");

            Row dataRow1 = sheet.createRow(1);
            dataRow1.createCell(0).setCellValue("Alice");
            dataRow1.createCell(1).setCellValue(25);

            Row dataRow2 = sheet.createRow(2);
            dataRow2.createCell(0).setCellValue("Bob");
            dataRow2.createCell(1).setCellValue(30);

            Row dataRow3 = sheet.createRow(3);
            dataRow3.createCell(0).setCellValue("Charlie");
            dataRow3.createCell(1).setCellValue(35);

            try (FileOutputStream fos = new FileOutputStream(tempExcelFile)) {
                workbook.write(fos);
            }
        }

        List<Customer> allResults = new ArrayList<>();
        AtomicInteger rowsWithPreviousRow = new AtomicInteger(0);

        ExcelReader.<Customer>builder()
                .source(tempExcelFile)
                .rowTransformer((headers, currentRow, previousRow) -> {
                    if (previousRow != null) {
                        rowsWithPreviousRow.incrementAndGet();
                    }
                    return Customer.builder()
                            .firstName(getCellValueAsString(currentRow.getCell(0)))
                            .age(getCellValueAsInt(currentRow.getCell(1)))
                            .build();
                })
                .batchProcessor(allResults::addAll)
                .read();

        assertEquals(3, allResults.size());
        // First row has no previous, so 2 rows should have previous row
        assertEquals(2, rowsWithPreviousRow.get());
    }

    @Test
    public void testReadExcelWithPreviousRowCumulative() throws IOException {
        // Test accumulating data using previousRow
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet1");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Name");
            headerRow.createCell(1).setCellValue("Value");

            Row dataRow1 = sheet.createRow(1);
            dataRow1.createCell(0).setCellValue("Alice");
            dataRow1.createCell(1).setCellValue(10);

            Row dataRow2 = sheet.createRow(2);
            dataRow2.createCell(0).setCellValue("Bob");
            dataRow2.createCell(1).setCellValue(20);

            Row dataRow3 = sheet.createRow(3);
            dataRow3.createCell(0).setCellValue("Charlie");
            dataRow3.createCell(1).setCellValue(30);

            try (FileOutputStream fos = new FileOutputStream(tempExcelFile)) {
                workbook.write(fos);
            }
        }

        List<Customer> allResults = new ArrayList<>();

        ExcelReader.<Customer>builder()
                .source(tempExcelFile)
                .rowTransformer((headers, currentRow, previousRow) -> {
                    int currentValue = getCellValueAsInt(currentRow.getCell(1));
                    int previousAge = (previousRow != null && previousRow.getAge() != null) ? previousRow.getAge() : 0;

                    return Customer.builder()
                            .firstName(getCellValueAsString(currentRow.getCell(0)))
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
    public void testReadExcelWithSheetIndex() throws IOException {
        // Create Excel with multiple sheets
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            // First sheet
            Sheet sheet1 = workbook.createSheet("Sheet1");
            Row header1 = sheet1.createRow(0);
            header1.createCell(0).setCellValue("Name");
            Row data1 = sheet1.createRow(1);
            data1.createCell(0).setCellValue("Sheet1Data");

            // Second sheet
            Sheet sheet2 = workbook.createSheet("Sheet2");
            Row header2 = sheet2.createRow(0);
            header2.createCell(0).setCellValue("Name");
            Row data2 = sheet2.createRow(1);
            data2.createCell(0).setCellValue("Sheet2Data");

            try (FileOutputStream fos = new FileOutputStream(tempExcelFile)) {
                workbook.write(fos);
            }
        }

        List<Customer> allResults = new ArrayList<>();

        ExcelReader.<Customer>builder()
                .source(tempExcelFile)
                .sheetIndex(1) // Read second sheet
                .rowTransformer((headers, currentRow, previousRow) -> Customer.builder()
                        .firstName(getCellValueAsString(currentRow.getCell(0)))
                        .build())
                .batchProcessor(allResults::addAll)
                .read();

        assertEquals(1, allResults.size());
        assertEquals("Sheet2Data", allResults.get(0).getFirstName());
    }

    @Test
    public void testReadExcelWithSheetName() throws IOException {
        // Create Excel with named sheets
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            // First sheet
            Sheet sheet1 = workbook.createSheet("Customers");
            Row header1 = sheet1.createRow(0);
            header1.createCell(0).setCellValue("Name");
            Row data1 = sheet1.createRow(1);
            data1.createCell(0).setCellValue("CustomerData");

            // Second sheet
            Sheet sheet2 = workbook.createSheet("Orders");
            Row header2 = sheet2.createRow(0);
            header2.createCell(0).setCellValue("Name");
            Row data2 = sheet2.createRow(1);
            data2.createCell(0).setCellValue("OrderData");

            try (FileOutputStream fos = new FileOutputStream(tempExcelFile)) {
                workbook.write(fos);
            }
        }

        List<Customer> allResults = new ArrayList<>();

        ExcelReader.<Customer>builder()
                .source(tempExcelFile)
                .sheetName("Orders") // Read Orders sheet
                .rowTransformer((headers, currentRow, previousRow) -> Customer.builder()
                        .firstName(getCellValueAsString(currentRow.getCell(0)))
                        .build())
                .batchProcessor(allResults::addAll)
                .read();

        assertEquals(1, allResults.size());
        assertEquals("OrderData", allResults.get(0).getFirstName());
    }

    @Test
    public void testReadExcelWithInvalidSheetIndex() throws IOException {
        createTestExcelFile(List.of("Name"), List.of("firstName"), 1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ExcelReader.<Customer>builder()
                    .source(tempExcelFile)
                    .sheetIndex(10) // Invalid index
                    .rowTransformer((headers, currentRow, previousRow) -> new Customer())
                    .batchProcessor(batch -> {})
                    .read();
        });

        assertTrue(exception.getMessage().contains("out of bounds"));
    }

    @Test
    public void testReadExcelWithInvalidSheetName() throws IOException {
        createTestExcelFile(List.of("Name"), List.of("firstName"), 1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ExcelReader.<Customer>builder()
                    .source(tempExcelFile)
                    .sheetName("NonExistentSheet")
                    .rowTransformer((headers, currentRow, previousRow) -> new Customer())
                    .batchProcessor(batch -> {})
                    .read();
        });

        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    public void testReadExcelWithSheetIndexNegativeValue() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ExcelReader.<Customer>builder()
                    .source(tempExcelFile)
                    .sheetIndex(-1)
                    .rowTransformer((headers, currentRow, previousRow) -> new Customer())
                    .batchProcessor(batch -> {})
                    .read();
        });

        assertEquals("Sheet index must be non-negative", exception.getMessage());
    }

    @Test
    public void testReadExcelWithAllNullTransforms() throws IOException {
        // Test when all rows return null
        createTestExcelFile(List.of("Name", "Email"), List.of("firstName", "email"), 3);

        List<Customer> allResults = new ArrayList<>();
        AtomicInteger batchCount = new AtomicInteger(0);
        AtomicInteger totalProcessed = new AtomicInteger(0);

        ExcelReader.<Customer>builder()
                .source(tempExcelFile)
                .rowTransformer((headers, currentRow, previousRow) -> null) // Always return null
                .batchProcessor(new BatchProcessor<Customer>() {
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
    public void testReadExcelBuilderChaining() throws IOException {
        // Test that builder pattern returns correct instance for chaining
        createTestExcelFile(List.of("Name", "Email"), List.of("firstName", "email"), 1);

        List<Customer> allResults = new ArrayList<>();

        ExcelReader<Customer> reader = ExcelReader.<Customer>builder()
                .source(tempExcelFile)
                .sheetIndex(0)
                .sheetName("Sheet1")
                .skipRows(0)
                .batchSize(10)
                .rowTransformer((headers, currentRow, previousRow) -> Customer.builder()
                        .firstName(getCellValueAsString(currentRow.getCell(0)))
                        .email(getCellValueAsString(currentRow.getCell(1)))
                        .build())
                .batchProcessor(allResults::addAll);

        assertNotNull(reader);
        reader.read();
        assertEquals(1, allResults.size());
    }

    @Test
    public void testReadExcelWithLargeFile() throws IOException {
        // Test with larger dataset to ensure memory efficiency
        int rowCount = 10000;
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet1");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Name");
            headerRow.createCell(1).setCellValue("Email");
            headerRow.createCell(2).setCellValue("Age");

            for (int i = 0; i < rowCount; i++) {
                Row dataRow = sheet.createRow(i + 1);
                dataRow.createCell(0).setCellValue("Name" + i);
                dataRow.createCell(1).setCellValue("email" + i + "@test.com");
                dataRow.createCell(2).setCellValue(20 + (i % 50));
            }

            try (FileOutputStream fos = new FileOutputStream(tempExcelFile)) {
                workbook.write(fos);
            }
        }

        List<Customer> allResults = new ArrayList<>();
        AtomicInteger batchCount = new AtomicInteger(0);

        ExcelReader.<Customer>builder()
                .source(tempExcelFile)
                .batchSize(500)
                .rowTransformer((headers, currentRow, previousRow) -> Customer.builder()
                        .firstName(getCellValueAsString(currentRow.getCell(0)))
                        .email(getCellValueAsString(currentRow.getCell(1)))
                        .age(getCellValueAsInt(currentRow.getCell(2)))
                        .build())
                .batchProcessor(new BatchProcessor<Customer>() {
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
    public void testReadExcelWithBatchSize1() throws IOException {
        // Test with batch size of 1 (extreme case)
        createTestExcelFile(List.of("Name", "Email"), List.of("firstName", "email"), 3);

        List<Customer> allResults = new ArrayList<>();
        AtomicInteger batchCount = new AtomicInteger(0);

        ExcelReader.<Customer>builder()
                .source(tempExcelFile)
                .batchSize(1)
                .rowTransformer((headers, currentRow, previousRow) -> Customer.builder()
                        .firstName(getCellValueAsString(currentRow.getCell(0)))
                        .email(getCellValueAsString(currentRow.getCell(1)))
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
    public void testReadExcelWithBatchSizeZero() {
        // Test that batch size 0 throws exception
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ExcelReader.<Customer>builder()
                    .source(tempExcelFile)
                    .batchSize(0)
                    .rowTransformer((headers, currentRow, previousRow) -> new Customer())
                    .batchProcessor(batch -> {})
                    .read();
        });

        assertEquals("Batch size must be positive", exception.getMessage());
    }

    @Test
    public void testReadExcelWithBatchSizeNegative() {
        // Test that negative batch size throws exception
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ExcelReader.<Customer>builder()
                    .source(tempExcelFile)
                    .batchSize(-1)
                    .rowTransformer((headers, currentRow, previousRow) -> new Customer())
                    .batchProcessor(batch -> {})
                    .read();
        });

        assertEquals("Batch size must be positive", exception.getMessage());
    }

    @Test
    public void testReadExcelWithEmptyRows() throws IOException {
        // Test handling of empty rows in Excel
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet1");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Name");
            headerRow.createCell(1).setCellValue("Email");

            Row dataRow1 = sheet.createRow(1);
            dataRow1.createCell(0).setCellValue("Alice");
            dataRow1.createCell(1).setCellValue("alice@test.com");

            // Empty row (row 2 exists but has no cells)
            sheet.createRow(2);

            Row dataRow3 = sheet.createRow(3);
            dataRow3.createCell(0).setCellValue("Bob");
            dataRow3.createCell(1).setCellValue("bob@test.com");

            try (FileOutputStream fos = new FileOutputStream(tempExcelFile)) {
                workbook.write(fos);
            }
        }

        List<Customer> allResults = new ArrayList<>();

        ExcelReader.<Customer>builder()
                .source(tempExcelFile)
                .rowTransformer((headers, currentRow, previousRow) -> {
                    Cell nameCell = currentRow.getCell(0);
                    String name = getCellValueAsString(nameCell);
                    // Skip empty names
                    if (name == null || name.trim().isEmpty()) {
                        return null;
                    }
                    return Customer.builder()
                            .firstName(name)
                            .email(getCellValueAsString(currentRow.getCell(1)))
                            .build();
                })
                .batchProcessor(allResults::addAll)
                .read();

        // Should only have 2 valid results
        assertEquals(2, allResults.size());
        assertEquals("Alice", allResults.get(0).getFirstName());
        assertEquals("Bob", allResults.get(1).getFirstName());
    }

    @Test
    public void testReadExcelWithCombinedCustomizations() throws IOException {
        // Test multiple customizations together
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("TestSheet");

            // Comment row to skip
            Row comment = sheet.createRow(0);
            comment.createCell(0).setCellValue("# Comment line to skip");

            // Header row
            Row headerRow = sheet.createRow(1);
            headerRow.createCell(0).setCellValue("Name");
            headerRow.createCell(1).setCellValue("Email");
            headerRow.createCell(2).setCellValue("Age");

            // Data rows
            Row dataRow1 = sheet.createRow(2);
            dataRow1.createCell(0).setCellValue("Alice");
            dataRow1.createCell(1).setCellValue("alice@test.com");
            dataRow1.createCell(2).setCellValue(25);

            Row dataRow2 = sheet.createRow(3);
            dataRow2.createCell(0).setCellValue("Bob");
            dataRow2.createCell(1).setCellValue("bob@test.com");
            dataRow2.createCell(2).setCellValue(30);

            Row dataRow3 = sheet.createRow(4);
            dataRow3.createCell(0).setCellValue("Charlie");
            dataRow3.createCell(1).setCellValue("charlie@test.com");
            dataRow3.createCell(2).setCellValue(35);

            try (FileOutputStream fos = new FileOutputStream(tempExcelFile)) {
                workbook.write(fos);
            }
        }

        List<Customer> allResults = new ArrayList<>();
        AtomicInteger totalProcessed = new AtomicInteger(0);

        ExcelReader.<Customer>builder()
                .source(tempExcelFile)
                .sheetName("TestSheet")
                .skipRows(1)
                .batchSize(2)
                .rowTransformer((headers, currentRow, previousRow) -> Customer.builder()
                        .firstName(getCellValueAsString(currentRow.getCell(0)))
                        .email(getCellValueAsString(currentRow.getCell(1)))
                        .age(getCellValueAsInt(currentRow.getCell(2)))
                        .build())
                .batchProcessor(new BatchProcessor<Customer>() {
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

    // Helper methods
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }

    private Integer getCellValueAsInt(Cell cell) {
        if (cell == null) {
            return null;
        }
        try {
            switch (cell.getCellType()) {
                case NUMERIC:
                    return (int) cell.getNumericCellValue();
                case STRING:
                    String value = cell.getStringCellValue();
                    if (value != null && !value.trim().isEmpty()) {
                        return Integer.parseInt(value);
                    }
                    return null;
                default:
                    return null;
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void createTestExcelFile(String... values) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet1");

            int rowIndex = 0;
            int colIndex = 0;
            Row currentRow = sheet.createRow(rowIndex);

            for (String value : values) {
                currentRow.createCell(colIndex).setCellValue(value);
                colIndex++;
                // Move to next row after every 2 values (assuming 2 columns)
                if (colIndex >= 2) {
                    colIndex = 0;
                    rowIndex++;
                    if (rowIndex < values.length / 2 + 1) {
                        currentRow = sheet.createRow(rowIndex);
                    }
                }
            }

            try (FileOutputStream fos = new FileOutputStream(tempExcelFile)) {
                workbook.write(fos);
            }
        }
    }

    private List<Customer> createTestExcelFile(List<String> headers, List<String> keys, int rowCount) {
        List<Customer> customers = Customer.fakeData(rowCount, false);
        try {
            ExcelWriter.<Customer>builder()
                    .headers(headers)
                    .keys(keys)
                    .data(customers)
                    .toFile(tempExcelFile)
                    .write();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return customers;
    }
}
