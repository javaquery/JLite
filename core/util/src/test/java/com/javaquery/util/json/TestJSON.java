package com.javaquery.util.json;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test cases for {@link JSON}
 *
 * @author vicky.thakor
 * @since 1.0.0
 */
public class TestJSON {

    @Test
    public void test_merge_simpleKeyValuePairs() {
        JSONObject original = new JSONObject();
        original.put("name", "John");
        original.put("age", 30);

        JSONObject newJson = new JSONObject();
        newJson.put("city", "New York");
        newJson.put("country", "USA");

        JSON.merge(original, newJson);

        Assertions.assertEquals("John", original.getString("name"));
        Assertions.assertEquals(30, original.getInt("age"));
        Assertions.assertEquals("New York", original.getString("city"));
        Assertions.assertEquals("USA", original.getString("country"));
        Assertions.assertEquals(4, original.length());
    }

    @Test
    public void test_merge_overwriteExistingKeys() {
        JSONObject original = new JSONObject();
        original.put("name", "John");
        original.put("age", 30);

        JSONObject newJson = new JSONObject();
        newJson.put("name", "Jane");
        newJson.put("age", 25);

        JSON.merge(original, newJson);

        Assertions.assertEquals("Jane", original.getString("name"));
        Assertions.assertEquals(25, original.getInt("age"));
    }

    @Test
    public void test_merge_nestedJSONObjects() {
        JSONObject original = new JSONObject();
        JSONObject address = new JSONObject();
        address.put("street", "123 Main St");
        address.put("city", "Boston");
        original.put("name", "John");
        original.put("address", address);

        JSONObject newJson = new JSONObject();
        JSONObject newAddress = new JSONObject();
        newAddress.put("zip", "02101");
        newAddress.put("country", "USA");
        newJson.put("address", newAddress);

        JSON.merge(original, newJson);

        Assertions.assertEquals("John", original.getString("name"));
        JSONObject mergedAddress = original.getJSONObject("address");
        Assertions.assertEquals("123 Main St", mergedAddress.getString("street"));
        Assertions.assertEquals("Boston", mergedAddress.getString("city"));
        Assertions.assertEquals("02101", mergedAddress.getString("zip"));
        Assertions.assertEquals("USA", mergedAddress.getString("country"));
    }

    @Test
    public void test_merge_deeplyNestedJSONObjects() {
        JSONObject original = new JSONObject();
        JSONObject level1 = new JSONObject();
        JSONObject level2 = new JSONObject();
        level2.put("key1", "value1");
        level1.put("level2", level2);
        original.put("level1", level1);

        JSONObject newJson = new JSONObject();
        JSONObject newLevel1 = new JSONObject();
        JSONObject newLevel2 = new JSONObject();
        newLevel2.put("key2", "value2");
        newLevel1.put("level2", newLevel2);
        newJson.put("level1", newLevel1);

        JSON.merge(original, newJson);

        JSONObject mergedLevel2 = original.getJSONObject("level1").getJSONObject("level2");
        Assertions.assertEquals("value1", mergedLevel2.getString("key1"));
        Assertions.assertEquals("value2", mergedLevel2.getString("key2"));
    }

    @Test
    public void test_merge_overwriteNestedObjectWithPrimitive() {
        JSONObject original = new JSONObject();
        JSONObject address = new JSONObject();
        address.put("street", "123 Main St");
        original.put("address", address);

        JSONObject newJson = new JSONObject();
        newJson.put("address", "Simple String Address");

        JSON.merge(original, newJson);

        Assertions.assertEquals("Simple String Address", original.getString("address"));
    }

    @Test
    public void test_merge_overwritePrimitiveWithNestedObject() {
        JSONObject original = new JSONObject();
        original.put("address", "Simple String Address");

        JSONObject newJson = new JSONObject();
        JSONObject address = new JSONObject();
        address.put("street", "123 Main St");
        address.put("city", "Boston");
        newJson.put("address", address);

        JSON.merge(original, newJson);

        JSONObject mergedAddress = original.getJSONObject("address");
        Assertions.assertEquals("123 Main St", mergedAddress.getString("street"));
        Assertions.assertEquals("Boston", mergedAddress.getString("city"));
    }

    @Test
    public void test_merge_emptyNewJson() {
        JSONObject original = new JSONObject();
        original.put("name", "John");
        original.put("age", 30);

        JSONObject newJson = new JSONObject();

        JSON.merge(original, newJson);

        Assertions.assertEquals("John", original.getString("name"));
        Assertions.assertEquals(30, original.getInt("age"));
        Assertions.assertEquals(2, original.length());
    }

