package com.javaquery.spring.firebase;

import com.google.firebase.database.*;
import com.javaquery.util.Is;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service class for interacting with Firebase Realtime Database.
 * Provides common CRUD operations and query methods for database management.
 *
 * @author vicky.thakor
 * @since 1.0.0
 */
@Service
public class FirebaseService {

    @Value("${firebase.database.queryTimeout:10}")
    private int databaseQueryTimeout;

    /**
     * Get Firebase Database instance.
     *
     * @return FirebaseDatabase instance
     */
    private FirebaseDatabase getDatabase() {
        return FirebaseDatabase.getInstance();
    }

    /**
     * Get a database reference for a specific path.
     *
     * @param path the database path
     * @return DatabaseReference for the specified path
     */
    public DatabaseReference getReference(String path) {
        return getDatabase().getReference(path);
    }

    /**
     * Save or update data at a specific path.
     *
     * @param path the database path
     * @param data the data to be saved or updated
     * @throws Exception if an error occurs during the operation
     */
    public void setValue(String path, Object data) throws Exception {
        CompletableFuture<Void> future = new CompletableFuture<>();
        getReference(path).setValue(data, (error, ref) -> {
            if (error != null) {
                future.completeExceptionally(new Exception(error.getMessage()));
            } else {
                future.complete(null);
            }
        });
        future.get(databaseQueryTimeout, TimeUnit.SECONDS);
    }

    /**
     * Update specific fields at a path without overwriting the entire object.
     *
     * @param path    the database path
     * @param updates a map of child keys to update with their new values
     * @throws Exception if an error occurs during the operation
     */
    public void updateChildren(String path, Map<String, Object> updates) throws Exception {
        CompletableFuture<Void> future = new CompletableFuture<>();
        getReference(path).updateChildren(updates, (error, ref) -> {
            if (error != null) {
                future.completeExceptionally(new Exception(error.getMessage()));
            } else {
                future.complete(null);
            }
        });
        future.get(databaseQueryTimeout, TimeUnit.SECONDS);
    }

    /**
     * Push a new child node with an auto-generated key.
     *
     * @param path the database path
     * @param data the data to be added
     * @return the auto-generated key of the new child
     * @throws Exception if an error occurs during the operation
     */
    public String push(String path, Object data) throws Exception {
        DatabaseReference newRef = getReference(path).push();
        String key = newRef.getKey();
        CompletableFuture<Void> future = new CompletableFuture<>();
        newRef.setValue(data, (error, ref) -> {
            if (error != null) {
                future.completeExceptionally(new Exception(error.getMessage()));
            } else {
                future.complete(null);
            }
        });
        future.get(databaseQueryTimeout, TimeUnit.SECONDS);
        return key;
    }

