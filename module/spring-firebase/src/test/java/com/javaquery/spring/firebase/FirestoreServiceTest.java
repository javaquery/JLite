package com.javaquery.spring.firebase;

import static org.junit.jupiter.api.Assertions.*;

import com.javaquery.spring.firebase.model.Customer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
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
}
