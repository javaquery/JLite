package com.javaquery.spring.service;

import static org.junit.jupiter.api.Assertions.*;

import com.javaquery.spring.TestConfiguration;
import com.javaquery.spring.model.Customer;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author vicky.thakor
 * @since 1.0.1
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestConfiguration.class)
class ObjectMapperServiceTest {

    @Autowired
    private ObjectMapperService objectMapperService;

    @Test
    void toJson_WithValidObject_ShouldReturnJsonString() {
        Customer customer = Customer.fakeData(1).get(0);
        String result = objectMapperService.toJson(customer);

        assertNotNull(result);
        assertTrue(result.contains("\"firstName\":\"" + customer.getFirstName() + "\""));
        assertTrue(result.contains("\"lastName\":\"" + customer.getLastName() + "\""));
        assertTrue(result.contains("\"age\":" + customer.getAge()));
    }

    @Test
    void toJson_WithNullObject_ShouldHandleGracefully() {
        String result = objectMapperService.toJson(null);
        assertEquals("null", result);
    }

    @Test
    void toSnakeCaseJson_WithValidObject_ShouldReturnSnakeCaseJsonString() {
        Customer customer = Customer.fakeData(1).get(0);
        String result = objectMapperService.toSnakeCaseJson(customer);

        assertNotNull(result);
        assertTrue(result.contains("\"first_name\":\"" + customer.getFirstName() + "\""));
        assertTrue(result.contains("\"last_name\":\"" + customer.getLastName() + "\""));
        assertTrue(result.contains("\"age\":" + customer.getAge()));
    }

    @Test
    void toSnakeCaseJson_WithNullObject_ShouldHandleGracefully() {
        String result = objectMapperService.toSnakeCaseJson(null);

        assertEquals("null", result);
    }

    @Test
    void convertValue_ShouldConvertMapToObject() {
        Map<String, Object> map = new HashMap<>();
        map.put("firstName", "Alice");
        map.put("lastName", "Johnson");
        map.put("email", "alice.johnson@example.com");
        map.put("age", 28);

        Customer result = objectMapperService.convertValue(map, Customer.class);

        assertNotNull(result);
        assertEquals("Alice", result.getFirstName());
        assertEquals("Johnson", result.getLastName());
        assertEquals("alice.johnson@example.com", result.getEmail());
        assertEquals(28, result.getAge());
    }

    @Test
    void convertValue_ShouldConvertObjectToMap() {
        Customer customer = Customer.fakeData(1).get(0);

        @SuppressWarnings("unchecked")
        Map<String, Object> result = objectMapperService.convertValue(customer, Map.class);

        assertNotNull(result);
        assertEquals(customer.getFirstName(), result.get("firstName"));
        assertEquals(customer.getLastName(), result.get("lastName"));
        assertEquals(customer.getEmail(), result.get("email"));
        assertEquals(customer.getAge(), result.get("age"));
    }

    @Test
    void readValue_WithValidJson_ShouldDeserializeSuccessfully() {
        String json = "{\"firstName\":\"Emma\",\"lastName\":\"Brown\",\"email\":\"emma@example.com\",\"age\":32}";
        Customer result = objectMapperService.readValue(json, Customer.class);

        assertNotNull(result);
        assertEquals("Emma", result.getFirstName());
        assertEquals("Brown", result.getLastName());
        assertEquals("emma@example.com", result.getEmail());
        assertEquals(32, result.getAge());
    }

    @Test
    void readValue_WithInvalidJson_ShouldReturnNull() {
        String invalidJson = "{invalid json}";
        Customer result = objectMapperService.readValue(invalidJson, Customer.class);

        assertNull(result);
    }

    @Test
    void readValue_WithNullString_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> objectMapperService.readValue(null, Customer.class));
    }

    @Test
    void readValue_WithEmptyJson_ShouldReturnNull() {
        String emptyJson = "";
        Customer result = objectMapperService.readValue(emptyJson, Customer.class);

        assertNull(result);
    }

    @Test
    void readSnakeCaseValue_WithValidSnakeCaseJson_ShouldDeserializeSuccessfully() {
        String json = "{\"first_name\":\"Sophia\",\"last_name\":\"Davis\",\"email\":\"sophia@example.com\",\"age\":29}";
        Customer result = objectMapperService.readSnakeCaseValue(json, Customer.class);

        assertNotNull(result);
        assertEquals("Sophia", result.getFirstName());
        assertEquals("Davis", result.getLastName());
        assertEquals("sophia@example.com", result.getEmail());
        assertEquals(29, result.getAge());
    }

    @Test
    void readSnakeCaseValue_WithInvalidJson_ShouldReturnNull() {
        String invalidJson = "{invalid json}";
        Customer result = objectMapperService.readSnakeCaseValue(invalidJson, Customer.class);

        assertNull(result);
    }

    @Test
    void readSnakeCaseValue_WithNullString_ShouldThrowException() {
        assertThrows(
                IllegalArgumentException.class, () -> objectMapperService.readSnakeCaseValue(null, Customer.class));
    }

    @Test
    void integrationTest_SerializeAndDeserialize_ShouldMaintainDataIntegrity() {
        Customer originalCustomer = Customer.fakeData(1).get(0);

        String json = objectMapperService.toJson(originalCustomer);
        Customer deserializedCustomer = objectMapperService.readValue(json, Customer.class);

        assertNotNull(json);
        assertNotNull(deserializedCustomer);
        assertEquals(originalCustomer.getFirstName(), deserializedCustomer.getFirstName());
        assertEquals(originalCustomer.getLastName(), deserializedCustomer.getLastName());
        assertEquals(originalCustomer.getEmail(), deserializedCustomer.getEmail());
        assertEquals(originalCustomer.getAge(), deserializedCustomer.getAge());
    }

    @Test
    void integrationTest_SerializeAndDeserializeSnakeCase_ShouldMaintainDataIntegrity() {
        Customer originalCustomer = Customer.fakeData(1).get(0);

        String json = objectMapperService.toSnakeCaseJson(originalCustomer);
        Customer deserializedCustomer = objectMapperService.readSnakeCaseValue(json, Customer.class);

        assertNotNull(json);
        assertTrue(json.contains("first_name"));
        assertTrue(json.contains("last_name"));
        assertNotNull(deserializedCustomer);
        assertEquals(originalCustomer.getFirstName(), deserializedCustomer.getFirstName());
        assertEquals(originalCustomer.getLastName(), deserializedCustomer.getLastName());
        assertEquals(originalCustomer.getEmail(), deserializedCustomer.getEmail());
        assertEquals(originalCustomer.getAge(), deserializedCustomer.getAge());
    }

    @Test
    void integrationTest_CompareSnakeCaseAndCamelCase_ShouldProduceDifferentFormats() {
        Customer customer = Customer.fakeData(1).get(0);

        String camelCaseJson = objectMapperService.toJson(customer);
        String snakeCaseJson = objectMapperService.toSnakeCaseJson(customer);

        assertNotNull(camelCaseJson);
        assertNotNull(snakeCaseJson);
        assertNotEquals(camelCaseJson, snakeCaseJson);
        assertTrue(camelCaseJson.contains("firstName"));
        assertTrue(snakeCaseJson.contains("first_name"));
        assertEquals(camelCaseJson.contains(customer.getFirstName()), snakeCaseJson.contains(customer.getFirstName()));
    }
}