    @Test
    public void test_merge_emptyOriginal() {
        JSONObject original = new JSONObject();

        JSONObject newJson = new JSONObject();
        newJson.put("name", "John");
        newJson.put("age", 30);

        JSON.merge(original, newJson);

        Assertions.assertEquals("John", original.getString("name"));
        Assertions.assertEquals(30, original.getInt("age"));
        Assertions.assertEquals(2, original.length());
    }

    @Test
    public void test_merge_bothEmpty() {
        JSONObject original = new JSONObject();
        JSONObject newJson = new JSONObject();

        JSON.merge(original, newJson);

        Assertions.assertEquals(0, original.length());
    }

    @Test
    public void test_merge_withArrays() {
        JSONObject original = new JSONObject();
        JSONArray array1 = new JSONArray();
        array1.put("item1");
        array1.put("item2");
        original.put("items", array1);

        JSONObject newJson = new JSONObject();
        JSONArray array2 = new JSONArray();
        array2.put("item3");
        array2.put("item4");
        newJson.put("items", array2);

        JSON.merge(original, newJson);

        // Arrays should be replaced, not merged
        JSONArray mergedArray = original.getJSONArray("items");
        Assertions.assertEquals(2, mergedArray.length());
        Assertions.assertEquals("item3", mergedArray.getString(0));
        Assertions.assertEquals("item4", mergedArray.getString(1));
    }

    @Test
    public void test_merge_withNullValues() {
        JSONObject original = new JSONObject();
        original.put("name", "John");
        original.put("age", 30);

        JSONObject newJson = new JSONObject();
        newJson.put("name", JSONObject.NULL);

        JSON.merge(original, newJson);

        Assertions.assertTrue(original.isNull("name"));
        Assertions.assertEquals(30, original.getInt("age"));
    }

    @Test
    public void test_merge_withBooleanValues() {
        JSONObject original = new JSONObject();
        original.put("active", true);
        original.put("verified", false);

        JSONObject newJson = new JSONObject();
        newJson.put("verified", true);
        newJson.put("deleted", false);

        JSON.merge(original, newJson);

        Assertions.assertTrue(original.getBoolean("active"));
        Assertions.assertTrue(original.getBoolean("verified"));
        Assertions.assertFalse(original.getBoolean("deleted"));
    }

    @Test
    public void test_merge_withNumericValues() {
        JSONObject original = new JSONObject();
        original.put("intValue", 100);
        original.put("doubleValue", 99.99);

        JSONObject newJson = new JSONObject();
        newJson.put("intValue", 200);
        newJson.put("longValue", 1000000000L);
        newJson.put("floatValue", 3.14f);

        JSON.merge(original, newJson);

        Assertions.assertEquals(200, original.getInt("intValue"));
        Assertions.assertEquals(99.99, original.getDouble("doubleValue"));
        Assertions.assertEquals(1000000000L, original.getLong("longValue"));
        Assertions.assertEquals(3.14f, original.getFloat("floatValue"), 0.001);
    }

    @Test
    public void test_merge_complexNestedStructure() {
        JSONObject original = new JSONObject();
        JSONObject user = new JSONObject();
        user.put("id", 1);
        user.put("name", "John");
        JSONObject profile = new JSONObject();
        profile.put("bio", "Software Developer");
        user.put("profile", profile);
        original.put("user", user);

        JSONObject newJson = new JSONObject();
        JSONObject newUser = new JSONObject();
        newUser.put("email", "john@example.com");
        JSONObject newProfile = new JSONObject();
        newProfile.put("avatar", "avatar.jpg");
        newProfile.put("bio", "Senior Software Developer");
        newUser.put("profile", newProfile);
        newJson.put("user", newUser);

        JSON.merge(original, newJson);

        JSONObject mergedUser = original.getJSONObject("user");
        Assertions.assertEquals(1, mergedUser.getInt("id"));
        Assertions.assertEquals("John", mergedUser.getString("name"));
        Assertions.assertEquals("john@example.com", mergedUser.getString("email"));

        JSONObject mergedProfile = mergedUser.getJSONObject("profile");
        Assertions.assertEquals("Senior Software Developer", mergedProfile.getString("bio"));
        Assertions.assertEquals("avatar.jpg", mergedProfile.getString("avatar"));
    }

