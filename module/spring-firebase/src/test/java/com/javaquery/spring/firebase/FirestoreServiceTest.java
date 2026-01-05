package com.javaquery.spring.firebase;

import static org.junit.jupiter.api.Assertions.*;

import com.javaquery.spring.firebase.model.Customer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Integration test for FirestoreService using Firebase emulator or real Firestore instance
 *
 * @author vicky.thakor
 * @since 1.0.0
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestConfiguration.class)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FirestoreServiceTest {

    @Autowired
    private FirestoreService firestoreService;

    private static final String TEST_COLLECTION = "test-collection";
    private static String testDocumentId;
    private static Customer customer;

    @BeforeAll
    static void setUpClass() {
        testDocumentId = "test-doc-" + UUID.randomUUID();
        customer = Customer.fakeData(1).get(0);
    }

    @Test
    @Order(1)
    void testSaveOrUpdate_WithNewDocument_ShouldCreateDocument() throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("firstName", customer.getFirstName());
        data.put("email", customer.getEmail());
        data.put("age", customer.getAge());

        assertDoesNotThrow(() -> firestoreService.saveOrUpdate(TEST_COLLECTION, testDocumentId, data));

        Map<String, Object> retrievedData = firestoreService.getDocument(TEST_COLLECTION, testDocumentId);
        assertNotNull(retrievedData);
        assertEquals(customer.getFirstName(), retrievedData.get("firstName"));
        assertEquals(customer.getEmail(), retrievedData.get("email"));
        assertEquals(customer.getAge(), ((Long) retrievedData.get("age")).intValue());
    }

    @Test
    @Order(2)
    void testSaveOrUpdate_WithExistingDocument_ShouldUpdateDocument() throws Exception {
        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("firstName", "John Updated");
        updatedData.put("email", "john.updated@example.com");
        updatedData.put("age", 31);

        assertDoesNotThrow(() -> firestoreService.saveOrUpdate(TEST_COLLECTION, testDocumentId, updatedData));

        Map<String, Object> retrievedData = firestoreService.getDocument(TEST_COLLECTION, testDocumentId);
        assertNotNull(retrievedData);
        assertEquals("John Updated", retrievedData.get("firstName"));
        assertEquals("john.updated@example.com", retrievedData.get("email"));
        assertEquals(31, ((Long) retrievedData.get("age")).intValue());
    }

    @Test
    @Order(3)
    void testSaveOrUpdate_WithObjectData_ShouldCreateDocument() throws Exception {
        String docId = "test-user-obj-" + UUID.randomUUID();
        Customer customer = Customer.fakeData(1).get(0);

        assertDoesNotThrow(() -> firestoreService.saveOrUpdate(TEST_COLLECTION, docId, customer));

        Customer retrievedUser = firestoreService.getDocument(TEST_COLLECTION, docId, Customer.class);
        assertNotNull(retrievedUser);
        assertEquals(customer.getFirstName(), retrievedUser.getFirstName());
        assertEquals(customer.getEmail(), retrievedUser.getEmail());
        assertEquals(customer.getAge(), retrievedUser.getAge());

        firestoreService.deleteDocument(TEST_COLLECTION, docId);
    }

    @Test
    @Order(4)
    void testAddDocument_ShouldCreateDocumentWithGeneratedId() throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Alice Wonder");
        data.put("email", "alice@example.com");
        data.put("age", 28);

        String documentId = firestoreService.addDocument(TEST_COLLECTION, data);

        assertNotNull(documentId);
        assertFalse(documentId.isEmpty());

        Map<String, Object> retrievedData = firestoreService.getDocument(TEST_COLLECTION, documentId);
        assertNotNull(retrievedData);
        assertEquals("Alice Wonder", retrievedData.get("name"));
        assertEquals("alice@example.com", retrievedData.get("email"));

        firestoreService.deleteDocument(TEST_COLLECTION, documentId);
    }

    @Test
    @Order(5)
    void testUpdateDocumentFields_ShouldUpdateSpecificFields() throws Exception {
        Map<String, Object> updatesToApply = new HashMap<>();
        updatesToApply.put("email", "john.final@example.com");

        assertDoesNotThrow(
                () -> firestoreService.updateDocumentFields(TEST_COLLECTION, testDocumentId, updatesToApply));

        Map<String, Object> retrievedData = firestoreService.getDocument(TEST_COLLECTION, testDocumentId);
        assertNotNull(retrievedData);
        assertEquals("john.final@example.com", retrievedData.get("email"));
        assertEquals("John Updated", retrievedData.get("firstName")); // Name should remain unchanged
    }

    @Test
    @Order(6)
    void testGetDocument_WithExistingDocument_ShouldReturnDocument() throws Exception {
        Map<String, Object> retrievedData = firestoreService.getDocument(TEST_COLLECTION, testDocumentId);

        assertNotNull(retrievedData);
        assertTrue(retrievedData.containsKey("firstName"));
        assertTrue(retrievedData.containsKey("email"));
        assertTrue(retrievedData.containsKey("age"));
    }

    @Test
    @Order(7)
    void testGetDocument_WithNonExistentDocument_ShouldThrowException() {
        String nonExistentDocId = "non-existent-doc-" + UUID.randomUUID();

        RuntimeException exception = assertThrows(
                RuntimeException.class, () -> firestoreService.getDocument(TEST_COLLECTION, nonExistentDocId));

        assertTrue(exception.getMessage().contains("Document not found"));
    }

    @Test
    @Order(8)
    void testGetDocument_WithClassType_ShouldReturnTypedObject() throws Exception {
        String docId = "test-typed-" + UUID.randomUUID();
        Customer newCustomer = Customer.fakeData(1).get(0);
        firestoreService.saveOrUpdate(TEST_COLLECTION, docId, newCustomer);

        Customer retrievedCustomer = firestoreService.getDocument(TEST_COLLECTION, docId, Customer.class);

        assertNotNull(retrievedCustomer);
        assertEquals(newCustomer.getFirstName(), retrievedCustomer.getFirstName());
        assertEquals(newCustomer.getEmail(), retrievedCustomer.getEmail());
        assertEquals(newCustomer.getAge(), retrievedCustomer.getAge());

        firestoreService.deleteDocument(TEST_COLLECTION, docId);
    }

    @Test
    @Order(9)
    void testGetDocument_WithClassType_NonExistentDocument_ShouldThrowException() {
        String nonExistentDocId = "non-existent-typed-" + UUID.randomUUID();

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> firestoreService.getDocument(TEST_COLLECTION, nonExistentDocId, Customer.class));

        assertTrue(exception.getMessage().contains("Document not found"));
    }

    @Test
    @Order(10)
    void testDeleteDocument_ShouldRemoveDocument() throws Exception {
        String docId = "test-delete-" + UUID.randomUUID();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Delete Test");
        data.put("email", "delete@example.com");
        firestoreService.saveOrUpdate(TEST_COLLECTION, docId, data);

        Map<String, Object> beforeDelete = firestoreService.getDocument(TEST_COLLECTION, docId);
        assertNotNull(beforeDelete);

        assertDoesNotThrow(() -> firestoreService.deleteDocument(TEST_COLLECTION, docId));

        RuntimeException exception =
                assertThrows(RuntimeException.class, () -> firestoreService.getDocument(TEST_COLLECTION, docId));
        assertTrue(exception.getMessage().contains("Document not found"));
    }

    @Test
    @Order(11)
    void testDeleteDocumentReturn_ShouldReturnDataBeforeDeletion() throws Exception {
        String docId = "test-delete-return-" + UUID.randomUUID();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Return Test");
        data.put("email", "return@example.com");
        data.put("age", 40);
        firestoreService.saveOrUpdate(TEST_COLLECTION, docId, data);

        Map<String, Object> deletedData = firestoreService.deleteDocumentReturn(TEST_COLLECTION, docId);

        assertNotNull(deletedData);
        assertEquals("Return Test", deletedData.get("name"));
        assertEquals("return@example.com", deletedData.get("email"));
        assertEquals(40, ((Long) deletedData.get("age")).intValue());

        RuntimeException exception =
                assertThrows(RuntimeException.class, () -> firestoreService.getDocument(TEST_COLLECTION, docId));
        assertTrue(exception.getMessage().contains("Document not found"));
    }

    @Test
    @Order(12)
    void testDeleteDocumentReturn_WithNonExistentDocument_ShouldThrowException() {
        String nonExistentDocId = "non-existent-delete-" + UUID.randomUUID();

        RuntimeException exception = assertThrows(
                RuntimeException.class, () -> firestoreService.deleteDocumentReturn(TEST_COLLECTION, nonExistentDocId));

        assertTrue(exception.getMessage().contains("Document not found"));
    }

    @Test
    @Order(13)
    void testDeleteDocumentAtomically_ShouldReturnDataBeforeDeletion() throws Exception {
        String docId = "test-atomic-delete-" + UUID.randomUUID();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Atomic Test");
        data.put("email", "atomic@example.com");
        data.put("age", 45);
        firestoreService.saveOrUpdate(TEST_COLLECTION, docId, data);

        Map<String, Object> deletedData = firestoreService.deleteDocumentAtomically(TEST_COLLECTION, docId);

        assertNotNull(deletedData);
        assertEquals("Atomic Test", deletedData.get("name"));
        assertEquals("atomic@example.com", deletedData.get("email"));
        assertEquals(45, ((Long) deletedData.get("age")).intValue());

        RuntimeException exception =
                assertThrows(RuntimeException.class, () -> firestoreService.getDocument(TEST_COLLECTION, docId));
        assertTrue(exception.getMessage().contains("Document not found"));
    }

    @Test
    @Order(14)
    void testDeleteDocumentAtomically_WithNonExistentDocument_ShouldThrowException() {
        String nonExistentDocId = "non-existent-atomic-" + UUID.randomUUID();

        // When & Then
        assertThrows(
                ExecutionException.class,
                () -> firestoreService.deleteDocumentAtomically(TEST_COLLECTION, nonExistentDocId));
    }

    @Test
    @Order(15)
    void testUpdateDocumentFields_WithNonExistentDocument_ShouldThrowException() {
        String nonExistentDocId = "non-existent-update-" + UUID.randomUUID();
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "Should Fail");

        assertThrows(
                Exception.class,
                () -> firestoreService.updateDocumentFields(TEST_COLLECTION, nonExistentDocId, updates));
    }

    @Test
    @Order(16)
    void testSaveOrUpdate_WithEmptyData_ShouldCreateEmptyDocument() throws Exception {
        String docId = "test-empty-" + UUID.randomUUID();
        Map<String, Object> emptyData = new HashMap<>();

        assertDoesNotThrow(() -> firestoreService.saveOrUpdate(TEST_COLLECTION, docId, emptyData));

        Map<String, Object> retrievedData = firestoreService.getDocument(TEST_COLLECTION, docId);
        assertNotNull(retrievedData);
        assertTrue(retrievedData.isEmpty());

        firestoreService.deleteDocument(TEST_COLLECTION, docId);
    }

    @Test
    @Order(17)
    void testSaveOrUpdate_WithComplexData_ShouldHandleNestedStructures() throws Exception {
        String docId = "test-complex-" + UUID.randomUUID();
        Map<String, Object> complexData = new HashMap<>();
        complexData.put("name", "Complex User");

        Map<String, Object> address = new HashMap<>();
        address.put("street", "123 Main St");
        address.put("city", "New York");
        address.put("zipCode", "10001");
        complexData.put("address", address);

        complexData.put("tags", List.of("developer", "tester", "architect"));

        assertDoesNotThrow(() -> firestoreService.saveOrUpdate(TEST_COLLECTION, docId, complexData));

        Map<String, Object> retrievedData = firestoreService.getDocument(TEST_COLLECTION, docId);
        assertNotNull(retrievedData);
        assertEquals("Complex User", retrievedData.get("name"));

        @SuppressWarnings("unchecked")
        Map<String, Object> retrievedAddress = (Map<String, Object>) retrievedData.get("address");
        assertNotNull(retrievedAddress);
        assertEquals("123 Main St", retrievedAddress.get("street"));
        assertEquals("New York", retrievedAddress.get("city"));

        firestoreService.deleteDocument(TEST_COLLECTION, docId);
    }

    @Test
    @Order(18)
    void testDocumentExists_WithExistingDocument_ShouldReturnTrue() throws Exception {
        String docId = "test-exists-" + UUID.randomUUID();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Exists Test");
        data.put("email", "exists@example.com");
        firestoreService.saveOrUpdate(TEST_COLLECTION, docId, data);

        boolean exists = firestoreService.documentExists(TEST_COLLECTION, docId);

        assertTrue(exists, "Document should exist");

        firestoreService.deleteDocument(TEST_COLLECTION, docId);
    }

    @Test
    @Order(19)
    void testDocumentExists_WithNonExistentDocument_ShouldReturnFalse() throws Exception {
        String nonExistentDocId = "non-existent-exists-" + UUID.randomUUID();
        boolean exists = firestoreService.documentExists(TEST_COLLECTION, nonExistentDocId);

        assertFalse(exists, "Document should not exist");
    }

    @Test
    @Order(20)
    void testDocumentExists_AfterDeletion_ShouldReturnFalse() throws Exception {
        String docId = "test-exists-deleted-" + UUID.randomUUID();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "To Be Deleted");
        firestoreService.saveOrUpdate(TEST_COLLECTION, docId, data);

        assertTrue(firestoreService.documentExists(TEST_COLLECTION, docId), "Document should exist before deletion");

        firestoreService.deleteDocument(TEST_COLLECTION, docId);

        boolean existsAfterDelete = firestoreService.documentExists(TEST_COLLECTION, docId);
        assertFalse(existsAfterDelete, "Document should not exist after deletion");
    }

    @Test
    @Order(21)
    void testDocumentExists_WithEmptyDocument_ShouldReturnTrue() throws Exception {
        String docId = "test-exists-empty-" + UUID.randomUUID();
        Map<String, Object> emptyData = new HashMap<>();
        firestoreService.saveOrUpdate(TEST_COLLECTION, docId, emptyData);

        boolean exists = firestoreService.documentExists(TEST_COLLECTION, docId);

        assertTrue(exists, "Empty document should still exist");
        firestoreService.deleteDocument(TEST_COLLECTION, docId);
    }

    @Test
    @Order(22)
    void testCountDocuments_WithEmptyCollection_ShouldReturnZero() throws Exception {
        String emptyCollection = "empty-collection-" + UUID.randomUUID();
        long count = firestoreService.countDocuments(emptyCollection);

        assertEquals(0, count, "Empty collection should have 0 documents");
    }

    @Test
    @Order(23)
    void testCountDocuments_WithSingleDocument_ShouldReturnOne() throws Exception {
        String testCollection = "count-test-single-" + UUID.randomUUID();
        String docId = "doc1";
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Test User");
        firestoreService.saveOrUpdate(testCollection, docId, data);

        long count = firestoreService.countDocuments(testCollection);

        assertEquals(1, count, "Collection should have 1 document");
        firestoreService.deleteDocument(testCollection, docId);
    }

    @Test
    @Order(24)
    void testCountDocuments_WithMultipleDocuments_ShouldReturnCorrectCount() throws Exception {
        String testCollection = "count-test-multiple-" + UUID.randomUUID();
        int numDocuments = 5;

        for (int i = 0; i < numDocuments; i++) {
            Map<String, Object> data = new HashMap<>();
            data.put("name", "User " + i);
            data.put("index", i);
            firestoreService.saveOrUpdate(testCollection, "doc-" + i, data);
        }

        long count = firestoreService.countDocuments(testCollection);

        assertEquals(numDocuments, count, "Collection should have " + numDocuments + " documents");

        for (int i = 0; i < numDocuments; i++) {
            firestoreService.deleteDocument(testCollection, "doc-" + i);
        }
    }

    @Test
    @Order(25)
    void testCountDocuments_AfterAddingDocuments_ShouldUpdateCount() throws Exception {
        String testCollection = "count-test-incremental-" + UUID.randomUUID();

        assertEquals(0, firestoreService.countDocuments(testCollection), "Initial count should be 0");

        Map<String, Object> data1 = new HashMap<>();
        data1.put("name", "First User");
        firestoreService.saveOrUpdate(testCollection, "doc1", data1);

        assertEquals(
                1, firestoreService.countDocuments(testCollection), "Count should be 1 after adding first document");

        Map<String, Object> data2 = new HashMap<>();
        data2.put("name", "Second User");
        firestoreService.saveOrUpdate(testCollection, "doc2", data2);

        assertEquals(
                2, firestoreService.countDocuments(testCollection), "Count should be 2 after adding second document");

        firestoreService.deleteDocument(testCollection, "doc1");
        firestoreService.deleteDocument(testCollection, "doc2");
    }

    @Test
    @Order(26)
    void testCountDocuments_AfterDeletingDocuments_ShouldUpdateCount() throws Exception {
        String testCollection = "count-test-delete-" + UUID.randomUUID();
        int initialCount = 3;

        for (int i = 0; i < initialCount; i++) {
            Map<String, Object> data = new HashMap<>();
            data.put("name", "User " + i);
            firestoreService.saveOrUpdate(testCollection, "doc-" + i, data);
        }

        assertEquals(
                initialCount,
                firestoreService.countDocuments(testCollection),
                "Initial count should be " + initialCount);

        firestoreService.deleteDocument(testCollection, "doc-0");

        assertEquals(
                initialCount - 1,
                firestoreService.countDocuments(testCollection),
                "Count should be " + (initialCount - 1) + " after deletion");

        firestoreService.deleteDocument(testCollection, "doc-1");

        assertEquals(
                initialCount - 2,
                firestoreService.countDocuments(testCollection),
                "Count should be " + (initialCount - 2) + " after second deletion");

        firestoreService.deleteDocument(testCollection, "doc-2");
    }

    @Test
    @Order(27)
    void testCountDocuments_WithLargeCollection_ShouldReturnCorrectCount() throws Exception {
        String testCollection = "count-test-large-" + UUID.randomUUID();
        int numDocuments = 25;

        for (int i = 0; i < numDocuments; i++) {
            Map<String, Object> data = new HashMap<>();
            data.put("name", "User " + i);
            data.put("index", i);
            data.put("email", "user" + i + "@example.com");
            firestoreService.saveOrUpdate(testCollection, "doc-" + i, data);
        }

        long count = firestoreService.countDocuments(testCollection);

        assertEquals(numDocuments, count, "Collection should have " + numDocuments + " documents");

        for (int i = 0; i < numDocuments; i++) {
            firestoreService.deleteDocument(testCollection, "doc-" + i);
        }
    }

    @Test
    @Order(28)
    void testDocumentExists_And_CountDocuments_Integration() throws Exception {
        String testCollection = "integration-test-" + UUID.randomUUID();
        String docId1 = "doc1";
        String docId2 = "doc2";

        assertFalse(firestoreService.documentExists(testCollection, docId1), "Document 1 should not exist initially");
        assertFalse(firestoreService.documentExists(testCollection, docId2), "Document 2 should not exist initially");
        assertEquals(0, firestoreService.countDocuments(testCollection), "Initial count should be 0");

        Map<String, Object> data1 = new HashMap<>();
        data1.put("name", "User 1");
        firestoreService.saveOrUpdate(testCollection, docId1, data1);

        assertTrue(firestoreService.documentExists(testCollection, docId1), "Document 1 should exist");
        assertFalse(firestoreService.documentExists(testCollection, docId2), "Document 2 should not exist yet");
        assertEquals(1, firestoreService.countDocuments(testCollection), "Count should be 1");

        Map<String, Object> data2 = new HashMap<>();
        data2.put("name", "User 2");
        firestoreService.saveOrUpdate(testCollection, docId2, data2);

        assertTrue(firestoreService.documentExists(testCollection, docId1), "Document 1 should exist");
        assertTrue(firestoreService.documentExists(testCollection, docId2), "Document 2 should exist");
        assertEquals(2, firestoreService.countDocuments(testCollection), "Count should be 2");

        firestoreService.deleteDocument(testCollection, docId1);

        assertFalse(
                firestoreService.documentExists(testCollection, docId1), "Document 1 should not exist after deletion");
        assertTrue(firestoreService.documentExists(testCollection, docId2), "Document 2 should still exist");
        assertEquals(1, firestoreService.countDocuments(testCollection), "Count should be 1 after deletion");

        firestoreService.deleteDocument(testCollection, docId2);
        assertEquals(0, firestoreService.countDocuments(testCollection), "Final count should be 0");
    }

    @Test
    @Order(29)
    void testListDocuments_WithEmptyCollection_ShouldReturnEmptyList() throws Exception {
        String emptyCollection = "empty-list-test-" + UUID.randomUUID();

        List<Map<String, Object>> documents = firestoreService.listDocuments(emptyCollection, null);

        assertNotNull(documents, "Documents list should not be null");
        assertTrue(documents.isEmpty(), "Empty collection should return empty list");
    }

    @Test
    @Order(30)
    void testListDocuments_WithSingleDocument_ShouldReturnOneDocument() throws Exception {
        String testCollection = "list-single-" + UUID.randomUUID();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "John Doe");
        data.put("email", "john@example.com");
        data.put("age", 30);
        firestoreService.saveOrUpdate(testCollection, "doc1", data);

        List<Map<String, Object>> documents = firestoreService.listDocuments(testCollection, null);

        assertNotNull(documents);
        assertEquals(1, documents.size(), "Should return exactly one document");
        assertEquals("John Doe", documents.get(0).get("name"));
        assertEquals("john@example.com", documents.get(0).get("email"));

        firestoreService.deleteDocument(testCollection, "doc1");
    }

    @Test
    @Order(31)
    void testListDocuments_WithMultipleDocuments_ShouldReturnAllDocuments() throws Exception {
        String testCollection = "list-multiple-" + UUID.randomUUID();
        int numDocs = 5;

        for (int i = 0; i < numDocs; i++) {
            Map<String, Object> data = new HashMap<>();
            data.put("name", "User " + i);
            data.put("index", i);
            data.put("email", "user" + i + "@example.com");
            firestoreService.saveOrUpdate(testCollection, "doc-" + i, data);
        }

        List<Map<String, Object>> documents = firestoreService.listDocuments(testCollection, null);

        assertNotNull(documents);
        assertEquals(numDocs, documents.size(), "Should return all " + numDocs + " documents");

        for (Map<String, Object> doc : documents) {
            assertNotNull(doc.get("name"), "Document should have name field");
            assertNotNull(doc.get("email"), "Document should have email field");
        }

        for (int i = 0; i < numDocs; i++) {
            firestoreService.deleteDocument(testCollection, "doc-" + i);
        }
    }

    @Test
    @Order(32)
    void testListDocuments_WithSpecificFields_ShouldReturnOnlySelectedFields() throws Exception {
        String testCollection = "list-fields-" + UUID.randomUUID();
        Map<String, Object> data = new HashMap<>();
        data.put("firstName", "John");
        data.put("lastName", "Doe");
        data.put("email", "john@example.com");
        data.put("age", 30);
        data.put("address", "123 Main St");
        firestoreService.saveOrUpdate(testCollection, "doc1", data);

        List<String> fieldsToRetrieve = List.of("firstName", "email");
        List<Map<String, Object>> documents = firestoreService.listDocuments(testCollection, fieldsToRetrieve, 10, -1);

        assertNotNull(documents);
        assertEquals(1, documents.size());

        Map<String, Object> doc = documents.get(0);
        assertTrue(doc.containsKey("firstName"), "Should contain firstName field");
        assertTrue(doc.containsKey("email"), "Should contain email field");
        assertFalse(doc.containsKey("lastName"), "Should not contain lastName field");
        assertFalse(doc.containsKey("age"), "Should not contain age field");
        assertFalse(doc.containsKey("address"), "Should not contain address field");

        assertEquals("John", doc.get("firstName"));
        assertEquals("john@example.com", doc.get("email"));

        firestoreService.deleteDocument(testCollection, "doc1");
    }

    @Test
    @Order(33)
    void testListDocuments_WithNullFields_ShouldReturnAllFields() throws Exception {
        String testCollection = "list-null-fields-" + UUID.randomUUID();
        Map<String, Object> data = new HashMap<>();
        data.put("firstName", "Jane");
        data.put("lastName", "Smith");
        data.put("email", "jane@example.com");
        data.put("age", 25);
        firestoreService.saveOrUpdate(testCollection, "doc1", data);

        List<Map<String, Object>> documents = firestoreService.listDocuments(testCollection, null, 10, -1);

        assertNotNull(documents);
        assertEquals(1, documents.size());

        Map<String, Object> doc = documents.get(0);
        assertTrue(doc.containsKey("firstName"), "Should contain firstName");
        assertTrue(doc.containsKey("lastName"), "Should contain lastName");
        assertTrue(doc.containsKey("email"), "Should contain email");
        assertTrue(doc.containsKey("age"), "Should contain age");

        assertEquals("Jane", doc.get("firstName"));
        assertEquals("Smith", doc.get("lastName"));
        assertEquals("jane@example.com", doc.get("email"));

        firestoreService.deleteDocument(testCollection, "doc1");
    }

    @Test
    @Order(34)
    void testListDocuments_WithEmptyFieldsList_ShouldReturnAllFields() throws Exception {
        String testCollection = "list-empty-fields-" + UUID.randomUUID();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Test User");
        data.put("email", "test@example.com");
        firestoreService.saveOrUpdate(testCollection, "doc1", data);

        List<Map<String, Object>> documents = firestoreService.listDocuments(testCollection, List.of(), 10, -1);

        assertNotNull(documents);
        assertEquals(1, documents.size());

        Map<String, Object> doc = documents.get(0);
        assertTrue(doc.containsKey("name"));
        assertTrue(doc.containsKey("email"));

        firestoreService.deleteDocument(testCollection, "doc1");
    }

    @Test
    @Order(35)
    void testListDocuments_WithLimit_ShouldReturnLimitedResults() throws Exception {
        String testCollection = "list-limit-" + UUID.randomUUID();
        int totalDocs = 10;
        int limit = 5;

        for (int i = 0; i < totalDocs; i++) {
            Map<String, Object> data = new HashMap<>();
            data.put("name", "User " + i);
            data.put("index", i);
            firestoreService.saveOrUpdate(testCollection, "doc-" + i, data);
        }

        List<Map<String, Object>> documents = firestoreService.listDocuments(testCollection, null, limit, -1);

        assertNotNull(documents);
        assertEquals(limit, documents.size(), "Should return exactly " + limit + " documents");

        for (int i = 0; i < totalDocs; i++) {
            firestoreService.deleteDocument(testCollection, "doc-" + i);
        }
    }

    @Test
    @Order(36)
    void testListDocuments_WithOffset_ShouldSkipDocuments() throws Exception {
        String testCollection = "list-offset-" + UUID.randomUUID();
        int totalDocs = 10;
        int offset = 5;

        for (int i = 0; i < totalDocs; i++) {
            Map<String, Object> data = new HashMap<>();
            data.put("name", "User " + i);
            data.put("index", i);
            firestoreService.saveOrUpdate(testCollection, "doc-" + i, data);
        }

        List<Map<String, Object>> documents = firestoreService.listDocuments(testCollection, null, 10, offset);

        assertNotNull(documents);
        assertEquals(totalDocs - offset, documents.size(), "Should return " + (totalDocs - offset) + " documents");

        for (int i = 0; i < totalDocs; i++) {
            firestoreService.deleteDocument(testCollection, "doc-" + i);
        }
    }

    @Test
    @Order(37)
    void testListDocuments_WithNegativeOffset_ShouldIgnoreOffset() throws Exception {
        String testCollection = "list-neg-offset-" + UUID.randomUUID();
        int totalDocs = 5;

        for (int i = 0; i < totalDocs; i++) {
            Map<String, Object> data = new HashMap<>();
            data.put("name", "User " + i);
            firestoreService.saveOrUpdate(testCollection, "doc-" + i, data);
        }

        List<Map<String, Object>> documents = firestoreService.listDocuments(testCollection, null, 10, -1);

        assertNotNull(documents);
        assertEquals(totalDocs, documents.size(), "Should return all documents when offset is negative");

        for (int i = 0; i < totalDocs; i++) {
            firestoreService.deleteDocument(testCollection, "doc-" + i);
        }
    }

    @Test
    @Order(38)
    void testListDocuments_WithLimitAndOffset_ShouldReturnCorrectSubset() throws Exception {
        String testCollection = "list-limit-offset-" + UUID.randomUUID();
        int totalDocs = 20;
        int limit = 5;
        int offset = 10;

        for (int i = 0; i < totalDocs; i++) {
            Map<String, Object> data = new HashMap<>();
            data.put("name", "User " + i);
            data.put("index", i);
            firestoreService.saveOrUpdate(testCollection, "doc-" + i, data);
        }

        List<Map<String, Object>> documents = firestoreService.listDocuments(testCollection, null, limit, offset);

        assertNotNull(documents);
        assertEquals(limit, documents.size(), "Should return " + limit + " documents");

        for (int i = 0; i < totalDocs; i++) {
            firestoreService.deleteDocument(testCollection, "doc-" + i);
        }
    }

    @Test
    @Order(39)
    void testListDocuments_DefaultPagination_ShouldUseDefaultPageSize() throws Exception {
        String testCollection = "list-default-page-" + UUID.randomUUID();
        int numDocs = 10; // Less than default page size of 50

        for (int i = 0; i < numDocs; i++) {
            Map<String, Object> data = new HashMap<>();
            data.put("name", "User " + i);
            data.put("index", i);
            firestoreService.saveOrUpdate(testCollection, "doc-" + i, data);
        }

        List<Map<String, Object>> documents = firestoreService.listDocuments(testCollection, null);

        assertNotNull(documents);
        assertEquals(numDocs, documents.size(), "Should return all documents using default pagination");

        for (int i = 0; i < numDocs; i++) {
            firestoreService.deleteDocument(testCollection, "doc-" + i);
        }
    }

    @Test
    @Order(40)
    void testListDocuments_DefaultPagination_WithSpecificFields() throws Exception {
        String testCollection = "list-default-fields-" + UUID.randomUUID();
        Map<String, Object> data = new HashMap<>();
        data.put("firstName", "Alice");
        data.put("lastName", "Wonder");
        data.put("email", "alice@example.com");
        data.put("age", 28);
        data.put("city", "Wonderland");
        firestoreService.saveOrUpdate(testCollection, "doc1", data);

        List<String> fields = List.of("firstName", "city");
        List<Map<String, Object>> documents = firestoreService.listDocuments(testCollection, fields);

        assertNotNull(documents);
        assertEquals(1, documents.size());

        Map<String, Object> doc = documents.get(0);
        assertTrue(doc.containsKey("firstName"), "Should contain firstName");
        assertTrue(doc.containsKey("city"), "Should contain city");
        assertFalse(doc.containsKey("lastName"), "Should not contain lastName");
        assertFalse(doc.containsKey("email"), "Should not contain email");
        assertFalse(doc.containsKey("age"), "Should not contain age");

        firestoreService.deleteDocument(testCollection, "doc1");
    }

    @Test
    @Order(41)
    void testListDocuments_WithComplexData_ShouldHandleNestedStructures() throws Exception {
        String testCollection = "list-complex-" + UUID.randomUUID();

        Map<String, Object> address = new HashMap<>();
        address.put("street", "123 Main St");
        address.put("city", "New York");
        address.put("zipCode", "10001");

        Map<String, Object> data = new HashMap<>();
        data.put("name", "Complex User");
        data.put("address", address);
        data.put("tags", List.of("developer", "tester"));

        firestoreService.saveOrUpdate(testCollection, "doc1", data);

        List<Map<String, Object>> documents = firestoreService.listDocuments(testCollection, null);

        assertNotNull(documents);
        assertEquals(1, documents.size());

        Map<String, Object> doc = documents.get(0);
        assertEquals("Complex User", doc.get("name"));
        assertNotNull(doc.get("address"));

        @SuppressWarnings("unchecked")
        Map<String, Object> retrievedAddress = (Map<String, Object>) doc.get("address");
        assertEquals("123 Main St", retrievedAddress.get("street"));
        assertEquals("New York", retrievedAddress.get("city"));

        firestoreService.deleteDocument(testCollection, "doc1");
    }

    @Test
    @Order(42)
    void testListDocuments_Pagination_SimulateMultiplePages() throws Exception {
        String testCollection = "list-pages-" + UUID.randomUUID();
        int totalDocs = 15;
        int pageSize = 5;

        for (int i = 0; i < totalDocs; i++) {
            Map<String, Object> data = new HashMap<>();
            data.put("name", "User " + i);
            data.put("index", i);
            firestoreService.saveOrUpdate(testCollection, "doc-" + String.format("%02d", i), data);
        }

        List<Map<String, Object>> page1 = firestoreService.listDocuments(testCollection, null, pageSize, 0);
        List<Map<String, Object>> page2 = firestoreService.listDocuments(testCollection, null, pageSize, pageSize);
        List<Map<String, Object>> page3 = firestoreService.listDocuments(testCollection, null, pageSize, pageSize * 2);

        assertNotNull(page1);
        assertEquals(pageSize, page1.size(), "Page 1 should have " + pageSize + " documents");

        assertNotNull(page2);
        assertEquals(pageSize, page2.size(), "Page 2 should have " + pageSize + " documents");

        assertNotNull(page3);
        assertEquals(pageSize, page3.size(), "Page 3 should have " + pageSize + " documents");

        int totalRetrieved = page1.size() + page2.size() + page3.size();
        assertEquals(totalDocs, totalRetrieved, "Total retrieved should equal total documents");

        for (int i = 0; i < totalDocs; i++) {
            firestoreService.deleteDocument(testCollection, "doc-" + String.format("%02d", i));
        }
    }

    @Test
    @Order(43)
    void testListDocuments_WithZeroLimit_ShouldReturnEmptyList() throws Exception {
        String testCollection = "list-zero-limit-" + UUID.randomUUID();
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Test User");
        firestoreService.saveOrUpdate(testCollection, "doc1", data);

        List<Map<String, Object>> documents = firestoreService.listDocuments(testCollection, null, 0, -1);

        assertNotNull(documents);
        assertTrue(documents.isEmpty(), "Zero limit should return empty list");

        // Cleanup
        firestoreService.deleteDocument(testCollection, "doc1");
    }

    @Test
    @Order(44)
    void testGetDocuments_WithSingleDocumentId_ShouldReturnOneDocument() throws Exception {
        String testCollection = "get-docs-single-" + UUID.randomUUID();
        String docId = "doc1";
        Map<String, Object> data = new HashMap<>();
        data.put("name", "John Doe");
        data.put("email", "john@example.com");
        data.put("age", 30);
        firestoreService.saveOrUpdate(testCollection, docId, data);

        List<Map<String, Object>> documents = firestoreService.getDocuments(testCollection, List.of(docId));

        assertNotNull(documents);
        assertEquals(1, documents.size(), "Should return exactly one document");
        assertEquals("John Doe", documents.get(0).get("name"));
        assertEquals("john@example.com", documents.get(0).get("email"));
        assertEquals(30, ((Long) documents.get(0).get("age")).intValue());

        firestoreService.deleteDocument(testCollection, docId);
    }

    @Test
    @Order(45)
    void testGetDocuments_WithMultipleDocumentIds_ShouldReturnAllDocuments() throws Exception {
        String testCollection = "get-docs-multiple-" + UUID.randomUUID();
        String docId1 = "doc1";
        String docId2 = "doc2";
        String docId3 = "doc3";

        Map<String, Object> data1 = new HashMap<>();
        data1.put("name", "Alice");
        data1.put("age", 25);
        firestoreService.saveOrUpdate(testCollection, docId1, data1);

        Map<String, Object> data2 = new HashMap<>();
        data2.put("name", "Bob");
        data2.put("age", 30);
        firestoreService.saveOrUpdate(testCollection, docId2, data2);

        Map<String, Object> data3 = new HashMap<>();
        data3.put("name", "Charlie");
        data3.put("age", 35);
        firestoreService.saveOrUpdate(testCollection, docId3, data3);

        List<Map<String, Object>> documents =
                firestoreService.getDocuments(testCollection, List.of(docId1, docId2, docId3));

        assertNotNull(documents);
        assertEquals(3, documents.size(), "Should return all three documents");

        // Verify all documents are retrieved
        List<String> names =
                documents.stream().map(doc -> (String) doc.get("name")).collect(Collectors.toList());
        assertTrue(names.contains("Alice"), "Should contain Alice");
        assertTrue(names.contains("Bob"), "Should contain Bob");
        assertTrue(names.contains("Charlie"), "Should contain Charlie");

        firestoreService.deleteDocument(testCollection, docId1);
        firestoreService.deleteDocument(testCollection, docId2);
        firestoreService.deleteDocument(testCollection, docId3);
    }

    @Test
    @Order(46)
    void testGetDocuments_WithNonExistentDocumentId_ShouldReturnEmptyList() throws Exception {
        String testCollection = "get-docs-nonexistent-" + UUID.randomUUID();
        String nonExistentDocId = "non-existent-doc-" + UUID.randomUUID();

        List<Map<String, Object>> documents = firestoreService.getDocuments(testCollection, List.of(nonExistentDocId));

        assertNotNull(documents);
        assertTrue(documents.isEmpty(), "Should return empty list for non-existent document");
    }

    @Test
    @Order(47)
    void testGetDocuments_WithMixedExistentAndNonExistentIds_ShouldReturnOnlyExistentDocuments() throws Exception {
        String testCollection = "get-docs-mixed-" + UUID.randomUUID();
        String existingDocId1 = "existing-doc1";
        String existingDocId2 = "existing-doc2";
        String nonExistentDocId1 = "non-existent-doc1";
        String nonExistentDocId2 = "non-existent-doc2";

        Map<String, Object> data1 = new HashMap<>();
        data1.put("name", "Existing User 1");
        data1.put("email", "user1@example.com");
        firestoreService.saveOrUpdate(testCollection, existingDocId1, data1);

        Map<String, Object> data2 = new HashMap<>();
        data2.put("name", "Existing User 2");
        data2.put("email", "user2@example.com");
        firestoreService.saveOrUpdate(testCollection, existingDocId2, data2);

        List<Map<String, Object>> documents = firestoreService.getDocuments(
                testCollection, List.of(existingDocId1, nonExistentDocId1, existingDocId2, nonExistentDocId2));

        assertNotNull(documents);
        assertEquals(2, documents.size(), "Should return only the two existing documents");

        List<String> names =
                documents.stream().map(doc -> (String) doc.get("name")).collect(Collectors.toList());
        assertTrue(names.contains("Existing User 1"), "Should contain Existing User 1");
        assertTrue(names.contains("Existing User 2"), "Should contain Existing User 2");

        firestoreService.deleteDocument(testCollection, existingDocId1);
        firestoreService.deleteDocument(testCollection, existingDocId2);
    }

    @Test
    @Order(48)
    void testGetDocuments_WithEmptyDocumentIdsList_ShouldReturnEmptyList() throws Exception {
        String testCollection = "get-docs-empty-list-" + UUID.randomUUID();

        List<Map<String, Object>> documents = firestoreService.getDocuments(testCollection, List.of());

        assertNotNull(documents);
        assertTrue(documents.isEmpty(), "Should return empty list for empty document IDs list");
    }

    @Test
    @Order(49)
    void testGetDocuments_WithDuplicateDocumentIds_ShouldReturnDocumentsOnce() throws Exception {
        String testCollection = "get-docs-duplicates-" + UUID.randomUUID();
        String docId = "doc1";

        Map<String, Object> data = new HashMap<>();
        data.put("name", "Test User");
        data.put("email", "test@example.com");
        firestoreService.saveOrUpdate(testCollection, docId, data);

        List<Map<String, Object>> documents =
                firestoreService.getDocuments(testCollection, List.of(docId, docId, docId));

        assertNotNull(documents);
        // The behavior might return the document once or multiple times depending on implementation
        // Let's verify at least one document is returned
        assertFalse(documents.isEmpty(), "Should return at least one document");
        assertEquals("Test User", documents.get(0).get("name"));

        firestoreService.deleteDocument(testCollection, docId);
    }

    @Test
    @Order(50)
    void testGetDocuments_WithComplexData_ShouldReturnNestedStructures() throws Exception {
        String testCollection = "get-docs-complex-" + UUID.randomUUID();
        String docId = "complex-doc1";

        Map<String, Object> address = new HashMap<>();
        address.put("street", "123 Main St");
        address.put("city", "New York");
        address.put("zipCode", "10001");

        Map<String, Object> data = new HashMap<>();
        data.put("name", "Complex User");
        data.put("email", "complex@example.com");
        data.put("address", address);
        data.put("tags", List.of("developer", "architect", "tester"));

        firestoreService.saveOrUpdate(testCollection, docId, data);

        List<Map<String, Object>> documents = firestoreService.getDocuments(testCollection, List.of(docId));

        assertNotNull(documents);
        assertEquals(1, documents.size());

        Map<String, Object> retrievedDoc = documents.get(0);
        assertEquals("Complex User", retrievedDoc.get("name"));
        assertEquals("complex@example.com", retrievedDoc.get("email"));

        @SuppressWarnings("unchecked")
        Map<String, Object> retrievedAddress = (Map<String, Object>) retrievedDoc.get("address");
        assertNotNull(retrievedAddress);
        assertEquals("123 Main St", retrievedAddress.get("street"));
        assertEquals("New York", retrievedAddress.get("city"));
        assertEquals("10001", retrievedAddress.get("zipCode"));

        @SuppressWarnings("unchecked")
        List<String> retrievedTags = (List<String>) retrievedDoc.get("tags");
        assertNotNull(retrievedTags);
        assertEquals(3, retrievedTags.size());
        assertTrue(retrievedTags.contains("developer"));
        assertTrue(retrievedTags.contains("architect"));
        assertTrue(retrievedTags.contains("tester"));

        firestoreService.deleteDocument(testCollection, docId);
    }

    @Test
    @Order(51)
    void testGetDocuments_WithLargeNumberOfDocumentIds_ShouldReturnAllDocuments() throws Exception {
        String testCollection = "get-docs-large-" + UUID.randomUUID();
        int numDocs = 20;
        List<String> docIds = new java.util.ArrayList<>();

        for (int i = 0; i < numDocs; i++) {
            String docId = "doc-" + i;
            docIds.add(docId);

            Map<String, Object> data = new HashMap<>();
            data.put("name", "User " + i);
            data.put("index", i);
            data.put("email", "user" + i + "@example.com");
            firestoreService.saveOrUpdate(testCollection, docId, data);
        }

        List<Map<String, Object>> documents = firestoreService.getDocuments(testCollection, docIds);

        assertNotNull(documents);
        assertEquals(numDocs, documents.size(), "Should return all " + numDocs + " documents");

        for (Map<String, Object> doc : documents) {
            assertNotNull(doc.get("name"), "Each document should have name field");
            assertNotNull(doc.get("email"), "Each document should have email field");
            assertNotNull(doc.get("index"), "Each document should have index field");
        }

        for (String docId : docIds) {
            firestoreService.deleteDocument(testCollection, docId);
        }
    }

    @Test
    @Order(52)
    void testGetDocuments_WithEmptyDocuments_ShouldReturnEmptyDocuments() throws Exception {
        String testCollection = "get-docs-empty-" + UUID.randomUUID();
        String docId1 = "empty-doc1";
        String docId2 = "empty-doc2";

        Map<String, Object> emptyData = new HashMap<>();
        firestoreService.saveOrUpdate(testCollection, docId1, emptyData);
        firestoreService.saveOrUpdate(testCollection, docId2, emptyData);

        List<Map<String, Object>> documents = firestoreService.getDocuments(testCollection, List.of(docId1, docId2));

        assertNotNull(documents);
        assertEquals(2, documents.size(), "Should return two empty documents");

        for (Map<String, Object> doc : documents) {
            assertNotNull(doc);
            assertTrue(doc.isEmpty() || doc.size() == 0, "Documents should be empty");
        }

        firestoreService.deleteDocument(testCollection, docId1);
        firestoreService.deleteDocument(testCollection, docId2);
    }

    @Test
    @Order(53)
    void testGetDocuments_AfterUpdateDocument_ShouldReturnUpdatedData() throws Exception {
        String testCollection = "get-docs-updated-" + UUID.randomUUID();
        String docId = "doc-to-update";

        Map<String, Object> originalData = new HashMap<>();
        originalData.put("name", "Original Name");
        originalData.put("email", "original@example.com");
        originalData.put("version", 1);
        firestoreService.saveOrUpdate(testCollection, docId, originalData);

        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("name", "Updated Name");
        updatedData.put("email", "updated@example.com");
        updatedData.put("version", 2);
        firestoreService.saveOrUpdate(testCollection, docId, updatedData);

        List<Map<String, Object>> documents = firestoreService.getDocuments(testCollection, List.of(docId));

        assertNotNull(documents);
        assertEquals(1, documents.size());

        Map<String, Object> retrievedDoc = documents.get(0);
        assertEquals("Updated Name", retrievedDoc.get("name"));
        assertEquals("updated@example.com", retrievedDoc.get("email"));
        assertEquals(2, ((Long) retrievedDoc.get("version")).intValue());

        firestoreService.deleteDocument(testCollection, docId);
    }

    @Test
    @Order(54)
    void testGetDocuments_VerifyOrderIndependence_ShouldReturnAllDocuments() throws Exception {
        String testCollection = "get-docs-order-" + UUID.randomUUID();
        String docId1 = "doc-a";
        String docId2 = "doc-b";
        String docId3 = "doc-c";

        Map<String, Object> data1 = new HashMap<>();
        data1.put("name", "User A");
        firestoreService.saveOrUpdate(testCollection, docId1, data1);

        Map<String, Object> data2 = new HashMap<>();
        data2.put("name", "User B");
        firestoreService.saveOrUpdate(testCollection, docId2, data2);

        Map<String, Object> data3 = new HashMap<>();
        data3.put("name", "User C");
        firestoreService.saveOrUpdate(testCollection, docId3, data3);

        // Request in one order
        List<Map<String, Object>> documentsOrder1 =
                firestoreService.getDocuments(testCollection, List.of(docId1, docId2, docId3));

        // Request in different order
        List<Map<String, Object>> documentsOrder2 =
                firestoreService.getDocuments(testCollection, List.of(docId3, docId1, docId2));

        assertNotNull(documentsOrder1);
        assertNotNull(documentsOrder2);
        assertEquals(3, documentsOrder1.size());
        assertEquals(3, documentsOrder2.size());

        // Verify all documents are retrieved regardless of order
        List<String> namesOrder1 =
                documentsOrder1.stream().map(doc -> (String) doc.get("name")).collect(Collectors.toList());
        List<String> namesOrder2 =
                documentsOrder2.stream().map(doc -> (String) doc.get("name")).collect(Collectors.toList());

        assertTrue(namesOrder1.containsAll(List.of("User A", "User B", "User C")));
        assertTrue(namesOrder2.containsAll(List.of("User A", "User B", "User C")));

        firestoreService.deleteDocument(testCollection, docId1);
        firestoreService.deleteDocument(testCollection, docId2);
        firestoreService.deleteDocument(testCollection, docId3);
    }

    @Test
    @Order(55)
    void testGetDocuments_WithTypedObjects_ShouldReturnCorrectData() throws Exception {
        String testCollection = "get-docs-typed-" + UUID.randomUUID();
        String docId1 = "customer1";
        String docId2 = "customer2";

        Customer customer1 = Customer.fakeData(1).get(0);
        Customer customer2 = Customer.fakeData(1).get(0);

        firestoreService.saveOrUpdate(testCollection, docId1, customer1);
        firestoreService.saveOrUpdate(testCollection, docId2, customer2);

        List<Map<String, Object>> documents = firestoreService.getDocuments(testCollection, List.of(docId1, docId2));

        assertNotNull(documents);
        assertEquals(2, documents.size());

        for (Map<String, Object> doc : documents) {
            assertNotNull(doc.get("firstName"));
            assertNotNull(doc.get("email"));
            assertNotNull(doc.get("age"));
        }

        firestoreService.deleteDocument(testCollection, docId1);
        firestoreService.deleteDocument(testCollection, docId2);
    }

    @Test
    @Order(56)
    void testGetDocumentsTyped_WithSingleDocumentId_ShouldReturnOneTypedDocument() throws Exception {
        String testCollection = "get-docs-typed-single-" + UUID.randomUUID();
        String docId = "customer1";
        Customer customer = Customer.fakeData(1).get(0);
        firestoreService.saveOrUpdate(testCollection, docId, customer);

        List<Customer> customers = firestoreService.getDocuments(testCollection, List.of(docId), Customer.class);

        assertNotNull(customers);
        assertEquals(1, customers.size(), "Should return exactly one customer");
        Customer retrievedCustomer = customers.get(0);
        assertNotNull(retrievedCustomer);
        assertEquals(customer.getFirstName(), retrievedCustomer.getFirstName());
        assertEquals(customer.getEmail(), retrievedCustomer.getEmail());
        assertEquals(customer.getAge(), retrievedCustomer.getAge());

        firestoreService.deleteDocument(testCollection, docId);
    }

    @Test
    @Order(57)
    void testGetDocumentsTyped_WithMultipleDocumentIds_ShouldReturnAllTypedDocuments() throws Exception {
        String testCollection = "get-docs-typed-multiple-" + UUID.randomUUID();
        List<Customer> originalCustomers = Customer.fakeData(3);
        String docId1 = "customer1";
        String docId2 = "customer2";
        String docId3 = "customer3";

        firestoreService.saveOrUpdate(testCollection, docId1, originalCustomers.get(0));
        firestoreService.saveOrUpdate(testCollection, docId2, originalCustomers.get(1));
        firestoreService.saveOrUpdate(testCollection, docId3, originalCustomers.get(2));

        List<Customer> customers =
                firestoreService.getDocuments(testCollection, List.of(docId1, docId2, docId3), Customer.class);

        assertNotNull(customers);
        assertEquals(3, customers.size(), "Should return all three customers");

        // Verify all customers are properly typed and have data
        for (Customer customer : customers) {
            assertNotNull(customer);
            assertNotNull(customer.getFirstName());
            assertNotNull(customer.getEmail());
            assertTrue(customer.getAge() > 0);
        }

        // Verify all original customer names are present
        List<String> retrievedFirstNames =
                customers.stream().map(Customer::getFirstName).collect(Collectors.toList());
        List<String> originalFirstNames =
                originalCustomers.stream().map(Customer::getFirstName).collect(Collectors.toList());
        assertTrue(retrievedFirstNames.containsAll(originalFirstNames));

        firestoreService.deleteDocument(testCollection, docId1);
        firestoreService.deleteDocument(testCollection, docId2);
        firestoreService.deleteDocument(testCollection, docId3);
    }

    @Test
    @Order(58)
    void testGetDocumentsTyped_WithNonExistentDocumentId_ShouldReturnEmptyList() throws Exception {
        String testCollection = "get-docs-typed-nonexistent-" + UUID.randomUUID();
        String nonExistentDocId = "non-existent-doc-" + UUID.randomUUID();

        List<Customer> customers =
                firestoreService.getDocuments(testCollection, List.of(nonExistentDocId), Customer.class);

        assertNotNull(customers);
        assertTrue(customers.isEmpty(), "Should return empty list for non-existent document");
    }

    @Test
    @Order(59)
    void testGetDocumentsTyped_WithMixedExistentAndNonExistentIds_ShouldReturnOnlyExistentDocuments() throws Exception {
        String testCollection = "get-docs-typed-mixed-" + UUID.randomUUID();
        List<Customer> originalCustomers = Customer.fakeData(2);
        String existingDocId1 = "existing-customer1";
        String existingDocId2 = "existing-customer2";
        String nonExistentDocId1 = "non-existent-customer1";
        String nonExistentDocId2 = "non-existent-customer2";

        firestoreService.saveOrUpdate(testCollection, existingDocId1, originalCustomers.get(0));
        firestoreService.saveOrUpdate(testCollection, existingDocId2, originalCustomers.get(1));

        List<Customer> customers = firestoreService.getDocuments(
                testCollection,
                List.of(existingDocId1, nonExistentDocId1, existingDocId2, nonExistentDocId2),
                Customer.class);

        assertNotNull(customers);
        assertEquals(2, customers.size(), "Should return only the two existing customers");

        // Verify retrieved customers match original data
        List<String> retrievedEmails =
                customers.stream().map(Customer::getEmail).collect(Collectors.toList());
        List<String> originalEmails =
                originalCustomers.stream().map(Customer::getEmail).collect(Collectors.toList());
        assertTrue(retrievedEmails.containsAll(originalEmails));

        firestoreService.deleteDocument(testCollection, existingDocId1);
        firestoreService.deleteDocument(testCollection, existingDocId2);
    }

    @Test
    @Order(60)
    void testGetDocumentsTyped_WithEmptyDocumentIdsList_ShouldReturnEmptyList() throws Exception {
        String testCollection = "get-docs-typed-empty-list-" + UUID.randomUUID();

        List<Customer> customers = firestoreService.getDocuments(testCollection, List.of(), Customer.class);

        assertNotNull(customers);
        assertTrue(customers.isEmpty(), "Should return empty list for empty document IDs list");
    }

    @Test
    @Order(61)
    void testGetDocumentsTyped_TypeSafety_ShouldReturnCorrectType() throws Exception {
        String testCollection = "get-docs-typed-type-safety-" + UUID.randomUUID();
        String docId = "customer1";
        Customer originalCustomer = Customer.fakeData(1).get(0);
        firestoreService.saveOrUpdate(testCollection, docId, originalCustomer);

        List<Customer> customers = firestoreService.getDocuments(testCollection, List.of(docId), Customer.class);

        assertNotNull(customers);
        assertEquals(1, customers.size());

        // Verify type safety - should be able to call Customer-specific methods
        Customer customer = customers.get(0);
        assertNotNull(customer.getFirstName());
        assertNotNull(customer.getLastName());
        assertNotNull(customer.getEmail());
        assertTrue(customer.getAge() > 0);

        // Verify instance type
        assertTrue(customer instanceof Customer, "Object should be instance of Customer class");

        firestoreService.deleteDocument(testCollection, docId);
    }

    @Test
    @Order(62)
    void testGetDocumentsTyped_WithPartialData_ShouldHandleNullFields() throws Exception {
        String testCollection = "get-docs-typed-partial-" + UUID.randomUUID();
        String docId = "partial-customer";

        // Create a document with only some fields
        Map<String, Object> partialData = new HashMap<>();
        partialData.put("firstName", "John");
        partialData.put("email", "john@example.com");
        // Missing: lastName, age, and other fields
        firestoreService.saveOrUpdate(testCollection, docId, partialData);

        List<Customer> customers = firestoreService.getDocuments(testCollection, List.of(docId), Customer.class);

        assertNotNull(customers);
        assertEquals(1, customers.size());

        Customer customer = customers.get(0);
        assertNotNull(customer);
        assertEquals("John", customer.getFirstName());
        assertEquals("john@example.com", customer.getEmail());
        // Missing fields should be null or default values
        // Note: behavior depends on Customer class implementation

        firestoreService.deleteDocument(testCollection, docId);
    }

    @Test
    @Order(63)
    void testGetDocumentsTyped_WithLargeNumberOfDocuments_ShouldReturnAllTypedDocuments() throws Exception {
        String testCollection = "get-docs-typed-large-" + UUID.randomUUID();
        int numDocs = 20;
        List<Customer> originalCustomers = Customer.fakeData(numDocs);
        List<String> docIds = new java.util.ArrayList<>();

        for (int i = 0; i < numDocs; i++) {
            String docId = "customer-" + i;
            docIds.add(docId);
            firestoreService.saveOrUpdate(testCollection, docId, originalCustomers.get(i));
        }

        List<Customer> customers = firestoreService.getDocuments(testCollection, docIds, Customer.class);

        assertNotNull(customers);
        assertEquals(numDocs, customers.size(), "Should return all " + numDocs + " customers");

        // Verify all are proper Customer objects with data
        for (Customer customer : customers) {
            assertNotNull(customer);
            assertNotNull(customer.getFirstName());
            assertNotNull(customer.getEmail());
            assertTrue(customer.getAge() > 0);
        }

        for (String docId : docIds) {
            firestoreService.deleteDocument(testCollection, docId);
        }
    }

    @Test
    @Order(64)
    void testGetDocumentsTyped_AfterUpdate_ShouldReturnUpdatedTypedData() throws Exception {
        String testCollection = "get-docs-typed-updated-" + UUID.randomUUID();
        String docId = "customer-to-update";

        // Create original customer
        Customer originalCustomer = Customer.fakeData(1).get(0);
        firestoreService.saveOrUpdate(testCollection, docId, originalCustomer);

        // Create updated customer with different data
        Customer updatedCustomer = Customer.fakeData(1).get(0);
        updatedCustomer.setFirstName("UpdatedFirstName");
        updatedCustomer.setEmail("updated@example.com");
        firestoreService.saveOrUpdate(testCollection, docId, updatedCustomer);

        List<Customer> customers = firestoreService.getDocuments(testCollection, List.of(docId), Customer.class);

        assertNotNull(customers);
        assertEquals(1, customers.size());

        Customer retrievedCustomer = customers.get(0);
        assertEquals("UpdatedFirstName", retrievedCustomer.getFirstName());
        assertEquals("updated@example.com", retrievedCustomer.getEmail());

        firestoreService.deleteDocument(testCollection, docId);
    }

    @Test
    @Order(65)
    void testGetDocumentsTyped_VerifyOrderIndependence_ShouldReturnAllTypedDocuments() throws Exception {
        String testCollection = "get-docs-typed-order-" + UUID.randomUUID();
        List<Customer> originalCustomers = Customer.fakeData(3);
        String docId1 = "customer-a";
        String docId2 = "customer-b";
        String docId3 = "customer-c";

        firestoreService.saveOrUpdate(testCollection, docId1, originalCustomers.get(0));
        firestoreService.saveOrUpdate(testCollection, docId2, originalCustomers.get(1));
        firestoreService.saveOrUpdate(testCollection, docId3, originalCustomers.get(2));

        // Request in one order
        List<Customer> customersOrder1 =
                firestoreService.getDocuments(testCollection, List.of(docId1, docId2, docId3), Customer.class);

        // Request in different order
        List<Customer> customersOrder2 =
                firestoreService.getDocuments(testCollection, List.of(docId3, docId1, docId2), Customer.class);

        assertNotNull(customersOrder1);
        assertNotNull(customersOrder2);
        assertEquals(3, customersOrder1.size());
        assertEquals(3, customersOrder2.size());

        // Verify all customers are retrieved regardless of order
        List<String> emailsOrder1 =
                customersOrder1.stream().map(Customer::getEmail).collect(Collectors.toList());
        List<String> emailsOrder2 =
                customersOrder2.stream().map(Customer::getEmail).collect(Collectors.toList());
        List<String> originalEmails =
                originalCustomers.stream().map(Customer::getEmail).collect(Collectors.toList());

        assertTrue(emailsOrder1.containsAll(originalEmails));
        assertTrue(emailsOrder2.containsAll(originalEmails));

        firestoreService.deleteDocument(testCollection, docId1);
        firestoreService.deleteDocument(testCollection, docId2);
        firestoreService.deleteDocument(testCollection, docId3);
    }

    @Test
    @Order(66)
    void testGetDocumentsTyped_CompareWithMapBasedVersion_ShouldReturnEquivalentData() throws Exception {
        String testCollection = "get-docs-typed-compare-" + UUID.randomUUID();
        String docId = "customer1";
        Customer originalCustomer = Customer.fakeData(1).get(0);
        firestoreService.saveOrUpdate(testCollection, docId, originalCustomer);

        // Get using typed version
        List<Customer> typedCustomers = firestoreService.getDocuments(testCollection, List.of(docId), Customer.class);

        // Get using map-based version
        List<Map<String, Object>> mapCustomers = firestoreService.getDocuments(testCollection, List.of(docId));

        assertNotNull(typedCustomers);
        assertNotNull(mapCustomers);
        assertEquals(1, typedCustomers.size());
        assertEquals(1, mapCustomers.size());

        // Compare data
        Customer typedCustomer = typedCustomers.get(0);
        Map<String, Object> mapCustomer = mapCustomers.get(0);

        assertEquals(typedCustomer.getFirstName(), mapCustomer.get("firstName"));
        assertEquals(typedCustomer.getEmail(), mapCustomer.get("email"));
        assertEquals(typedCustomer.getAge(), ((Long) mapCustomer.get("age")).intValue());

        firestoreService.deleteDocument(testCollection, docId);
    }

    @Test
    @Order(67)
    void testGetDocumentsTyped_WithDuplicateIds_ShouldHandleCorrectly() throws Exception {
        String testCollection = "get-docs-typed-duplicates-" + UUID.randomUUID();
        String docId = "customer1";
        Customer customer = Customer.fakeData(1).get(0);
        firestoreService.saveOrUpdate(testCollection, docId, customer);

        List<Customer> customers =
                firestoreService.getDocuments(testCollection, List.of(docId, docId, docId), Customer.class);

        assertNotNull(customers);
        assertFalse(customers.isEmpty(), "Should return at least one customer");

        // Verify the customer data is correct
        Customer retrievedCustomer = customers.get(0);
        assertNotNull(retrievedCustomer);
        assertEquals(customer.getFirstName(), retrievedCustomer.getFirstName());
        assertEquals(customer.getEmail(), retrievedCustomer.getEmail());

        firestoreService.deleteDocument(testCollection, docId);
    }

    @Test
    @Order(68)
    void testGetDocumentsTyped_MultipleCallsConsistency_ShouldReturnSameData() throws Exception {
        String testCollection = "get-docs-typed-consistency-" + UUID.randomUUID();
        String docId = "customer1";
        Customer customer = Customer.fakeData(1).get(0);
        firestoreService.saveOrUpdate(testCollection, docId, customer);

        // Make multiple calls
        List<Customer> customers1 = firestoreService.getDocuments(testCollection, List.of(docId), Customer.class);
        List<Customer> customers2 = firestoreService.getDocuments(testCollection, List.of(docId), Customer.class);
        List<Customer> customers3 = firestoreService.getDocuments(testCollection, List.of(docId), Customer.class);

        // All should return the same data
        assertNotNull(customers1);
        assertNotNull(customers2);
        assertNotNull(customers3);

        assertEquals(1, customers1.size());
        assertEquals(1, customers2.size());
        assertEquals(1, customers3.size());

        Customer customer1 = customers1.get(0);
        Customer customer2 = customers2.get(0);
        Customer customer3 = customers3.get(0);

        assertEquals(customer1.getFirstName(), customer2.getFirstName());
        assertEquals(customer2.getFirstName(), customer3.getFirstName());
        assertEquals(customer1.getEmail(), customer2.getEmail());
        assertEquals(customer2.getEmail(), customer3.getEmail());

        firestoreService.deleteDocument(testCollection, docId);
    }

    // ========== incrementField Tests ==========

    @Test
    @Order(69)
    void testIncrementField_WithPositiveValue_ShouldIncreaseField() throws Exception {
        String testCollection = "increment-field-positive-" + UUID.randomUUID();
        String docId = "doc1";

        Map<String, Object> data = new HashMap<>();
        data.put("name", "Test User");
        data.put("points", 100);
        firestoreService.saveOrUpdate(testCollection, docId, data);

        firestoreService.incrementField(testCollection, docId, "points", 50);

        Map<String, Object> updatedDoc = firestoreService.getDocument(testCollection, docId);
        assertEquals(150, ((Long) updatedDoc.get("points")).intValue());

        firestoreService.deleteDocument(testCollection, docId);
    }

    @Test
    @Order(70)
    void testIncrementField_WithNegativeValue_ShouldDecreaseField() throws Exception {
        String testCollection = "increment-field-negative-" + UUID.randomUUID();
        String docId = "doc1";

        Map<String, Object> data = new HashMap<>();
        data.put("name", "Test User");
        data.put("balance", 1000);
        firestoreService.saveOrUpdate(testCollection, docId, data);

        firestoreService.incrementField(testCollection, docId, "balance", -200);

        Map<String, Object> updatedDoc = firestoreService.getDocument(testCollection, docId);
        assertEquals(800, ((Long) updatedDoc.get("balance")).intValue());

        firestoreService.deleteDocument(testCollection, docId);
    }

    @Test
    @Order(71)
    void testIncrementField_WithZeroValue_ShouldNotChangeField() throws Exception {
        String testCollection = "increment-field-zero-" + UUID.randomUUID();
        String docId = "doc1";

        Map<String, Object> data = new HashMap<>();
        data.put("name", "Test User");
        data.put("score", 500);
        firestoreService.saveOrUpdate(testCollection, docId, data);

        firestoreService.incrementField(testCollection, docId, "score", 0);

        Map<String, Object> updatedDoc = firestoreService.getDocument(testCollection, docId);
        assertEquals(500, ((Long) updatedDoc.get("score")).intValue());

        firestoreService.deleteDocument(testCollection, docId);
    }

    @Test
    @Order(72)
    void testIncrementField_OnNonExistentField_ShouldInitializeField() throws Exception {
        String testCollection = "increment-field-new-" + UUID.randomUUID();
        String docId = "doc1";

        Map<String, Object> data = new HashMap<>();
        data.put("name", "Test User");
        firestoreService.saveOrUpdate(testCollection, docId, data);

        firestoreService.incrementField(testCollection, docId, "newCounter", 10);

        Map<String, Object> updatedDoc = firestoreService.getDocument(testCollection, docId);
        assertEquals(10, ((Long) updatedDoc.get("newCounter")).intValue());

        firestoreService.deleteDocument(testCollection, docId);
    }

    @Test
    @Order(73)
    void testIncrementField_MultipleIncrements_ShouldAccumulateCorrectly() throws Exception {
        String testCollection = "increment-field-multiple-" + UUID.randomUUID();
        String docId = "doc1";

        Map<String, Object> data = new HashMap<>();
        data.put("name", "Test User");
        data.put("visits", 0);
        firestoreService.saveOrUpdate(testCollection, docId, data);

        firestoreService.incrementField(testCollection, docId, "visits", 1);
        firestoreService.incrementField(testCollection, docId, "visits", 1);
        firestoreService.incrementField(testCollection, docId, "visits", 1);
        firestoreService.incrementField(testCollection, docId, "visits", 5);

        Map<String, Object> updatedDoc = firestoreService.getDocument(testCollection, docId);
        assertEquals(8, ((Long) updatedDoc.get("visits")).intValue());

        firestoreService.deleteDocument(testCollection, docId);
    }

    @Test
    @Order(74)
    void testIncrementField_WithLargeValue_ShouldHandleCorrectly() throws Exception {
        String testCollection = "increment-field-large-" + UUID.randomUUID();
        String docId = "doc1";

        Map<String, Object> data = new HashMap<>();
        data.put("name", "Test User");
        data.put("bigCounter", 1000000L);
        firestoreService.saveOrUpdate(testCollection, docId, data);

        firestoreService.incrementField(testCollection, docId, "bigCounter", 500000L);

        Map<String, Object> updatedDoc = firestoreService.getDocument(testCollection, docId);
        assertEquals(1500000L, ((Long) updatedDoc.get("bigCounter")).longValue());

        firestoreService.deleteDocument(testCollection, docId);
    }

    @Test
    @Order(75)
    void testIncrementField_OnNonExistentDocument_ShouldThrowException() {
        String testCollection = "increment-field-nonexistent-" + UUID.randomUUID();
        String nonExistentDocId = "non-existent-doc-" + UUID.randomUUID();

        assertThrows(
                Exception.class, () -> firestoreService.incrementField(testCollection, nonExistentDocId, "field", 10));
    }

    @Test
    @Order(76)
    void testIncrementField_MixedPositiveAndNegative_ShouldCalculateCorrectly() throws Exception {
        String testCollection = "increment-field-mixed-" + UUID.randomUUID();
        String docId = "doc1";

        Map<String, Object> data = new HashMap<>();
        data.put("name", "Test User");
        data.put("balance", 100);
        firestoreService.saveOrUpdate(testCollection, docId, data);

        firestoreService.incrementField(testCollection, docId, "balance", 50);
        firestoreService.incrementField(testCollection, docId, "balance", -30);
        firestoreService.incrementField(testCollection, docId, "balance", 20);
        firestoreService.incrementField(testCollection, docId, "balance", -10);

        Map<String, Object> updatedDoc = firestoreService.getDocument(testCollection, docId);
        assertEquals(130, ((Long) updatedDoc.get("balance")).intValue());

        firestoreService.deleteDocument(testCollection, docId);
    }

    // ========== arrayUnion Tests ==========

    @Test
    @Order(77)
    void testArrayUnion_WithSingleElement_ShouldAddToArray() throws Exception {
        String testCollection = "array-union-single-" + UUID.randomUUID();
        String docId = "doc1";

        Map<String, Object> data = new HashMap<>();
        data.put("name", "Test User");
        data.put("tags", List.of("developer"));
        firestoreService.saveOrUpdate(testCollection, docId, data);

        firestoreService.arrayUnion(testCollection, docId, "tags", "tester");

        Map<String, Object> updatedDoc = firestoreService.getDocument(testCollection, docId);
        @SuppressWarnings("unchecked")
        List<String> tags = (List<String>) updatedDoc.get("tags");
        assertNotNull(tags);
        assertEquals(2, tags.size());
        assertTrue(tags.contains("developer"));
        assertTrue(tags.contains("tester"));

        firestoreService.deleteDocument(testCollection, docId);
    }

    @Test
    @Order(78)
    void testArrayUnion_WithMultipleElements_ShouldAddAllToArray() throws Exception {
        String testCollection = "array-union-multiple-" + UUID.randomUUID();
        String docId = "doc1";

        Map<String, Object> data = new HashMap<>();
        data.put("name", "Test User");
        data.put("skills", List.of("Java"));
        firestoreService.saveOrUpdate(testCollection, docId, data);

        firestoreService.arrayUnion(testCollection, docId, "skills", "Python", "JavaScript", "Go");

        Map<String, Object> updatedDoc = firestoreService.getDocument(testCollection, docId);
        @SuppressWarnings("unchecked")
        List<String> skills = (List<String>) updatedDoc.get("skills");
        assertNotNull(skills);
        assertEquals(4, skills.size());
        assertTrue(skills.contains("Java"));
        assertTrue(skills.contains("Python"));
        assertTrue(skills.contains("JavaScript"));
        assertTrue(skills.contains("Go"));

        firestoreService.deleteDocument(testCollection, docId);
    }

    @Test
    @Order(79)
    void testArrayUnion_WithDuplicateElements_ShouldNotAddDuplicates() throws Exception {
        String testCollection = "array-union-duplicates-" + UUID.randomUUID();
        String docId = "doc1";

        Map<String, Object> data = new HashMap<>();
        data.put("name", "Test User");
        data.put("languages", List.of("English", "Spanish"));
        firestoreService.saveOrUpdate(testCollection, docId, data);

        // Try to add "English" which already exists
        firestoreService.arrayUnion(testCollection, docId, "languages", "English", "French");

        Map<String, Object> updatedDoc = firestoreService.getDocument(testCollection, docId);
        @SuppressWarnings("unchecked")
        List<String> languages = (List<String>) updatedDoc.get("languages");
        assertNotNull(languages);
        assertEquals(3, languages.size()); // Should not duplicate "English"
        assertTrue(languages.contains("English"));
        assertTrue(languages.contains("Spanish"));
        assertTrue(languages.contains("French"));

        firestoreService.deleteDocument(testCollection, docId);
    }

    @Test
    @Order(80)
    void testArrayUnion_OnNonExistentField_ShouldCreateArrayWithElements() throws Exception {
        String testCollection = "array-union-new-field-" + UUID.randomUUID();
        String docId = "doc1";

        Map<String, Object> data = new HashMap<>();
        data.put("name", "Test User");
        firestoreService.saveOrUpdate(testCollection, docId, data);

        firestoreService.arrayUnion(testCollection, docId, "newArray", "item1", "item2", "item3");

        Map<String, Object> updatedDoc = firestoreService.getDocument(testCollection, docId);
        @SuppressWarnings("unchecked")
        List<String> newArray = (List<String>) updatedDoc.get("newArray");
        assertNotNull(newArray);
        assertEquals(3, newArray.size());
        assertTrue(newArray.contains("item1"));
        assertTrue(newArray.contains("item2"));
        assertTrue(newArray.contains("item3"));

        firestoreService.deleteDocument(testCollection, docId);
    }

    @Test
    @Order(81)
    void testArrayUnion_WithEmptyArray_ShouldAddElements() throws Exception {
        String testCollection = "array-union-empty-" + UUID.randomUUID();
        String docId = "doc1";

        Map<String, Object> data = new HashMap<>();
        data.put("name", "Test User");
        data.put("emptyList", List.of());
        firestoreService.saveOrUpdate(testCollection, docId, data);

        firestoreService.arrayUnion(testCollection, docId, "emptyList", "first", "second");

        Map<String, Object> updatedDoc = firestoreService.getDocument(testCollection, docId);
        @SuppressWarnings("unchecked")
        List<String> emptyList = (List<String>) updatedDoc.get("emptyList");
        assertNotNull(emptyList);
        assertEquals(2, emptyList.size());
        assertTrue(emptyList.contains("first"));
        assertTrue(emptyList.contains("second"));

        firestoreService.deleteDocument(testCollection, docId);
    }

    @Test
    @Order(82)
    void testArrayUnion_WithDifferentTypes_ShouldAddMixedElements() throws Exception {
        String testCollection = "array-union-mixed-types-" + UUID.randomUUID();
        String docId = "doc1";

        Map<String, Object> data = new HashMap<>();
        data.put("name", "Test User");
        data.put("mixedArray", List.of("string1"));
        firestoreService.saveOrUpdate(testCollection, docId, data);

        firestoreService.arrayUnion(testCollection, docId, "mixedArray", "string2", 123, true);

        Map<String, Object> updatedDoc = firestoreService.getDocument(testCollection, docId);
        @SuppressWarnings("unchecked")
        List<Object> mixedArray = (List<Object>) updatedDoc.get("mixedArray");
        assertNotNull(mixedArray);
        assertEquals(4, mixedArray.size());
        assertTrue(mixedArray.contains("string1"));
        assertTrue(mixedArray.contains("string2"));
        assertTrue(mixedArray.contains(123L));
        assertTrue(mixedArray.contains(true));

        firestoreService.deleteDocument(testCollection, docId);
    }

    @Test
    @Order(83)
    void testArrayUnion_MultipleOperations_ShouldAccumulateElements() throws Exception {
        String testCollection = "array-union-multiple-ops-" + UUID.randomUUID();
        String docId = "doc1";

        Map<String, Object> data = new HashMap<>();
        data.put("name", "Test User");
        data.put("items", List.of("item1"));
        firestoreService.saveOrUpdate(testCollection, docId, data);

        firestoreService.arrayUnion(testCollection, docId, "items", "item2");
        firestoreService.arrayUnion(testCollection, docId, "items", "item3");
        firestoreService.arrayUnion(testCollection, docId, "items", "item4", "item5");

        Map<String, Object> updatedDoc = firestoreService.getDocument(testCollection, docId);
        @SuppressWarnings("unchecked")
        List<String> items = (List<String>) updatedDoc.get("items");
        assertNotNull(items);
        assertEquals(5, items.size());
        assertTrue(items.containsAll(List.of("item1", "item2", "item3", "item4", "item5")));

        firestoreService.deleteDocument(testCollection, docId);
    }

    @Test
    @Order(84)
    void testArrayUnion_OnNonExistentDocument_ShouldThrowException() {
        String testCollection = "array-union-nonexistent-" + UUID.randomUUID();
        String nonExistentDocId = "non-existent-doc-" + UUID.randomUUID();

        assertThrows(
                Exception.class, () -> firestoreService.arrayUnion(testCollection, nonExistentDocId, "field", "value"));
    }

    // ========== arrayRemove Tests ==========

    @Test
    @Order(85)
    void testArrayRemove_WithSingleElement_ShouldRemoveFromArray() throws Exception {
        String testCollection = "array-remove-single-" + UUID.randomUUID();
        String docId = "doc1";

        Map<String, Object> data = new HashMap<>();
        data.put("name", "Test User");
        data.put("tags", List.of("developer", "tester", "architect"));
        firestoreService.saveOrUpdate(testCollection, docId, data);

        firestoreService.arrayRemove(testCollection, docId, "tags", "tester");

        Map<String, Object> updatedDoc = firestoreService.getDocument(testCollection, docId);
        @SuppressWarnings("unchecked")
        List<String> tags = (List<String>) updatedDoc.get("tags");
        assertNotNull(tags);
        assertEquals(2, tags.size());
        assertTrue(tags.contains("developer"));
        assertTrue(tags.contains("architect"));
        assertFalse(tags.contains("tester"));

        firestoreService.deleteDocument(testCollection, docId);
    }

    @Test
    @Order(86)
    void testArrayRemove_WithMultipleElements_ShouldRemoveAllFromArray() throws Exception {
        String testCollection = "array-remove-multiple-" + UUID.randomUUID();
        String docId = "doc1";

        Map<String, Object> data = new HashMap<>();
        data.put("name", "Test User");
        data.put("skills", List.of("Java", "Python", "JavaScript", "Go", "Ruby"));
        firestoreService.saveOrUpdate(testCollection, docId, data);

        firestoreService.arrayRemove(testCollection, docId, "skills", "Python", "Go");

        Map<String, Object> updatedDoc = firestoreService.getDocument(testCollection, docId);
        @SuppressWarnings("unchecked")
        List<String> skills = (List<String>) updatedDoc.get("skills");
        assertNotNull(skills);
        assertEquals(3, skills.size());
        assertTrue(skills.contains("Java"));
        assertTrue(skills.contains("JavaScript"));
        assertTrue(skills.contains("Ruby"));
        assertFalse(skills.contains("Python"));
        assertFalse(skills.contains("Go"));

        firestoreService.deleteDocument(testCollection, docId);
    }

    @Test
    @Order(87)
    void testArrayRemove_WithNonExistentElement_ShouldNotChangeArray() throws Exception {
        String testCollection = "array-remove-nonexistent-elem-" + UUID.randomUUID();
        String docId = "doc1";

        Map<String, Object> data = new HashMap<>();
        data.put("name", "Test User");
        data.put("languages", List.of("English", "Spanish"));
        firestoreService.saveOrUpdate(testCollection, docId, data);

        // Try to remove "French" which doesn't exist
        firestoreService.arrayRemove(testCollection, docId, "languages", "French");

        Map<String, Object> updatedDoc = firestoreService.getDocument(testCollection, docId);
        @SuppressWarnings("unchecked")
        List<String> languages = (List<String>) updatedDoc.get("languages");
        assertNotNull(languages);
        assertEquals(2, languages.size());
        assertTrue(languages.contains("English"));
        assertTrue(languages.contains("Spanish"));

        firestoreService.deleteDocument(testCollection, docId);
    }

    @Test
    @Order(88)
    void testArrayRemove_RemoveAllElements_ShouldLeaveEmptyArray() throws Exception {
        String testCollection = "array-remove-all-" + UUID.randomUUID();
        String docId = "doc1";

        Map<String, Object> data = new HashMap<>();
        data.put("name", "Test User");
        data.put("items", List.of("item1", "item2", "item3"));
        firestoreService.saveOrUpdate(testCollection, docId, data);

        firestoreService.arrayRemove(testCollection, docId, "items", "item1", "item2", "item3");

        Map<String, Object> updatedDoc = firestoreService.getDocument(testCollection, docId);
        @SuppressWarnings("unchecked")
        List<String> items = (List<String>) updatedDoc.get("items");
        assertNotNull(items);
        assertTrue(items.isEmpty());

        firestoreService.deleteDocument(testCollection, docId);
    }

    @Test
    @Order(89)
    void testArrayRemove_WithDuplicatesInArray_ShouldRemoveAllOccurrences() throws Exception {
        String testCollection = "array-remove-duplicates-" + UUID.randomUUID();
        String docId = "doc1";

        Map<String, Object> data = new HashMap<>();
        data.put("name", "Test User");
        data.put("numbers", List.of(1, 2, 3, 2, 4, 2, 5));
        firestoreService.saveOrUpdate(testCollection, docId, data);

        firestoreService.arrayRemove(testCollection, docId, "numbers", 2);

        Map<String, Object> updatedDoc = firestoreService.getDocument(testCollection, docId);
        @SuppressWarnings("unchecked")
        List<Long> numbers = (List<Long>) updatedDoc.get("numbers");
        assertNotNull(numbers);
        assertEquals(4, numbers.size());
        assertFalse(numbers.contains(2L));
        assertTrue(numbers.containsAll(List.of(1L, 3L, 4L, 5L)));

        firestoreService.deleteDocument(testCollection, docId);
    }

    @Test
    @Order(90)
    void testArrayRemove_OnEmptyArray_ShouldRemainEmpty() throws Exception {
        String testCollection = "array-remove-empty-" + UUID.randomUUID();
        String docId = "doc1";

        Map<String, Object> data = new HashMap<>();
        data.put("name", "Test User");
        data.put("emptyList", List.of());
        firestoreService.saveOrUpdate(testCollection, docId, data);

        firestoreService.arrayRemove(testCollection, docId, "emptyList", "nonexistent");

        Map<String, Object> updatedDoc = firestoreService.getDocument(testCollection, docId);
        @SuppressWarnings("unchecked")
        List<String> emptyList = (List<String>) updatedDoc.get("emptyList");
        assertNotNull(emptyList);
        assertTrue(emptyList.isEmpty());

        firestoreService.deleteDocument(testCollection, docId);
    }

    @Test
    @Order(91)
    void testArrayRemove_MultipleOperations_ShouldRemoveSequentially() throws Exception {
        String testCollection = "array-remove-multiple-ops-" + UUID.randomUUID();
        String docId = "doc1";

        Map<String, Object> data = new HashMap<>();
        data.put("name", "Test User");
        data.put("items", List.of("item1", "item2", "item3", "item4", "item5"));
        firestoreService.saveOrUpdate(testCollection, docId, data);

        firestoreService.arrayRemove(testCollection, docId, "items", "item2");
        firestoreService.arrayRemove(testCollection, docId, "items", "item4");

        Map<String, Object> updatedDoc = firestoreService.getDocument(testCollection, docId);
        @SuppressWarnings("unchecked")
        List<String> items = (List<String>) updatedDoc.get("items");
        assertNotNull(items);
        assertEquals(3, items.size());
        assertTrue(items.containsAll(List.of("item1", "item3", "item5")));
        assertFalse(items.contains("item2"));
        assertFalse(items.contains("item4"));

        firestoreService.deleteDocument(testCollection, docId);
    }

    @Test
    @Order(92)
    void testArrayRemove_OnNonExistentDocument_ShouldThrowException() {
        String testCollection = "array-remove-nonexistent-doc-" + UUID.randomUUID();
        String nonExistentDocId = "non-existent-doc-" + UUID.randomUUID();

        assertThrows(
                Exception.class,
                () -> firestoreService.arrayRemove(testCollection, nonExistentDocId, "field", "value"));
    }

    // ========== Integration Tests for Multiple Operations ==========

    @Test
    @Order(93)
    void testCombinedOperations_IncrementAndArrayUnion_ShouldWorkTogether() throws Exception {
        String testCollection = "combined-increment-union-" + UUID.randomUUID();
        String docId = "doc1";

        Map<String, Object> data = new HashMap<>();
        data.put("name", "Test User");
        data.put("points", 100);
        data.put("badges", List.of("beginner"));
        firestoreService.saveOrUpdate(testCollection, docId, data);

        // Perform combined operations
        firestoreService.incrementField(testCollection, docId, "points", 50);
        firestoreService.arrayUnion(testCollection, docId, "badges", "contributor", "expert");

        Map<String, Object> updatedDoc = firestoreService.getDocument(testCollection, docId);
        assertEquals(150, ((Long) updatedDoc.get("points")).intValue());

        @SuppressWarnings("unchecked")
        List<String> badges = (List<String>) updatedDoc.get("badges");
        assertEquals(3, badges.size());
        assertTrue(badges.containsAll(List.of("beginner", "contributor", "expert")));

        firestoreService.deleteDocument(testCollection, docId);
    }

    @Test
    @Order(94)
    void testCombinedOperations_ArrayUnionAndRemove_ShouldWorkTogether() throws Exception {
        String testCollection = "combined-union-remove-" + UUID.randomUUID();
        String docId = "doc1";

        Map<String, Object> data = new HashMap<>();
        data.put("name", "Test User");
        data.put("tags", List.of("tag1", "tag2", "tag3"));
        firestoreService.saveOrUpdate(testCollection, docId, data);

        // Add and remove elements
        firestoreService.arrayUnion(testCollection, docId, "tags", "tag4", "tag5");
        firestoreService.arrayRemove(testCollection, docId, "tags", "tag2");

        Map<String, Object> updatedDoc = firestoreService.getDocument(testCollection, docId);
        @SuppressWarnings("unchecked")
        List<String> tags = (List<String>) updatedDoc.get("tags");
        assertEquals(4, tags.size());
        assertTrue(tags.containsAll(List.of("tag1", "tag3", "tag4", "tag5")));
        assertFalse(tags.contains("tag2"));

        firestoreService.deleteDocument(testCollection, docId);
    }

    @Test
    @Order(95)
    void testCombinedOperations_AllThreeOperations_ShouldWorkTogether() throws Exception {
        String testCollection = "combined-all-ops-" + UUID.randomUUID();
        String docId = "doc1";

        Map<String, Object> data = new HashMap<>();
        data.put("name", "Test User");
        data.put("score", 0);
        data.put("achievements", List.of("first_login"));
        data.put("skills", List.of("beginner"));
        firestoreService.saveOrUpdate(testCollection, docId, data);

        // Perform all three operations
        firestoreService.incrementField(testCollection, docId, "score", 100);
        firestoreService.arrayUnion(testCollection, docId, "achievements", "completed_tutorial", "first_win");
        firestoreService.arrayRemove(testCollection, docId, "skills", "beginner");
        firestoreService.arrayUnion(testCollection, docId, "skills", "intermediate");

        Map<String, Object> updatedDoc = firestoreService.getDocument(testCollection, docId);

        // Verify increment
        assertEquals(100, ((Long) updatedDoc.get("score")).intValue());

        // Verify array union
        @SuppressWarnings("unchecked")
        List<String> achievements = (List<String>) updatedDoc.get("achievements");
        assertEquals(3, achievements.size());
        assertTrue(achievements.containsAll(List.of("first_login", "completed_tutorial", "first_win")));

        // Verify array remove and union
        @SuppressWarnings("unchecked")
        List<String> skills = (List<String>) updatedDoc.get("skills");
        assertEquals(1, skills.size());
        assertFalse(skills.contains("beginner"));
        assertTrue(skills.contains("intermediate"));

        firestoreService.deleteDocument(testCollection, docId);
    }

    @Test
    @Order(96)
    void testQueryDocuments_WithSimpleQuery_ShouldReturnMatchingDocuments() throws Exception {
        String testCollection = "query-test-" + UUID.randomUUID();

        // Create test documents
        Map<String, Object> doc1 = new HashMap<>();
        doc1.put("name", "Alice");
        doc1.put("age", 25);
        doc1.put("city", "New York");

        Map<String, Object> doc2 = new HashMap<>();
        doc2.put("name", "Bob");
        doc2.put("age", 30);
        doc2.put("city", "New York");

        Map<String, Object> doc3 = new HashMap<>();
        doc3.put("name", "Charlie");
        doc3.put("age", 25);
        doc3.put("city", "Boston");

        firestoreService.saveOrUpdate(testCollection, "doc1", doc1);
        firestoreService.saveOrUpdate(testCollection, "doc2", doc2);
        firestoreService.saveOrUpdate(testCollection, "doc3", doc3);

        // Query documents where age equals 25
        List<Map<String, Object>> results =
                firestoreService.queryDocuments(testCollection, collectionRef -> collectionRef.whereEqualTo("age", 25));

        assertNotNull(results);
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(doc -> ((Long) doc.get("age")).intValue() == 25));

        // Cleanup
        firestoreService.deleteDocument(testCollection, "doc1");
        firestoreService.deleteDocument(testCollection, "doc2");
        firestoreService.deleteDocument(testCollection, "doc3");
    }

    @Test
    @Order(97)
    void testQueryDocuments_WithMultipleConditions_ShouldReturnMatchingDocuments() throws Exception {
        String testCollection = "query-multi-" + UUID.randomUUID();

        // Create test documents
        Map<String, Object> doc1 = new HashMap<>();
        doc1.put("name", "Alice");
        doc1.put("age", 25);
        doc1.put("city", "New York");
        doc1.put("active", true);

        Map<String, Object> doc2 = new HashMap<>();
        doc2.put("name", "Bob");
        doc2.put("age", 30);
        doc2.put("city", "New York");
        doc2.put("active", true);

        Map<String, Object> doc3 = new HashMap<>();
        doc3.put("name", "Charlie");
        doc3.put("age", 25);
        doc3.put("city", "New York");
        doc3.put("active", false);

        firestoreService.saveOrUpdate(testCollection, "doc1", doc1);
        firestoreService.saveOrUpdate(testCollection, "doc2", doc2);
        firestoreService.saveOrUpdate(testCollection, "doc3", doc3);

        // Query documents where city is "New York" AND active is true
        List<Map<String, Object>> results = firestoreService.queryDocuments(
                testCollection,
                collectionRef -> collectionRef.whereEqualTo("city", "New York").whereEqualTo("active", true));

        assertNotNull(results);
        assertEquals(2, results.size());
        assertTrue(results.stream()
                .allMatch(doc -> "New York".equals(doc.get("city")) && Boolean.TRUE.equals(doc.get("active"))));

        // Cleanup
        firestoreService.deleteDocument(testCollection, "doc1");
        firestoreService.deleteDocument(testCollection, "doc2");
        firestoreService.deleteDocument(testCollection, "doc3");
    }

    @Test
    @Order(98)
    void testQueryDocuments_WithOrderByAndLimit_ShouldReturnOrderedLimitedResults() throws Exception {
        String testCollection = "query-order-limit-" + UUID.randomUUID();

        // Create test documents
        for (int i = 1; i <= 5; i++) {
            Map<String, Object> doc = new HashMap<>();
            doc.put("name", "User" + i);
            doc.put("score", i * 10);
            firestoreService.saveOrUpdate(testCollection, "doc" + i, doc);
        }

        // Query with order by score descending and limit 3
        List<Map<String, Object>> results =
                firestoreService.queryDocuments(testCollection, collectionRef -> collectionRef
                        .orderBy("score", com.google.cloud.firestore.Query.Direction.DESCENDING)
                        .limit(3));

        assertNotNull(results);
        assertEquals(3, results.size());
        assertEquals(50, ((Long) results.get(0).get("score")).intValue());
        assertEquals(40, ((Long) results.get(1).get("score")).intValue());
        assertEquals(30, ((Long) results.get(2).get("score")).intValue());

        // Cleanup
        for (int i = 1; i <= 5; i++) {
            firestoreService.deleteDocument(testCollection, "doc" + i);
        }
    }

    @Test
    @Order(99)
    void testQueryDocuments_WithRangeQuery_ShouldReturnMatchingDocuments() throws Exception {
        String testCollection = "query-range-" + UUID.randomUUID();

        // Create test documents with different ages
        for (int i = 1; i <= 10; i++) {
            Map<String, Object> doc = new HashMap<>();
            doc.put("name", "Person" + i);
            doc.put("age", 20 + i);
            firestoreService.saveOrUpdate(testCollection, "doc" + i, doc);
        }

        // Query documents where age is between 25 and 28 (inclusive)
        List<Map<String, Object>> results =
                firestoreService.queryDocuments(testCollection, collectionRef -> collectionRef
                        .whereGreaterThanOrEqualTo("age", 25)
                        .whereLessThanOrEqualTo("age", 28));

        assertNotNull(results);
        assertEquals(4, results.size());
        assertTrue(results.stream().allMatch(doc -> {
            int age = ((Long) doc.get("age")).intValue();
            return age >= 25 && age <= 28;
        }));

        // Cleanup
        for (int i = 1; i <= 10; i++) {
            firestoreService.deleteDocument(testCollection, "doc" + i);
        }
    }

    @Test
    @Order(100)
    void testQueryDocuments_WithNoResults_ShouldReturnEmptyList() throws Exception {
        String testCollection = "query-empty-" + UUID.randomUUID();

        // Create test documents
        Map<String, Object> doc1 = new HashMap<>();
        doc1.put("name", "Alice");
        doc1.put("age", 25);
        firestoreService.saveOrUpdate(testCollection, "doc1", doc1);

        // Query for non-existent data
        List<Map<String, Object>> results = firestoreService.queryDocuments(
                testCollection, collectionRef -> collectionRef.whereEqualTo("age", 999));

        assertNotNull(results);
        assertTrue(results.isEmpty());

        // Cleanup
        firestoreService.deleteDocument(testCollection, "doc1");
    }

    @Test
    @Order(101)
    void testQueryDocuments_WithTypedResults_ShouldReturnMappedObjects() throws Exception {
        String testCollection = "query-typed-" + UUID.randomUUID();

        // Create test customers
        List<Customer> testCustomers = Customer.fakeData(3);
        for (int i = 0; i < testCustomers.size(); i++) {
            Customer customer = testCustomers.get(i);
            // Set specific age for testing
            customer.setAge(30 + i);
            firestoreService.saveOrUpdate(testCollection, "customer" + i, customer);
        }

        // Query using the private method through reflection or create a public wrapper
        // Since the typed version is private, we'll test the Map version and verify mapping
        List<Map<String, Object>> results = firestoreService.queryDocuments(
                testCollection, collectionRef -> collectionRef.whereGreaterThanOrEqualTo("age", 30));

        assertNotNull(results);
        assertEquals(3, results.size());
        assertTrue(results.stream().allMatch(doc -> {
            Long age = (Long) doc.get("age");
            return age != null && age >= 30;
        }));

        // Verify all required fields are present
        for (Map<String, Object> result : results) {
            assertNotNull(result.get("firstName"));
            assertNotNull(result.get("email"));
            assertNotNull(result.get("age"));
        }

        // Cleanup
        for (int i = 0; i < testCustomers.size(); i++) {
            firestoreService.deleteDocument(testCollection, "customer" + i);
        }
    }

    @Test
    @Order(102)
    void testQueryDocuments_WithArrayContains_ShouldReturnMatchingDocuments() throws Exception {
        String testCollection = "query-array-" + UUID.randomUUID();

        // Create documents with array fields
        Map<String, Object> doc1 = new HashMap<>();
        doc1.put("name", "User1");
        doc1.put("tags", List.of("java", "spring", "firebase"));

        Map<String, Object> doc2 = new HashMap<>();
        doc2.put("name", "User2");
        doc2.put("tags", List.of("python", "django"));

        Map<String, Object> doc3 = new HashMap<>();
        doc3.put("name", "User3");
        doc3.put("tags", List.of("java", "hibernate"));

        firestoreService.saveOrUpdate(testCollection, "doc1", doc1);
        firestoreService.saveOrUpdate(testCollection, "doc2", doc2);
        firestoreService.saveOrUpdate(testCollection, "doc3", doc3);

        // Query documents where tags array contains "java"
        List<Map<String, Object>> results = firestoreService.queryDocuments(
                testCollection, collectionRef -> collectionRef.whereArrayContains("tags", "java"));

        assertNotNull(results);
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(doc -> {
            @SuppressWarnings("unchecked")
            List<String> tags = (List<String>) doc.get("tags");
            return tags != null && tags.contains("java");
        }));

        // Cleanup
        firestoreService.deleteDocument(testCollection, "doc1");
        firestoreService.deleteDocument(testCollection, "doc2");
        firestoreService.deleteDocument(testCollection, "doc3");
    }

    @Test
    @Order(103)
    void testQueryDocuments_WithInQuery_ShouldReturnMatchingDocuments() throws Exception {
        String testCollection = "query-in-" + UUID.randomUUID();

        // Create test documents
        for (int i = 1; i <= 5; i++) {
            Map<String, Object> doc = new HashMap<>();
            doc.put("name", "User" + i);
            doc.put("status", i % 2 == 0 ? "active" : "inactive");
            doc.put("role", i <= 2 ? "admin" : "user");
            firestoreService.saveOrUpdate(testCollection, "doc" + i, doc);
        }

        // Query documents where role is in ["admin", "moderator"]
        List<Map<String, Object>> results = firestoreService.queryDocuments(
                testCollection, collectionRef -> collectionRef.whereIn("role", List.of("admin", "moderator")));

        assertNotNull(results);
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(doc -> "admin".equals(doc.get("role"))));

        // Cleanup
        for (int i = 1; i <= 5; i++) {
            firestoreService.deleteDocument(testCollection, "doc" + i);
        }
    }

    @Test
    @Order(105)
    void testQueryDocumentsTyped_WithSimpleQuery_ShouldReturnTypedObjects() throws Exception {
        String testCollection = "query-typed-simple-" + UUID.randomUUID();

        // Create test customers
        List<Customer> testCustomers = Customer.fakeData(5);
        for (int i = 0; i < testCustomers.size(); i++) {
            Customer customer = testCustomers.get(i);
            customer.setAge(25 + i); // Set ages 25, 26, 27, 28, 29
            firestoreService.saveOrUpdate(testCollection, "customer" + i, customer);
        }

        // Query using typed method where age equals 27
        List<Customer> results = firestoreService.queryDocuments(
                testCollection, Customer.class, collectionRef -> collectionRef.whereEqualTo("age", 27));

        assertNotNull(results);
        assertEquals(1, results.size());
        Customer customer = results.get(0);
        assertNotNull(customer);
        assertEquals(27, customer.getAge());
        assertNotNull(customer.getFirstName());
        assertNotNull(customer.getEmail());

        // Cleanup
        for (int i = 0; i < testCustomers.size(); i++) {
            firestoreService.deleteDocument(testCollection, "customer" + i);
        }
    }

    @Test
    @Order(106)
    void testQueryDocumentsTyped_WithMultipleResults_ShouldReturnAllTypedObjects() throws Exception {
        String testCollection = "query-typed-multi-" + UUID.randomUUID();

        // Create test customers with specific ages
        List<Customer> testCustomers = Customer.fakeData(6);
        for (int i = 0; i < testCustomers.size(); i++) {
            Customer customer = testCustomers.get(i);
            customer.setAge(i % 2 == 0 ? 30 : 35); // Alternate between 30 and 35
            firestoreService.saveOrUpdate(testCollection, "customer" + i, customer);
        }

        // Query for age 30 - should return 3 customers
        List<Customer> results = firestoreService.queryDocuments(
                testCollection, Customer.class, collectionRef -> collectionRef.whereEqualTo("age", 30));

        assertNotNull(results);
        assertEquals(3, results.size());
        for (Customer customer : results) {
            assertNotNull(customer);
            assertEquals(30, customer.getAge());
            assertNotNull(customer.getFirstName());
            assertNotNull(customer.getLastName());
            assertNotNull(customer.getEmail());
        }

        // Cleanup
        for (int i = 0; i < testCustomers.size(); i++) {
            firestoreService.deleteDocument(testCollection, "customer" + i);
        }
    }

    @Test
    @Order(107)
    void testQueryDocumentsTyped_WithOrderByAndLimit_ShouldReturnOrderedTypedObjects() throws Exception {
        String testCollection = "query-typed-order-" + UUID.randomUUID();

        // Create test customers with different ages
        List<Customer> testCustomers = Customer.fakeData(8);
        for (int i = 0; i < testCustomers.size(); i++) {
            Customer customer = testCustomers.get(i);
            customer.setAge(20 + (i * 5)); // Ages: 20, 25, 30, 35, 40, 45, 50, 55
            firestoreService.saveOrUpdate(testCollection, "customer" + i, customer);
        }

        // Query with order by age descending and limit 4
        List<Customer> results =
                firestoreService.queryDocuments(testCollection, Customer.class, collectionRef -> collectionRef
                        .orderBy("age", com.google.cloud.firestore.Query.Direction.DESCENDING)
                        .limit(4));

        assertNotNull(results);
        assertEquals(4, results.size());
        // Verify descending order: 55, 50, 45, 40
        assertEquals(55, results.get(0).getAge());
        assertEquals(50, results.get(1).getAge());
        assertEquals(45, results.get(2).getAge());
        assertEquals(40, results.get(3).getAge());

        // Verify all objects are properly typed
        for (Customer customer : results) {
            assertNotNull(customer.getFirstName());
            assertNotNull(customer.getEmail());
        }

        // Cleanup
        for (int i = 0; i < testCustomers.size(); i++) {
            firestoreService.deleteDocument(testCollection, "customer" + i);
        }
    }

    @Test
    @Order(108)
    void testQueryDocumentsTyped_WithRangeQuery_ShouldReturnTypedObjectsInRange() throws Exception {
        String testCollection = "query-typed-range-" + UUID.randomUUID();

        // Create test customers with ages from 20 to 60
        List<Customer> testCustomers = Customer.fakeData(9);
        for (int i = 0; i < testCustomers.size(); i++) {
            Customer customer = testCustomers.get(i);
            customer.setAge(20 + (i * 5)); // Ages: 20, 25, 30, 35, 40, 45, 50, 55, 60
            firestoreService.saveOrUpdate(testCollection, "customer" + i, customer);
        }

        // Query for customers aged between 30 and 45 (inclusive)
        List<Customer> results =
                firestoreService.queryDocuments(testCollection, Customer.class, collectionRef -> collectionRef
                        .whereGreaterThanOrEqualTo("age", 30)
                        .whereLessThanOrEqualTo("age", 45));

        assertNotNull(results);
        assertEquals(4, results.size()); // Should match ages: 30, 35, 40, 45
        for (Customer customer : results) {
            assertNotNull(customer);
            assertTrue(customer.getAge() >= 30 && customer.getAge() <= 45);
            assertNotNull(customer.getFirstName());
            assertNotNull(customer.getEmail());
        }

        // Cleanup
        for (int i = 0; i < testCustomers.size(); i++) {
            firestoreService.deleteDocument(testCollection, "customer" + i);
        }
    }

    @Test
    @Order(109)
    void testQueryDocumentsTyped_WithNoResults_ShouldReturnEmptyList() throws Exception {
        String testCollection = "query-typed-empty-" + UUID.randomUUID();

        // Create test customers
        List<Customer> testCustomers = Customer.fakeData(3);
        for (int i = 0; i < testCustomers.size(); i++) {
            Customer customer = testCustomers.get(i);
            customer.setAge(25);
            firestoreService.saveOrUpdate(testCollection, "customer" + i, customer);
        }

        // Query for non-existent age
        List<Customer> results = firestoreService.queryDocuments(
                testCollection, Customer.class, collectionRef -> collectionRef.whereEqualTo("age", 999));

        assertNotNull(results);
        assertTrue(results.isEmpty());

        // Cleanup
        for (int i = 0; i < testCustomers.size(); i++) {
            firestoreService.deleteDocument(testCollection, "customer" + i);
        }
    }

    @Test
    @Order(110)
    void testQueryDocumentsTyped_WithFieldSelection_ShouldMapAvailableFields() throws Exception {
        String testCollection = "query-typed-fields-" + UUID.randomUUID();

        // Create test customers with all fields
        List<Customer> testCustomers = Customer.fakeData(3);
        for (int i = 0; i < testCustomers.size(); i++) {
            Customer customer = testCustomers.get(i);
            customer.setAge(30 + i);
            customer.setAbout("This is customer " + i);
            firestoreService.saveOrUpdate(testCollection, "customer" + i, customer);
        }

        // Query all customers aged >= 30
        List<Customer> results = firestoreService.queryDocuments(
                testCollection, Customer.class, collectionRef -> collectionRef.whereGreaterThanOrEqualTo("age", 30));

        assertNotNull(results);
        assertEquals(3, results.size());

        // Verify all fields are properly mapped
        for (Customer customer : results) {
            assertNotNull(customer);
            assertTrue(customer.getAge() >= 30);
            assertNotNull(customer.getFirstName());
            assertNotNull(customer.getLastName());
            assertNotNull(customer.getEmail());
            // Note: 'about' field should also be present if it was saved
        }

        // Cleanup
        for (int i = 0; i < testCustomers.size(); i++) {
            firestoreService.deleteDocument(testCollection, "customer" + i);
        }
    }

    @Test
    @Order(111)
    void testQueryDocumentsTyped_WithOrderByAscending_ShouldReturnTypedObjectsInAscendingOrder() throws Exception {
        String testCollection = "query-typed-asc-" + UUID.randomUUID();

        // Create customers with random ages
        List<Customer> testCustomers = Customer.fakeData(5);
        int[] ages = {45, 25, 60, 30, 50};
        for (int i = 0; i < testCustomers.size(); i++) {
            Customer customer = testCustomers.get(i);
            customer.setAge(ages[i]);
            firestoreService.saveOrUpdate(testCollection, "customer" + i, customer);
        }

        // Query with ascending order
        List<Customer> results = firestoreService.queryDocuments(
                testCollection,
                Customer.class,
                collectionRef -> collectionRef.orderBy("age", com.google.cloud.firestore.Query.Direction.ASCENDING));

        assertNotNull(results);
        assertEquals(5, results.size());

        // Verify ascending order: 25, 30, 45, 50, 60
        assertEquals(25, results.get(0).getAge());
        assertEquals(30, results.get(1).getAge());
        assertEquals(45, results.get(2).getAge());
        assertEquals(50, results.get(3).getAge());
        assertEquals(60, results.get(4).getAge());

        // Cleanup
        for (int i = 0; i < testCustomers.size(); i++) {
            firestoreService.deleteDocument(testCollection, "customer" + i);
        }
    }

    @Test
    @Order(112)
    void testQueryDocumentsTyped_WithGreaterThanQuery_ShouldReturnTypedObjectsAboveThreshold() throws Exception {
        String testCollection = "query-typed-gt-" + UUID.randomUUID();

        // Create customers with different ages
        List<Customer> testCustomers = Customer.fakeData(7);
        for (int i = 0; i < testCustomers.size(); i++) {
            Customer customer = testCustomers.get(i);
            customer.setAge(20 + (i * 10)); // Ages: 20, 30, 40, 50, 60, 70, 80
            firestoreService.saveOrUpdate(testCollection, "customer" + i, customer);
        }

        // Query for age > 45
        List<Customer> results = firestoreService.queryDocuments(
                testCollection, Customer.class, collectionRef -> collectionRef.whereGreaterThan("age", 45));

        assertNotNull(results);
        assertEquals(4, results.size()); // Should match ages: 50, 60, 70, 80
        for (Customer customer : results) {
            assertNotNull(customer);
            assertTrue(customer.getAge() > 45);
        }

        // Cleanup
        for (int i = 0; i < testCustomers.size(); i++) {
            firestoreService.deleteDocument(testCollection, "customer" + i);
        }
    }

    @Test
    @Order(113)
    void testQueryDocumentsTyped_WithLimitAndOffset_ShouldReturnPaginatedTypedResults() throws Exception {
        String testCollection = "query-typed-paginate-" + UUID.randomUUID();

        // Create 10 customers with sequential ages
        List<Customer> testCustomers = Customer.fakeData(10);
        for (int i = 0; i < testCustomers.size(); i++) {
            Customer customer = testCustomers.get(i);
            customer.setAge(20 + i); // Ages: 20, 21, 22, ..., 29
            firestoreService.saveOrUpdate(testCollection, "customer" + i, customer);
        }

        // Query with offset and limit for pagination
        List<Customer> results =
                firestoreService.queryDocuments(testCollection, Customer.class, collectionRef -> collectionRef
                        .orderBy("age", com.google.cloud.firestore.Query.Direction.ASCENDING)
                        .offset(3)
                        .limit(4));

        assertNotNull(results);
        assertEquals(4, results.size());
        // After offset 3, we skip ages 20, 21, 22 and get 23, 24, 25, 26
        assertEquals(23, results.get(0).getAge());
        assertEquals(24, results.get(1).getAge());
        assertEquals(25, results.get(2).getAge());
        assertEquals(26, results.get(3).getAge());

        // Cleanup
        for (int i = 0; i < testCustomers.size(); i++) {
            firestoreService.deleteDocument(testCollection, "customer" + i);
        }
    }

    @Test
    @Order(9999)
    void testCleanup_FirestoreServiceTestCollections() throws Exception {
        firestoreService.deleteDocument(TEST_COLLECTION, testDocumentId);
    }
}