    /**
     * Get data at a specific path.
     *
     * @param path the database path
     * @return DataSnapshot containing the data
     * @throws Exception if an error occurs during the operation
     */
    public DataSnapshot getValue(String path) throws Exception {
        CompletableFuture<DataSnapshot> future = new CompletableFuture<>();
        getReference(path).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                future.complete(snapshot);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                future.completeExceptionally(new Exception(error.getMessage()));
            }
        });
        return future.get(databaseQueryTimeout, TimeUnit.SECONDS);
    }

    /**
     * Get data at a specific path and convert it to a specific class type.
     *
     * @param path       the database path
     * @param valueClass the class type to convert the data to
     * @param <T>        the type parameter
     * @return the data converted to the specified type, or null if not found
     * @throws Exception if an error occurs during the operation
     */
    public <T> T getValue(String path, Class<T> valueClass) throws Exception {
        DataSnapshot snapshot = getValue(path);
        if (snapshot.exists()) {
            return snapshot.getValue(valueClass);
        }
        return null;
    }

    /**
     * Get data at a specific path as a Map.
     *
     * @param path the database path
     * @return a Map containing the data, or null if not found
     * @throws Exception if an error occurs during the operation
     */
    public Map<String, Object> getValueAsMap(String path) throws Exception {
        DataSnapshot snapshot = getValue(path);
        if (snapshot.exists()) {
            @SuppressWarnings("unchecked")
            Map<String, Object> result = (Map<String, Object>) snapshot.getValue();
            return result;
        }
        return null;
    }

    /**
     * Get all children at a specific path as a list of Maps.
     *
     * @param path the database path
     * @return a list of Maps, each representing a child node
     * @throws Exception if an error occurs during the operation
     */
    public List<Map<String, Object>> getChildren(String path) throws Exception {
        DataSnapshot snapshot = getValue(path);
        List<Map<String, Object>> children = new ArrayList<>();
        if (snapshot.exists()) {
            for (DataSnapshot child : snapshot.getChildren()) {
                Map<String, Object> childData = new HashMap<>();
                childData.put("key", child.getKey());
                @SuppressWarnings("unchecked")
                Map<String, Object> value = (Map<String, Object>) child.getValue();
                if (Is.nonNull(value)) {
                    childData.putAll(value);
                }
                children.add(childData);
            }
        }
        return children;
    }

    /**
     * Get all children at a specific path as a list of specific class type.
     *
     * @param path       the database path
     * @param valueClass the class type to convert each child to
     * @param <T>        the type parameter
     * @return a list of objects of the specified type
     * @throws Exception if an error occurs during the operation
     */
    public <T> List<T> getChildren(String path, Class<T> valueClass) throws Exception {
        DataSnapshot snapshot = getValue(path);
        List<T> children = new ArrayList<>();
        if (snapshot.exists()) {
            for (DataSnapshot child : snapshot.getChildren()) {
                T value = child.getValue(valueClass);
                if (Is.nonNull(value)) {
                    children.add(value);
                }
            }
        }
        return children;
    }

    /**
     * Delete data at a specific path.
     *
     * @param path the database path
     * @throws Exception if an error occurs during the operation
     */
    public void delete(String path) throws Exception {
        CompletableFuture<Void> future = new CompletableFuture<>();
        getReference(path).removeValue((error, ref) -> {
            if (error != null) {
                future.completeExceptionally(new Exception(error.getMessage()));
            } else {
                future.complete(null);
            }
        });
        future.get(databaseQueryTimeout, TimeUnit.SECONDS);
    }

    /**
     * Delete data at a specific path and return the deleted data.
     *
     * @param path the database path
     * @return DataSnapshot containing the deleted data
     * @throws Exception if an error occurs during the operation
     */
    public DataSnapshot deleteAndReturn(String path) throws Exception {
        DataSnapshot snapshot = getValue(path);
        if (snapshot.exists()) {
            delete(path);
        }
        return snapshot;
    }

    /**
     * Check if data exists at a specific path.
     *
     * @param path the database path
     * @return true if data exists, false otherwise
     * @throws Exception if an error occurs during the operation
     */
    public boolean exists(String path) throws Exception {
        DataSnapshot snapshot = getValue(path);
        return snapshot.exists();
    }

    /**
     * Query data with limit to first N items.
     *
     * @param path  the database path
     * @param limit the number of items to limit
     * @return DataSnapshot containing the query results
     * @throws Exception if an error occurs during the operation
     */
    public DataSnapshot limitToFirst(String path, int limit) throws Exception {
        CompletableFuture<DataSnapshot> future = new CompletableFuture<>();
        getReference(path).limitToFirst(limit).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                future.complete(snapshot);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                future.completeExceptionally(new Exception(error.getMessage()));
            }
        });
        return future.get(databaseQueryTimeout, TimeUnit.SECONDS);
    }

    /**
     * Query data with limit to last N items.
     *
     * @param path  the database path
     * @param limit the number of items to limit
     * @return DataSnapshot containing the query results
     * @throws Exception if an error occurs during the operation
     */
    public DataSnapshot limitToLast(String path, int limit) throws Exception {
        CompletableFuture<DataSnapshot> future = new CompletableFuture<>();
        getReference(path).limitToLast(limit).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                future.complete(snapshot);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                future.completeExceptionally(new Exception(error.getMessage()));
            }
        });
        return future.get(databaseQueryTimeout, TimeUnit.SECONDS);
    }

    /**
     * Query data with a custom query builder.
     * @param path    the database path
     * @param queryBuilder a function that builds the query
     * @return DataSnapshot containing the query results
     * @throws Exception if an error occurs during the operation
     */
    public DataSnapshot queryDataSnapshot(String path, Function<DatabaseReference, Query> queryBuilder)
            throws Exception {
        CompletableFuture<DataSnapshot> future = new CompletableFuture<>();
        Query query = queryBuilder.apply(getReference(path));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                future.complete(snapshot);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                future.completeExceptionally(new Exception(error.getMessage()));
            }
        });
        return future.get(databaseQueryTimeout, TimeUnit.SECONDS);
    }

    /**
     * Add a value event listener to a specific path.
     *
     * @param path     the database path
     * @param listener the value event listener
     * @return the ValueEventListener that was added
     */
    public ValueEventListener addValueEventListener(String path, ValueEventListener listener) {
        getReference(path).addValueEventListener(listener);
        return listener;
    }

    /**
     * Add a child event listener to a specific path.
     *
     * @param path     the database path
     * @param listener the child event listener
     * @return the ChildEventListener that was added
     */
    public ChildEventListener addChildEventListener(String path, ChildEventListener listener) {
        getReference(path).addChildEventListener(listener);
        return listener;
    }

    /**
     * Remove a value event listener from a specific path.
     *
     * @param path     the database path
     * @param listener the value event listener to remove
     */
    public void removeValueEventListener(String path, ValueEventListener listener) {
        getReference(path).removeEventListener(listener);
    }

    /**
     * Remove a child event listener from a specific path.
     *
     * @param path     the database path
     * @param listener the child event listener to remove
     */
    public void removeChildEventListener(String path, ChildEventListener listener) {
        getReference(path).removeEventListener(listener);
    }

    /**
     * Increment a numeric value at a specific path atomically.
     *
     * @param path      the database path
     * @param increment the amount to increment (can be negative for decrement)
     * @return the new value after increment
     * @throws Exception if an error occurs during the operation
     */
    public Long incrementValue(String path, long increment) throws Exception {
        CompletableFuture<Long> future = new CompletableFuture<>();
        getReference(path).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData currentData) {
                Long currentValue = currentData.getValue(Long.class);
                if (currentValue == null) {
                    currentValue = 0L;
                }
                currentData.setValue(currentValue + increment);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(DatabaseError error, boolean committed, DataSnapshot snapshot) {
                if (error != null) {
                    future.completeExceptionally(new Exception(error.getMessage()));
                } else {
                    future.complete(snapshot.getValue(Long.class));
                }
            }
        });
        return future.get(databaseQueryTimeout, TimeUnit.SECONDS);
    }

    /**
     * Set a value with server timestamp.
     *
     * @param path      the database path
     * @param fieldName the field name for the timestamp
     * @throws Exception if an error occurs during the operation
     */
    public void setServerTimestamp(String path, String fieldName) throws Exception {
        Map<String, Object> update = new HashMap<>();
        update.put(fieldName, ServerValue.TIMESTAMP);
        updateChildren(path, update);
    }
}
