package com.javaquery.spring.firebase;

import static org.junit.jupiter.api.Assertions.*;

import com.google.firebase.database.*;
import com.javaquery.spring.firebase.model.Customer;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Integration test for FirebaseService using Firebase Realtime Database emulator or real instance
 *
 * @author vicky.thakor
 * @since 1.0.0
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestConfiguration.class)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FirebaseServiceTest {

    @Autowired
    private FirebaseService firebaseService;

    private static final String TEST_PATH = "test-data";
    private static String testNodeKey;
    private static Customer customer;

    @BeforeAll
    static void setUpClass() {
        testNodeKey = "test-node-" + UUID.randomUUID();
        customer = Customer.fakeData(1).get(0);
    }

    @Test
    @Order(1)
    void testGetReference() {
        DatabaseReference reference = firebaseService.getReference(TEST_PATH);
        assertNotNull(reference, "Database reference should not be null");
        assertEquals(TEST_PATH, reference.getKey(), "Reference key should match the path");
    }

    @Test
    @Order(2)
    void testSetValue() throws Exception {
        String path = TEST_PATH + "/" + testNodeKey;
        firebaseService.setValue(path, customer);

        DataSnapshot snapshot = firebaseService.getValue(path);
        assertTrue(snapshot.exists(), "Data should exist after setValue");
    }

    @Test
    @Order(3)
    void testGetValue() throws Exception {
        String path = TEST_PATH + "/" + testNodeKey;
        DataSnapshot snapshot = firebaseService.getValue(path);

        assertNotNull(snapshot, "Snapshot should not be null");
        assertTrue(snapshot.exists(), "Data should exist");
    }

    @Test
    @Order(4)
    void testGetValueWithClass() throws Exception {
        String path = TEST_PATH + "/" + testNodeKey;
        Customer retrievedCustomer = firebaseService.getValue(path, Customer.class);

        assertNotNull(retrievedCustomer, "Retrieved customer should not be null");
        assertEquals(customer.getFirstName(), retrievedCustomer.getFirstName(), "First name should match");
        assertEquals(customer.getLastName(), retrievedCustomer.getLastName(), "Last name should match");
        assertEquals(customer.getEmail(), retrievedCustomer.getEmail(), "Email should match");
        assertEquals(customer.getAge(), retrievedCustomer.getAge(), "Age should match");
    }

    @Test
    @Order(5)
    void testGetValueAsMap() throws Exception {
        String path = TEST_PATH + "/" + testNodeKey;
        Map<String, Object> dataMap = firebaseService.getValueAsMap(path);

        assertNotNull(dataMap, "Data map should not be null");
        assertFalse(dataMap.isEmpty(), "Data map should not be empty");
        assertEquals(customer.getFirstName(), dataMap.get("firstName"), "First name should match");
    }

    @Test
    @Order(6)
    void testUpdateChildren() throws Exception {
        String path = TEST_PATH + "/" + testNodeKey;
        Map<String, Object> updates = new HashMap<>();
        updates.put("firstName", "UpdatedFirstName");
        updates.put("age", 50);

        firebaseService.updateChildren(path, updates);

        Customer updatedCustomer = firebaseService.getValue(path, Customer.class);
        assertEquals("UpdatedFirstName", updatedCustomer.getFirstName(), "First name should be updated");
        assertEquals(50, updatedCustomer.getAge(), "Age should be updated");
        assertEquals(customer.getLastName(), updatedCustomer.getLastName(), "Last name should remain unchanged");
    }

    @Test
    @Order(7)
    void testPush() throws Exception {
        Customer newCustomer = Customer.fakeData(1).get(0);
        String generatedKey = firebaseService.push(TEST_PATH, newCustomer);

        assertNotNull(generatedKey, "Generated key should not be null");
        assertFalse(generatedKey.isEmpty(), "Generated key should not be empty");

        String path = TEST_PATH + "/" + generatedKey;
        Customer retrievedCustomer = firebaseService.getValue(path, Customer.class);
        assertNotNull(retrievedCustomer, "Retrieved customer should not be null");
        assertEquals(newCustomer.getEmail(), retrievedCustomer.getEmail(), "Email should match");
    }

    @Test
    @Order(8)
    void testExists() throws Exception {
        String existingPath = TEST_PATH + "/" + testNodeKey;
        String nonExistingPath = TEST_PATH + "/non-existing-node-" + UUID.randomUUID();

        assertTrue(firebaseService.exists(existingPath), "Data should exist at the path");
        assertFalse(firebaseService.exists(nonExistingPath), "Data should not exist at non-existing path");
    }

    @Test
    @Order(9)
    void testGetChildren() throws Exception {
        List<Map<String, Object>> children = firebaseService.getChildren(TEST_PATH);

        assertNotNull(children, "Children list should not be null");
        assertFalse(children.isEmpty(), "Children list should not be empty");
        assertTrue(children.size() >= 2, "Should have at least 2 children from previous tests");

        for (Map<String, Object> child : children) {
            assertTrue(child.containsKey("key"), "Each child should have a key");
            assertNotNull(child.get("key"), "Key should not be null");
        }
    }

    @Test
    @Order(10)
    void testGetChildrenWithClass() throws Exception {
        List<Customer> customers = firebaseService.getChildren(TEST_PATH, Customer.class);

        assertNotNull(customers, "Customers list should not be null");
        assertFalse(customers.isEmpty(), "Customers list should not be empty");
        assertTrue(customers.size() >= 2, "Should have at least 2 customers from previous tests");

        for (Customer cust : customers) {
            assertNotNull(cust, "Customer should not be null");
        }
    }

    @Test
    @Order(11)
    void testLimitToFirst() throws Exception {
        DataSnapshot snapshot = firebaseService.limitToFirst(TEST_PATH, 1);

        assertNotNull(snapshot, "Snapshot should not be null");
        assertTrue(snapshot.exists(), "Data should exist");

        long count = snapshot.getChildrenCount();
        assertEquals(1, count, "Should return exactly 1 item");
    }

    @Test
    @Order(12)
    void testLimitToLast() throws Exception {
        DataSnapshot snapshot = firebaseService.limitToLast(TEST_PATH, 1);

        assertNotNull(snapshot, "Snapshot should not be null");
        assertTrue(snapshot.exists(), "Data should exist");

        long count = snapshot.getChildrenCount();
        assertEquals(1, count, "Should return exactly 1 item");
    }

    @Test
    @Order(13)
    void testQueryDataSnapshot() throws Exception {
        DataSnapshot snapshot = firebaseService.queryDataSnapshot(
                TEST_PATH, ref -> ref.orderByChild("firstName").equalTo("UpdatedFirstName"));

        assertNotNull(snapshot, "Snapshot should not be null");

        boolean found = false;
        for (DataSnapshot child : snapshot.getChildren()) {
            Customer cust = child.getValue(Customer.class);
            if (cust != null && "UpdatedFirstName".equals(cust.getFirstName())) {
                found = true;
                break;
            }
        }
        assertTrue(found, "Should find customer with UpdatedFirstName");
    }

    @Test
    @Order(14)
    void testIncrementValue() throws Exception {
        String counterPath = TEST_PATH + "/counter";

        firebaseService.setValue(counterPath, 10L);

        Long newValue = firebaseService.incrementValue(counterPath, 5L);
        assertEquals(15L, newValue, "Value should be incremented to 15");

        newValue = firebaseService.incrementValue(counterPath, -3L);
        assertEquals(12L, newValue, "Value should be decremented to 12");

        firebaseService.delete(counterPath);
    }

    @Test
    @Order(15)
    void testIncrementValueFromNull() throws Exception {
        String counterPath = TEST_PATH + "/newCounter";

        Long newValue = firebaseService.incrementValue(counterPath, 7L);
        assertEquals(7L, newValue, "Value should be 7 when starting from null");

        firebaseService.delete(counterPath);
    }

    @Test
    @Order(16)
    void testSetServerTimestamp() throws Exception {
        String timestampPath = TEST_PATH + "/timestampNode";
        firebaseService.setValue(timestampPath, new HashMap<String, Object>());
        firebaseService.setServerTimestamp(timestampPath, "createdAt");

        Map<String, Object> data = firebaseService.getValueAsMap(timestampPath);
        assertNotNull(data, "Data should not be null");
        assertTrue(data.containsKey("createdAt"), "Should contain createdAt field");
        assertNotNull(data.get("createdAt"), "createdAt should not be null");

        firebaseService.delete(timestampPath);
    }

    @Test
    @Order(17)
    void testAddValueEventListener() throws Exception {
        String listenerPath = TEST_PATH + "/listenerTest";
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger callCount = new AtomicInteger(0);

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                callCount.incrementAndGet();
                latch.countDown();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                fail("Listener should not be cancelled");
            }
        };

        firebaseService.addValueEventListener(listenerPath, listener);
        firebaseService.setValue(listenerPath, "test-value");

        assertTrue(latch.await(5, TimeUnit.SECONDS), "Listener should be called within 5 seconds");
        assertTrue(callCount.get() >= 1, "Listener should be called at least once");

        firebaseService.removeValueEventListener(listenerPath, listener);
        firebaseService.delete(listenerPath);
    }

    @Test
    @Order(18)
    void testAddChildEventListener() throws Exception {
        String listenerPath = TEST_PATH + "/childListenerTest";
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger addedCount = new AtomicInteger(0);

        ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                addedCount.incrementAndGet();
                latch.countDown();
            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String previousChildName) {}

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {}

            @Override
            public void onChildMoved(DataSnapshot snapshot, String previousChildName) {}

            @Override
            public void onCancelled(DatabaseError error) {
                fail("Listener should not be cancelled");
            }
        };

        firebaseService.addChildEventListener(listenerPath, listener);
        firebaseService.push(listenerPath, "child-value");

        assertTrue(latch.await(5, TimeUnit.SECONDS), "Listener should be called within 5 seconds");
        assertTrue(addedCount.get() >= 1, "Child added listener should be called at least once");

        firebaseService.removeChildEventListener(listenerPath, listener);
        firebaseService.delete(listenerPath);
    }

    @Test
    @Order(19)
    void testDeleteAndReturn() throws Exception {
        String deletePath = TEST_PATH + "/deleteTest";
        Customer testCustomer = Customer.fakeData(1).get(0);
        firebaseService.setValue(deletePath, testCustomer);

        DataSnapshot deletedSnapshot = firebaseService.deleteAndReturn(deletePath);

        assertTrue(deletedSnapshot.exists(), "Deleted snapshot should have existed before deletion");
        Customer deletedCustomer = deletedSnapshot.getValue(Customer.class);
        assertNotNull(deletedCustomer, "Deleted customer should not be null");
        assertEquals(testCustomer.getEmail(), deletedCustomer.getEmail(), "Email should match");

        assertFalse(firebaseService.exists(deletePath), "Data should not exist after deletion");
    }

    @Test
    @Order(20)
    void testGetValueWithClassNonExisting() throws Exception {
        String nonExistingPath = TEST_PATH + "/non-existing-" + UUID.randomUUID();
        Customer result = firebaseService.getValue(nonExistingPath, Customer.class);

        assertNull(result, "Should return null for non-existing path");
    }

    @Test
    @Order(21)
    void testGetValueAsMapNonExisting() throws Exception {
        String nonExistingPath = TEST_PATH + "/non-existing-" + UUID.randomUUID();
        Map<String, Object> result = firebaseService.getValueAsMap(nonExistingPath);

        assertNull(result, "Should return null for non-existing path");
    }

    @Test
    @Order(22)
    void testGetChildrenEmptyPath() throws Exception {
        String emptyPath = TEST_PATH + "/empty-" + UUID.randomUUID();
        List<Map<String, Object>> children = firebaseService.getChildren(emptyPath);

        assertNotNull(children, "Children list should not be null");
        assertTrue(children.isEmpty(), "Children list should be empty for non-existing path");
    }

    @Test
    @Order(23)
    void testGetChildrenWithClassEmptyPath() throws Exception {
        String emptyPath = TEST_PATH + "/empty-" + UUID.randomUUID();
        List<Customer> customers = firebaseService.getChildren(emptyPath, Customer.class);

        assertNotNull(customers, "Customers list should not be null");
        assertTrue(customers.isEmpty(), "Customers list should be empty for non-existing path");
    }

    @Test
    @Order(999)
    void cleanup() throws Exception {
        firebaseService.delete(TEST_PATH);

        assertFalse(firebaseService.exists(TEST_PATH), "Test data should be deleted after cleanup");
    }
}