    @Test
    public void test_merge_multipleNestedLevels() {
        JSONObject original = new JSONObject();
        JSONObject level1 = new JSONObject();
        JSONObject level2 = new JSONObject();
        JSONObject level3 = new JSONObject();
        level3.put("deepKey", "deepValue");
        level2.put("level3", level3);
        level1.put("level2", level2);
        original.put("level1", level1);
        original.put("topKey", "topValue");

        JSONObject newJson = new JSONObject();
        JSONObject newLevel1 = new JSONObject();
        JSONObject newLevel2 = new JSONObject();
        JSONObject newLevel3 = new JSONObject();
        newLevel3.put("newDeepKey", "newDeepValue");
        newLevel2.put("level3", newLevel3);
        newLevel2.put("middleKey", "middleValue");
        newLevel1.put("level2", newLevel2);
        newJson.put("level1", newLevel1);

        JSON.merge(original, newJson);

        Assertions.assertEquals("topValue", original.getString("topKey"));
        JSONObject mergedLevel3 =
                original.getJSONObject("level1").getJSONObject("level2").getJSONObject("level3");
        Assertions.assertEquals("deepValue", mergedLevel3.getString("deepKey"));
        Assertions.assertEquals("newDeepValue", mergedLevel3.getString("newDeepKey"));
        Assertions.assertEquals(
                "middleValue",
                original.getJSONObject("level1").getJSONObject("level2").getString("middleKey"));
    }

    @Test
    public void test_merge_withSpecialCharactersInKeys() {
        JSONObject original = new JSONObject();
        original.put("key-with-dash", "value1");
        original.put("key.with.dot", "value2");

        JSONObject newJson = new JSONObject();
        newJson.put("key_with_underscore", "value3");
        newJson.put("key with space", "value4");

        JSON.merge(original, newJson);

        Assertions.assertEquals("value1", original.getString("key-with-dash"));
        Assertions.assertEquals("value2", original.getString("key.with.dot"));
        Assertions.assertEquals("value3", original.getString("key_with_underscore"));
        Assertions.assertEquals("value4", original.getString("key with space"));
    }

    @Test
    public void test_merge_withUnicodeValues() {
        JSONObject original = new JSONObject();
        original.put("greeting", "Hello");

        JSONObject newJson = new JSONObject();
        newJson.put("greeting", "你好");
        newJson.put("arabic", "مرحبا");
        newJson.put("emoji", "😊");

        JSON.merge(original, newJson);

        Assertions.assertEquals("你好", original.getString("greeting"));
        Assertions.assertEquals("مرحبا", original.getString("arabic"));
        Assertions.assertEquals("😊", original.getString("emoji"));
    }

    @Test
    public void test_merge_preservesOriginalReference() {
        JSONObject original = new JSONObject();
        original.put("name", "John");

        JSONObject newJson = new JSONObject();
        newJson.put("age", 30);

        JSONObject reference = original;
        JSON.merge(original, newJson);

        // Verify that original and reference point to the same object
        Assertions.assertSame(original, reference);
        Assertions.assertEquals("John", reference.getString("name"));
        Assertions.assertEquals(30, reference.getInt("age"));
    }

    @Test
    public void test_merge_doesNotModifyNewJson() {
        JSONObject original = new JSONObject();
        original.put("name", "John");

        JSONObject newJson = new JSONObject();
        newJson.put("age", 30);
        newJson.put("city", "Boston");

        JSON.merge(original, newJson);

        // Verify newJson remains unchanged
        Assertions.assertEquals(2, newJson.length());
        Assertions.assertEquals(30, newJson.getInt("age"));
        Assertions.assertEquals("Boston", newJson.getString("city"));
        Assertions.assertFalse(newJson.has("name"));
    }

    @Test
    public void test_merge_chainedMerges() {
        JSONObject original = new JSONObject();
        original.put("key1", "value1");

        JSONObject json2 = new JSONObject();
        json2.put("key2", "value2");

        JSONObject json3 = new JSONObject();
        json3.put("key3", "value3");

        JSON.merge(original, json2);
        JSON.merge(original, json3);

        Assertions.assertEquals("value1", original.getString("key1"));
        Assertions.assertEquals("value2", original.getString("key2"));
        Assertions.assertEquals("value3", original.getString("key3"));
        Assertions.assertEquals(3, original.length());
    }
}
