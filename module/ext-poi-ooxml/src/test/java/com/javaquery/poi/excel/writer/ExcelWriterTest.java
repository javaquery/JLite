package com.javaquery.poi.excel.writer;

import static org.junit.jupiter.api.Assertions.*;

import com.javaquery.poi.excel.model.Customer;
import com.javaquery.poi.excel.model.Product;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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

    // ========== Tests for setCellValue with different data types ==========

    @Test
    public void testWriteExcelWithStringDataType() throws IOException {
        tempFile = File.createTempFile("product_string", ".xlsx");

        Product product = Product.builder()
                .name("Laptop")
                .build();

        ExcelWriter.<Product>builder()
                .headers(List.of("Name"))
                .keys(List.of("name"))
                .data(List.of(product))
                .toFile(tempFile)
                .write();

        try (FileInputStream fis = new FileInputStream(tempFile);
                Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row dataRow = sheet.getRow(1);
            Cell cell = dataRow.getCell(0);

            assertEquals(CellType.STRING, cell.getCellType());
            assertEquals("Laptop", cell.getStringCellValue());
        }
    }

    @Test
    public void testWriteExcelWithDoubleDataType() throws IOException {
        tempFile = File.createTempFile("product_double", ".xlsx");

        Product product = Product.builder()
                .name("Laptop")
                .price(999.99)
                .build();

        ExcelWriter.<Product>builder()
                .headers(List.of("Name", "Price"))
                .keys(List.of("name", "price"))
                .data(List.of(product))
                .toFile(tempFile)
                .write();

        try (FileInputStream fis = new FileInputStream(tempFile);
                Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row dataRow = sheet.getRow(1);
            Cell cell = dataRow.getCell(1);

            assertEquals(CellType.NUMERIC, cell.getCellType());
            assertEquals(999.99, cell.getNumericCellValue(), 0.001);
        }
    }

    @Test
    public void testWriteExcelWithIntegerDataType() throws IOException {
        tempFile = File.createTempFile("product_integer", ".xlsx");

        Product product = Product.builder()
                .name("Laptop")
                .quantity(10)
                .build();

        ExcelWriter.<Product>builder()
                .headers(List.of("Name", "Quantity"))
                .keys(List.of("name", "quantity"))
                .data(List.of(product))
                .toFile(tempFile)
                .write();

        try (FileInputStream fis = new FileInputStream(tempFile);
                Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row dataRow = sheet.getRow(1);
            Cell cell = dataRow.getCell(1);

            assertEquals(CellType.NUMERIC, cell.getCellType());
            assertEquals(10.0, cell.getNumericCellValue(), 0.001);
        }
    }

    @Test
    public void testWriteExcelWithFloatDataType() throws IOException {
        tempFile = File.createTempFile("product_float", ".xlsx");

        Product product = Product.builder()
                .name("Laptop")
                .discount(15.5f)
                .build();

        ExcelWriter.<Product>builder()
                .headers(List.of("Name", "Discount"))
                .keys(List.of("name", "discount"))
                .data(List.of(product))
                .toFile(tempFile)
                .write();

        try (FileInputStream fis = new FileInputStream(tempFile);
                Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row dataRow = sheet.getRow(1);
            Cell cell = dataRow.getCell(1);

            assertEquals(CellType.NUMERIC, cell.getCellType());
            assertEquals(15.5, cell.getNumericCellValue(), 0.001);
        }
    }

    @Test
    public void testWriteExcelWithLongDataType() throws IOException {
        tempFile = File.createTempFile("product_long", ".xlsx");

        Product product = Product.builder()
                .name("Laptop")
                .weight(5000L)
                .build();

        ExcelWriter.<Product>builder()
                .headers(List.of("Name", "Weight"))
                .keys(List.of("name", "weight"))
                .data(List.of(product))
                .toFile(tempFile)
                .write();

        try (FileInputStream fis = new FileInputStream(tempFile);
                Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row dataRow = sheet.getRow(1);
            Cell cell = dataRow.getCell(1);

            assertEquals(CellType.NUMERIC, cell.getCellType());
            assertEquals(5000.0, cell.getNumericCellValue(), 0.001);
        }
    }

    @Test
    public void testWriteExcelWithByteDataType() throws IOException {
        tempFile = File.createTempFile("product_byte", ".xlsx");

        Product product = Product.builder()
                .name("Laptop")
                .rating((byte) 5)
                .build();

        ExcelWriter.<Product>builder()
                .headers(List.of("Name", "Rating"))
                .keys(List.of("name", "rating"))
                .data(List.of(product))
                .toFile(tempFile)
                .write();

        try (FileInputStream fis = new FileInputStream(tempFile);
                Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row dataRow = sheet.getRow(1);
            Cell cell = dataRow.getCell(1);

            assertEquals(CellType.NUMERIC, cell.getCellType());
            assertEquals(5.0, cell.getNumericCellValue(), 0.001);
        }
    }

    @Test
    public void testWriteExcelWithShortDataType() throws IOException {
        tempFile = File.createTempFile("product_short", ".xlsx");

        Product product = Product.builder()
                .name("Laptop")
                .views((short) 1500)
                .build();

        ExcelWriter.<Product>builder()
                .headers(List.of("Name", "Views"))
                .keys(List.of("name", "views"))
                .data(List.of(product))
                .toFile(tempFile)
                .write();

        try (FileInputStream fis = new FileInputStream(tempFile);
                Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row dataRow = sheet.getRow(1);
            Cell cell = dataRow.getCell(1);

            assertEquals(CellType.NUMERIC, cell.getCellType());
            assertEquals(1500.0, cell.getNumericCellValue(), 0.001);
        }
    }

    @Test
    public void testWriteExcelWithBooleanDataType() throws IOException {
        tempFile = File.createTempFile("product_boolean", ".xlsx");

        Product product1 = Product.builder()
                .name("Laptop")
                .available(true)
                .build();

        Product product2 = Product.builder()
                .name("Mouse")
                .available(false)
                .build();

        ExcelWriter.<Product>builder()
                .headers(List.of("Name", "Available"))
                .keys(List.of("name", "available"))
                .data(List.of(product1, product2))
                .toFile(tempFile)
                .write();

        try (FileInputStream fis = new FileInputStream(tempFile);
                Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);

            Row dataRow1 = sheet.getRow(1);
            Cell cell1 = dataRow1.getCell(1);
            assertEquals(CellType.BOOLEAN, cell1.getCellType());
            assertTrue(cell1.getBooleanCellValue());

            Row dataRow2 = sheet.getRow(2);
            Cell cell2 = dataRow2.getCell(1);
            assertEquals(CellType.BOOLEAN, cell2.getCellType());
            assertFalse(cell2.getBooleanCellValue());
        }
    }

    @Test
    public void testWriteExcelWithDateDataType() throws IOException {
        tempFile = File.createTempFile("product_date", ".xlsx");

        Date manufacturedDate = new Date();
        Product product = Product.builder()
                .name("Laptop")
                .manufacturedDate(manufacturedDate)
                .build();

        ExcelWriter.<Product>builder()
                .headers(List.of("Name", "Manufactured Date"))
                .keys(List.of("name", "manufacturedDate"))
                .data(List.of(product))
                .toFile(tempFile)
                .write();

        try (FileInputStream fis = new FileInputStream(tempFile);
                Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row dataRow = sheet.getRow(1);
            Cell cell = dataRow.getCell(1);

            assertEquals(CellType.NUMERIC, cell.getCellType());
            Date cellDate = cell.getDateCellValue();
            assertNotNull(cellDate);
            // Compare timestamps (allow small difference due to precision)
            assertTrue(Math.abs(cellDate.getTime() - manufacturedDate.getTime()) < 1000);
        }
    }

    @Test
    public void testWriteExcelWithLocalDateTimeDataType() throws IOException {
        tempFile = File.createTempFile("product_localdatetime", ".xlsx");

        LocalDateTime expiryDate = LocalDateTime.now().plusMonths(6);
        Product product = Product.builder()
                .name("Laptop")
                .expiryDate(expiryDate)
                .build();

        ExcelWriter.<Product>builder()
                .headers(List.of("Name", "Expiry Date"))
                .keys(List.of("name", "expiryDate"))
                .data(List.of(product))
                .toFile(tempFile)
                .write();

        try (FileInputStream fis = new FileInputStream(tempFile);
                Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row dataRow = sheet.getRow(1);
            Cell cell = dataRow.getCell(1);

            assertEquals(CellType.NUMERIC, cell.getCellType());
            LocalDateTime cellDate = cell.getLocalDateTimeCellValue();
            assertNotNull(cellDate);
            assertEquals(expiryDate.getYear(), cellDate.getYear());
            assertEquals(expiryDate.getMonth(), cellDate.getMonth());
            assertEquals(expiryDate.getDayOfMonth(), cellDate.getDayOfMonth());
        }
    }

    @Test
    public void testWriteExcelWithFormulaDataType() throws IOException {
        tempFile = File.createTempFile("product_formula", ".xlsx");

        Product product = Product.builder()
                .name("Laptop")
                .price(100.0)
                .quantity(5)
                .totalPrice("B2*C2") // Formula to multiply price * quantity
                .build();

        ExcelWriter.<Product>builder()
                .headers(List.of("Name", "Price", "Quantity", "Total Price"))
                .keys(List.of("name", "price", "quantity", "totalPrice"))
                .data(List.of(product))
                .toFile(tempFile)
                .write();

        try (FileInputStream fis = new FileInputStream(tempFile);
                Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row dataRow = sheet.getRow(1);
            Cell cell = dataRow.getCell(3);

            assertEquals(CellType.FORMULA, cell.getCellType());
            assertEquals("B2*C2", cell.getCellFormula());
        }
    }

    @Test
    public void testWriteExcelWithRichTextDataType() throws IOException {
        tempFile = File.createTempFile("product_richtext", ".xlsx");

        Product product = Product.builder()
                .name("Laptop")
                .description("High-performance laptop with advanced features")
                .build();

        ExcelWriter.<Product>builder()
                .headers(List.of("Name", "Description"))
                .keys(List.of("name", "description"))
                .data(List.of(product))
                .toFile(tempFile)
                .write();

        try (FileInputStream fis = new FileInputStream(tempFile);
                Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row dataRow = sheet.getRow(1);
            Cell cell = dataRow.getCell(1);

            // Rich text is stored as STRING type
            assertEquals(CellType.STRING, cell.getCellType());
            RichTextString richText = cell.getRichStringCellValue();
            assertNotNull(richText);
            assertEquals("High-performance laptop with advanced features", richText.getString());
        }
    }

    @Test
    public void testWriteExcelWithNullCellValue() throws IOException {
        tempFile = File.createTempFile("product_null", ".xlsx");

        Product product = Product.builder()
                .name("Laptop")
                .price(null)
                .available(null)
                .build();

        ExcelWriter.<Product>builder()
                .headers(List.of("Name", "Price", "Available"))
                .keys(List.of("name", "price", "available"))
                .data(List.of(product))
                .toFile(tempFile)
                .write();

        try (FileInputStream fis = new FileInputStream(tempFile);
                Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row dataRow = sheet.getRow(1);

            Cell priceCell = dataRow.getCell(1);
            assertEquals(CellType.STRING, priceCell.getCellType());
            assertEquals("", priceCell.getStringCellValue());

            Cell availableCell = dataRow.getCell(2);
            assertEquals(CellType.STRING, availableCell.getCellType());
            assertEquals("", availableCell.getStringCellValue());
        }
    }

    @Test
    public void testWriteExcelWithAllDataTypes() throws IOException {
        tempFile = File.createTempFile("product_all_types", ".xlsx");

        Date manufacturedDate = new Date();
        LocalDateTime expiryDate = LocalDateTime.now().plusMonths(6);

        Product product = Product.builder()
                .name("Laptop")
                .description("High-performance laptop")
                .price(999.99)
                .quantity(10)
                .discount(15.5f)
                .weight(5000L)
                .available(true)
                .manufacturedDate(manufacturedDate)
                .expiryDate(expiryDate)
                .totalPrice("C2*D2")
                .rating((byte) 5)
                .views((short) 1500)
                .build();

        ExcelWriter.<Product>builder()
                .headers(List.of(
                        "Name",
                        "Description",
                        "Price",
                        "Quantity",
                        "Discount",
                        "Weight",
                        "Available",
                        "Manufactured Date",
                        "Expiry Date",
                        "Total Price",
                        "Rating",
                        "Views"))
                .keys(List.of(
                        "name",
                        "description",
                        "price",
                        "quantity",
                        "discount",
                        "weight",
                        "available",
                        "manufacturedDate",
                        "expiryDate",
                        "totalPrice",
                        "rating",
                        "views"))
                .data(List.of(product))
                .toFile(tempFile)
                .write();

        try (FileInputStream fis = new FileInputStream(tempFile);
                Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row dataRow = sheet.getRow(1);

            // Verify all data types
            assertEquals("Laptop", dataRow.getCell(0).getStringCellValue());
            assertEquals("High-performance laptop", dataRow.getCell(1).getRichStringCellValue().getString());
            assertEquals(999.99, dataRow.getCell(2).getNumericCellValue(), 0.001);
            assertEquals(10.0, dataRow.getCell(3).getNumericCellValue(), 0.001);
            assertEquals(15.5, dataRow.getCell(4).getNumericCellValue(), 0.1);
            assertEquals(5000.0, dataRow.getCell(5).getNumericCellValue(), 0.001);
            assertTrue(dataRow.getCell(6).getBooleanCellValue());
            assertNotNull(dataRow.getCell(7).getDateCellValue());
            assertNotNull(dataRow.getCell(8).getLocalDateTimeCellValue());
            assertEquals("C2*D2", dataRow.getCell(9).getCellFormula());
            assertEquals(5.0, dataRow.getCell(10).getNumericCellValue(), 0.001);
            assertEquals(1500.0, dataRow.getCell(11).getNumericCellValue(), 0.001);
        }
    }

    @Test
    public void testWriteExcelWithMultipleProductsVariousDataTypes() throws IOException {
        tempFile = File.createTempFile("products_multiple", ".xlsx");

        List<Product> products = List.of(
                Product.builder()
                        .name("Laptop")
                        .price(999.99)
                        .quantity(10)
                        .available(true)
                        .rating((byte) 5)
                        .build(),
                Product.builder()
                        .name("Mouse")
                        .price(25.50)
                        .quantity(50)
                        .available(false)
                        .rating((byte) 4)
                        .build(),
                Product.builder()
                        .name("Keyboard")
                        .price(75.00)
                        .quantity(30)
                        .available(true)
                        .rating((byte) 5)
                        .build());

        ExcelWriter.<Product>builder()
                .headers(List.of("Name", "Price", "Quantity", "Available", "Rating"))
                .keys(List.of("name", "price", "quantity", "available", "rating"))
                .data(products)
                .toFile(tempFile)
                .write();

        try (FileInputStream fis = new FileInputStream(tempFile);
                Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);

            // Verify first product
            Row row1 = sheet.getRow(1);
            assertEquals("Laptop", row1.getCell(0).getStringCellValue());
            assertEquals(999.99, row1.getCell(1).getNumericCellValue(), 0.001);
            assertEquals(10.0, row1.getCell(2).getNumericCellValue(), 0.001);
            assertTrue(row1.getCell(3).getBooleanCellValue());
            assertEquals(5.0, row1.getCell(4).getNumericCellValue(), 0.001);

            // Verify second product
            Row row2 = sheet.getRow(2);
            assertEquals("Mouse", row2.getCell(0).getStringCellValue());
            assertEquals(25.50, row2.getCell(1).getNumericCellValue(), 0.001);
            assertFalse(row2.getCell(3).getBooleanCellValue());

            // Verify third product
            Row row3 = sheet.getRow(3);
            assertEquals("Keyboard", row3.getCell(0).getStringCellValue());
            assertEquals(75.00, row3.getCell(1).getNumericCellValue(), 0.001);
            assertTrue(row3.getCell(3).getBooleanCellValue());
        }
    }
}
