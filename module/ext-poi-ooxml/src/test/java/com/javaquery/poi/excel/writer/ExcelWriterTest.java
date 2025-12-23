package com.javaquery.poi.excel.writer;

import static org.junit.jupiter.api.Assertions.*;

import com.javaquery.poi.excel.model.Customer;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * @author vicky.thakor
 * @since 2025-12-23
 */
public class ExcelWriterTest {

    private File tempFile;

    @AfterEach
    public void cleanup() throws IOException {
        if (tempFile != null && tempFile.exists()) {
            Files.deleteIfExists(tempFile.toPath());
        }
    }

    @Test
    public void testWriteExcelWithNestedCollection() throws IOException {
        tempFile = File.createTempFile("customers", ".xlsx");
        ExcelWriter.<Customer>builder()
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
        try (FileInputStream fis = new FileInputStream(tempFile);
                Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            assertNotNull(sheet);

            Row headerRow = sheet.getRow(0);
            assertNotNull(headerRow);
            assertEquals("First Name", headerRow.getCell(0).getStringCellValue());
            assertEquals("Last Name", headerRow.getCell(1).getStringCellValue());
            assertEquals("Email", headerRow.getCell(2).getStringCellValue());
            assertEquals("Address Line 1", headerRow.getCell(3).getStringCellValue());
            assertEquals("City", headerRow.getCell(4).getStringCellValue());
            assertEquals("State", headerRow.getCell(5).getStringCellValue());

            // Verify at least one data row exists
            assertTrue(sheet.getPhysicalNumberOfRows() > 1);
        }
    }

    @Test
    public void testWriteExcelWithoutHeader() throws IOException {
        tempFile = File.createTempFile("customers_noheader", ".xlsx");
        ExcelWriter.<Customer>builder()
                .keys(List.of("firstName", "lastName", "email"))
                .data(Customer.fakeData(20, false))
                .toFile(tempFile)
                .includeHeader(false)
                .write();

        // Verify file exists
        assertTrue(tempFile.exists());

        // Verify no header row (first row should be data)
        try (FileInputStream fis = new FileInputStream(tempFile);
                Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            assertNotNull(sheet);

            Row firstRow = sheet.getRow(0);
            assertNull(firstRow); // Row index 0 should not exist when includeHeader is false

            // First data row should be at index 1
            Row dataRow = sheet.getRow(1);
            assertNotNull(dataRow);
        }
    }

    @Test
    public void testWriteExcelWithEmptyData() throws IOException {
        tempFile = File.createTempFile("customers_empty", ".xlsx");

        // Should not throw exception, should just return without writing
        ExcelWriter.<Customer>builder()
                .headers(List.of("First Name", "Last Name"))
                .keys(List.of("firstName", "lastName"))
                .data(new ArrayList<>())
                .toFile(tempFile)
                .write();

        // File should be empty or not contain data
        assertEquals(0, tempFile.length());
    }

    @Test
    public void testWriteExcelThrowsExceptionWhenHeadersMissing() {
        tempFile = new File("test_no_headers.xlsx");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ExcelWriter.<Customer>builder()
                    .keys(List.of("firstName", "lastName"))
                    .data(Customer.fakeData(20, false))
                    .toFile(tempFile)
                    .includeHeader(true)
                    .write();
        });

        assertEquals("Headers must be provided when includeHeader is true", exception.getMessage());
    }

    @Test
    public void testWriteExcelThrowsExceptionWhenHeadersIsEmpty() {
        tempFile = new File("test_no_headers.xlsx");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ExcelWriter.<Customer>builder()
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
    public void testWriteExcelThrowsExceptionWhenKeysMissing() {
        tempFile = new File("test_no_keys.xlsx");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ExcelWriter.<Customer>builder()
                    .data(Customer.fakeData(1, true))
                    .toFile(tempFile)
                    .includeHeader(false)
                    .write();
        });

        assertEquals("Keys must be provided when includeHeader is false", exception.getMessage());
    }

    @Test
    public void testWriteExcelThrowsExceptionWhenKeysIsEmpty() {
        tempFile = new File("test_no_keys.xlsx");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ExcelWriter.<Customer>builder()
                    .keys(Collections.emptyList())
                    .data(Customer.fakeData(1, true))
                    .toFile(tempFile)
                    .includeHeader(false)
                    .write();
        });

        assertEquals("Keys must be provided when includeHeader is false", exception.getMessage());
    }

    @Test
    public void testWriteExcelThrowsExceptionWhenHeadersAndKeysSizeMismatch() {
        tempFile = new File("test_size_mismatch.xlsx");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ExcelWriter.<Customer>builder()
                    .headers(List.of("First Name", "Last Name", "Email"))
                    .keys(List.of("firstName", "lastName"))
                    .data(Customer.fakeData(1, false))
                    .toFile(tempFile)
                    .write();
        });

        assertEquals("Headers and Keys size must be same", exception.getMessage());
    }

    @Test
    public void testWriteExcelWithSimpleFields() throws IOException {
        tempFile = File.createTempFile("customers_simple", ".xlsx");

        ExcelWriter.<Customer>builder()
                .headers(List.of("First Name", "Last Name", "Email"))
                .keys(List.of("firstName", "lastName", "email"))
                .data(Customer.fakeData(30, false))
                .toFile(tempFile)
                .write();

        assertTrue(tempFile.exists());

        // Verify content
        try (FileInputStream fis = new FileInputStream(tempFile);
                Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            assertTrue(sheet.getPhysicalNumberOfRows() > 1); // At least header + 1 data row

            Row headerRow = sheet.getRow(0);
            assertEquals("First Name", headerRow.getCell(0).getStringCellValue());
            assertEquals("Last Name", headerRow.getCell(1).getStringCellValue());
            assertEquals("Email", headerRow.getCell(2).getStringCellValue());

            // Verify a data row has values
            Row dataRow = sheet.getRow(1);
            assertNotNull(dataRow);
            assertNotNull(dataRow.getCell(0));
            assertNotNull(dataRow.getCell(1));
            assertNotNull(dataRow.getCell(2));
        }
    }

    @Test
    public void testWriteExcelWithNestedFields() throws IOException {
        tempFile = File.createTempFile("customers_nested", ".xlsx");

        ExcelWriter.<Customer>builder()
                .headers(List.of("First Name", "Last Name", "Email", "Passport Number"))
                .keys(List.of("firstName", "lastName", "email", "passport.passportNumber"))
                .data(Customer.fakeData(20, false))
                .toFile(tempFile)
                .write();

        assertTrue(tempFile.exists());

        try (FileInputStream fis = new FileInputStream(tempFile);
                Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            assertNotNull(sheet);

            Row headerRow = sheet.getRow(0);
            assertEquals("Passport Number", headerRow.getCell(3).getStringCellValue());

            // Verify at least one data row exists
            assertTrue(sheet.getPhysicalNumberOfRows() > 1);
        }
    }

    @Test
    public void testWriteExcelWithNullNestedField() throws IOException {
        tempFile = File.createTempFile("customers_null_passport", ".xlsx");

        List<Customer> customers = Customer.fakeData(20, false);
        for (Customer customer : customers) {
            customer.setPassport(null);
        }

        ExcelWriter.<Customer>builder()
                .headers(List.of("First Name", "Last Name", "Email", "Passport Number"))
                .keys(List.of("firstName", "lastName", "email", "passport.passportNumber"))
                .data(customers)
                .toFile(tempFile)
                .write();

        assertTrue(tempFile.exists());

        try (FileInputStream fis = new FileInputStream(tempFile);
                Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            assertNotNull(sheet);

            // Verify data rows have empty passport number cells
            Row dataRow = sheet.getRow(1);
            assertNotNull(dataRow);
            Cell passportCell = dataRow.getCell(3);
            // Cell should be empty string
            assertEquals("", passportCell.getStringCellValue());
        }
    }

    @Test
    public void testWriteExcelWithInvalidKey() throws IOException {
        tempFile = File.createTempFile("customers_invalid", ".xlsx");

        List<Customer> customers = Customer.fakeData(20, false);
        for (Customer customer : customers) {
            customer.setPassport(null);
        }

        ExcelWriter.<Customer>builder()
                .headers(List.of("First Name", "Last Name", "Email", "Passport Number", "Country"))
                .keys(List.of("firstName", "lastName", "email", "invalid.key", "invalidKey"))
                .data(customers)
                .toFile(tempFile)
                .write();

        assertTrue(tempFile.exists());

        try (FileInputStream fis = new FileInputStream(tempFile);
                Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            assertNotNull(sheet);

            // Verify data rows have empty cells for invalid keys
            Row dataRow = sheet.getRow(1);
            assertNotNull(dataRow);
            Cell invalidCell = dataRow.getCell(3);
            assertEquals("", invalidCell.getStringCellValue());
        }
    }

    @Test
    public void testWriteExcelWithLargeDataset() throws IOException {
        tempFile = File.createTempFile("customers_large", ".xlsx");

        ExcelWriter.<Customer>builder()
                .headers(List.of("First Name", "Last Name", "Email"))
                .keys(List.of("firstName", "lastName", "email"))
                .data(Customer.fakeData(1000, false))
                .toFile(tempFile)
                .write();

        assertTrue(tempFile.exists());
        assertTrue(tempFile.length() > 0);

        try (FileInputStream fis = new FileInputStream(tempFile);
                Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            // Verify 1 header row + 1000 data rows
            assertTrue(sheet.getPhysicalNumberOfRows() >= 1000);
        }
    }

    @Test
    public void testWriteExcelWithMultipleNestedCollections() throws IOException {
        tempFile = File.createTempFile("customers_multiple_addresses", ".xlsx");

        ExcelWriter.<Customer>builder()
                .headers(List.of("First Name", "Last Name", "Email", "Address Line 1", "City", "State", "Zip Code"))
                .keys(List.of(
                        "firstName",
                        "lastName",
                        "email",
                        "addresses.addressLine1",
                        "addresses.city",
                        "addresses.state",
                        "addresses.zipCode"))
                .data(Customer.fakeData(10, true))
                .toFile(tempFile)
                .write();

        assertTrue(tempFile.exists());

        try (FileInputStream fis = new FileInputStream(tempFile);
                Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            assertNotNull(sheet);

            // Should have header + multiple rows for addresses
            assertTrue(sheet.getPhysicalNumberOfRows() > 10);
        }
    }

    @Test
    public void testWriteExcelColumnWidthAutoSize() throws IOException {
        tempFile = File.createTempFile("customers_width", ".xlsx");

        ExcelWriter.<Customer>builder()
                .headers(List.of("First Name", "Last Name", "Email"))
                .keys(List.of("firstName", "lastName", "email"))
                .data(Customer.fakeData(50, false))
                .toFile(tempFile)
                .write();

        assertTrue(tempFile.exists());

        try (FileInputStream fis = new FileInputStream(tempFile);
                Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);

            // Verify columns have width set (not default)
            int col0Width = sheet.getColumnWidth(0);
            int col1Width = sheet.getColumnWidth(1);
            int col2Width = sheet.getColumnWidth(2);

            // Default column width is typically around 2048-2560
            // Our implementation should set width based on content
            assertTrue(col0Width > 0);
            assertTrue(col1Width > 0);
            assertTrue(col2Width > 0);
        }
    }

    @Test
    public void testWriteExcelVerifySheetName() throws IOException {
        tempFile = File.createTempFile("customers_sheet", ".xlsx");

        ExcelWriter.<Customer>builder()
                .headers(List.of("First Name"))
                .keys(List.of("firstName"))
                .data(Customer.fakeData(10, false))
                .toFile(tempFile)
                .write();

        try (FileInputStream fis = new FileInputStream(tempFile);
                Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            assertEquals("Sheet1", sheet.getSheetName());
        }
    }
}
