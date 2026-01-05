package com.javaquery.spring.firebase;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.javaquery.util.Is;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author vicky.thakor
 * @since 1.0.0
 */
@Service
public class FirestoreService {

    @Value("${firebase.firestore.queryTimeout:10}")
    private int firestoreQueryTimeout;

    @Value("${firebase.firestore.pageSize:50}")
    private int firestorePageSize;

    private Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }

    /**
     * Save or update a document in Firestore.
     *
     * @param collectionName the name of the collection
     * @param documentId     the ID of the document
     * @param data           the data to be saved or updated
     * @throws Exception if an error occurs during the operation
     */
    public void saveOrUpdate(String collectionName, String documentId, Object data) throws Exception {
        Firestore firestore = getFirestore();
        ApiFuture<WriteResult> writeResult =
                firestore.collection(collectionName).document(documentId).set(data);
        writeResult.get();
    }

    /**
     * Add a new document to Firestore.
     *
     * @param collectionName the name of the collection
     * @param data           the data to be added
     * @return the ID of the newly created document
     * @throws Exception if an error occurs during the operation
     */
    public String addDocument(String collectionName, Object data) throws Exception {
        Firestore firestore = getFirestore();
        ApiFuture<DocumentReference> addedDocRef =
                firestore.collection(collectionName).add(data);
        return addedDocRef.get().getId();
    }

    /**
     * Update specific fields of a document in Firestore.
     *
     * @param collectionName the name of the collection
     * @param documentId     the ID of the document
     * @param data           a map of fields to be updated
     * @throws Exception if an error occurs during the operation
     */
    public void updateDocumentFields(String collectionName, String documentId, Map<String, Object> data)
            throws Exception {
        Firestore firestore = getFirestore();
        ApiFuture<WriteResult> writeResult =
                firestore.collection(collectionName).document(documentId).update(data);
        writeResult.get();
    }

    /**
     * Delete a document from Firestore.
     *
     * @param collectionName the name of the collection
     * @param documentId     the ID of the document
     * @throws Exception if an error occurs during the operation
     */
    public void deleteDocument(String collectionName, String documentId) throws Exception {
        Firestore firestore = getFirestore();
        ApiFuture<WriteResult> writeResult =
                firestore.collection(collectionName).document(documentId).delete();
        writeResult.get();
    }

    /**
     * Delete a document from Firestore and return its data before deletion.
     *
     * @param collectionName the name of the collection
     * @param documentId     the ID of the document
     * @return a map containing the document's data before deletion
     * @throws Exception if an error occurs during the operation
     */
    public Map<String, Object> deleteDocumentReturn(String collectionName, String documentId) throws Exception {
        Firestore db = getFirestore();
        DocumentReference docRef = db.collection(collectionName).document(documentId);

        ApiFuture<DocumentSnapshot> getFuture = docRef.get();
        DocumentSnapshot document = getFuture.get(firestoreQueryTimeout, TimeUnit.SECONDS);

        if (!document.exists()) {
            throw new RuntimeException("Document not found");
        }

        Map<String, Object> documentData = document.getData();

        ApiFuture<WriteResult> deleteFuture = docRef.delete();
        deleteFuture.get();
        return documentData;
    }

    /**
     * Delete a document atomically within a transaction and return its data before deletion.
     *
     * @param collection the name of the collection
     * @param documentId the ID of the document
     * @return a map containing the document's data before deletion
     * @throws ExecutionException   if an error occurs during the operation
     * @throws InterruptedException if the operation is interrupted
     */
    public Map<String, Object> deleteDocumentAtomically(String collection, String documentId)
            throws ExecutionException, InterruptedException {
        Firestore db = getFirestore();

        ApiFuture<Map<String, Object>> future = db.runTransaction(transaction -> {
            DocumentReference docRef = db.collection(collection).document(documentId);

            // Read the document within transaction
            DocumentSnapshot snapshot = transaction.get(docRef).get();

            if (!snapshot.exists()) {
                throw new RuntimeException("Document not found");
            }

            // Get the data
            Map<String, Object> data = snapshot.getData();

            // Delete within the same transaction
            transaction.delete(docRef);
            return data;
        });
        return future.get();
    }

    /**
     * Retrieve multiple documents from Firestore.
     *
     * @param collection  the name of the collection
     * @param documentIds a collection of document IDs
     * @return a list of maps, each containing a document's data
     * @throws Exception if an error occurs during the operation
     */
    public List<Map<String, Object>> getDocuments(String collection, Collection<String> documentIds) throws Exception {
        Firestore db = getFirestore();

        ApiFuture<List<DocumentSnapshot>> future = db.getAll(documentIds.stream()
                .map(id -> db.collection(collection).document(id))
                .toArray(DocumentReference[]::new));
        List<DocumentSnapshot> documents = future.get(firestoreQueryTimeout, TimeUnit.SECONDS);

        return documents.stream()
                .filter(DocumentSnapshot::exists)
                .map(DocumentSnapshot::getData)
                .collect(Collectors.toList());
    }

    /**
     * Retrieve multiple documents from Firestore and map them to a specific class type.
     *
     * @param collection  the name of the collection
     * @param documentIds a collection of document IDs
     * @param valueType   the class type to map the documents to
     * @param <T>         the type parameter
     * @return a list of instances of the specified class type containing the documents' data
     * @throws Exception if an error occurs during the operation
     */
    public <T> List<T> getDocuments(String collection, Collection<String> documentIds, Class<T> valueType)
            throws Exception {
        Firestore db = getFirestore();

        ApiFuture<List<DocumentSnapshot>> future = db.getAll(documentIds.stream()
                .map(id -> db.collection(collection).document(id))
                .toArray(DocumentReference[]::new));
        List<DocumentSnapshot> documents = future.get(firestoreQueryTimeout, TimeUnit.SECONDS);

        return documents.stream()
                .filter(DocumentSnapshot::exists)
                .map(doc -> doc.toObject(valueType))
                .collect(Collectors.toList());
    }

    /**
     * Retrieve a document from Firestore.
     *
     * @param collection the name of the collection
     * @param documentId the ID of the document
     * @return a map containing the document's data
     * @throws Exception if an error occurs during the operation
     */
    public Map<String, Object> getDocument(String collection, String documentId) throws Exception {
        List<Map<String, Object>> documents = getDocuments(collection, List.of(documentId));
        if (documents.isEmpty()) {
            throw new RuntimeException("Document not found");
        } else {
            return documents.get(0);
        }
    }

    /**
     * Retrieve a document from Firestore and map it to a specific class type.
     *
     * @param collection the name of the collection
     * @param documentId the ID of the document
     * @param valueType  the class type to map the document to
     * @param <T>        the type parameter
     * @return an instance of the specified class type containing the document's data
     * @throws Exception if an error occurs during the operation
     */
    public <T> T getDocument(String collection, String documentId, Class<T> valueType) throws Exception {
        List<T> documents = getDocuments(collection, List.of(documentId), valueType);
        if (documents.isEmpty()) {
            throw new RuntimeException("Document not found");
        } else {
            return documents.get(0);
        }
    }

    /**
     * List documents from a Firestore collection with pagination.
     *
     * @param collectionName the name of the collection
     * @param fields         the fields to retrieve, you can pass null or empty list to get all fields
     * @param limit          the maximum number of documents to retrieve
     * @param offSet         the number of documents to skip, use negative value for no offset
     * @return a list of maps, each containing a document's data
     * @throws Exception if an error occurs during the operation
     */
    public List<Map<String, Object>> listDocuments(
            String collectionName, Collection<String> fields, int limit, int offSet) throws Exception {
        Firestore db = getFirestore();
        CollectionReference collectionRef = db.collection(collectionName);
        Query query = collectionRef.limit(limit);

        if (Is.nonNullNonEmpty(fields)) {
            query = query.select(fields.toArray(new String[0]));
        }

        if (offSet > 0) {
            query = query.offset(offSet);
        }

        ApiFuture<QuerySnapshot> querySnapshotFuture = query.get();
        QuerySnapshot querySnapshot = querySnapshotFuture.get(firestoreQueryTimeout, TimeUnit.SECONDS);

        return querySnapshot.getDocuments().stream()
                .map(DocumentSnapshot::getData)
                .collect(Collectors.toList());
    }

    /**
     * List documents from a Firestore collection with default pagination.<br/>
     * DEFAULT PAGE SIZE is used and no offset. Yuu can override page size using firebase.firestore.pageSize property.
     *
     * @param collectionName the name of the collection
     * @param fields         the fields to retrieve, you can pass null or empty list to get all fields
     * @return a list of maps, each containing a document's data
     * @throws Exception if an error occurs during the operation
     */
    public List<Map<String, Object>> listDocuments(String collectionName, Collection<String> fields) throws Exception {
        return listDocuments(collectionName, fields, firestorePageSize, -1);
    }

    /**
     * Check if a document exists in Firestore.
     *
     * @param collection the name of the collection
     * @param documentId the ID of the document
     * @return true if the document exists, false otherwise
     * @throws Exception if an error occurs during the operation
     */
    public boolean documentExists(String collection, String documentId) throws Exception {
        Firestore db = getFirestore();
        DocumentReference docRef = db.collection(collection).document(documentId);

        // Add timeout to prevent indefinite waiting
        ApiFuture<DocumentSnapshot> future = docRef.get(FieldMask.of(new String[0]));
        DocumentSnapshot document = future.get(firestoreQueryTimeout, TimeUnit.SECONDS);
        return document.exists();
    }

    /**
     * Count the number of documents in a collection.
     *
     * @param collection the name of the collection
     * @return the count of documents
     * @throws Exception if an error occurs during the operation
     */
    public long countDocuments(String collection) throws Exception {
        Firestore db = getFirestore();

        AggregateQuerySnapshot snapshot =
                db.collection(collection).count().get().get(firestoreQueryTimeout, TimeUnit.SECONDS);

        return snapshot.getCount();
    }

    /**
     * Increment a numeric field in a document.
     *
     * @param collection the name of the collection
     * @param documentId the ID of the document
     * @param field      the field to increment
     * @param value      the value to increment by
     * @throws Exception if an error occurs during the operation
     */
    public void incrementField(String collection, String documentId, String field, long value) throws Exception {
        Firestore db = getFirestore();
        DocumentReference docRef = db.collection(collection).document(documentId);
        ApiFuture<WriteResult> future = docRef.update(field, FieldValue.increment(value));
        future.get();
    }

    /**
     * Add an element to an array field in a document.
     *
     * @param collection the name of the collection
     * @param documentId the ID of the document
     * @param field      the array field name
     * @param values     the values to add to the array
     * @throws Exception if an error occurs during the operation
     */
    public void arrayUnion(String collection, String documentId, String field, Object... values) throws Exception {
        Firestore db = getFirestore();
        DocumentReference docRef = db.collection(collection).document(documentId);
        ApiFuture<WriteResult> future = docRef.update(field, FieldValue.arrayUnion(values));
        future.get();
    }

    /**
     * Remove elements from an array field in a document.
     *
     * @param collection the name of the collection
     * @param documentId the ID of the document
     * @param field      the array field name
     * @param values     the values to remove from the array
     * @throws Exception if an error occurs during the operation
     */
    public void arrayRemove(String collection, String documentId, String field, Object... values) throws Exception {
        Firestore db = getFirestore();
        DocumentReference docRef = db.collection(collection).document(documentId);
        ApiFuture<WriteResult> future = docRef.update(field, FieldValue.arrayRemove(values));
        future.get();
    }

    /**
     * Query documents from a Firestore collection using a custom query builder.
     *
     * @param collectionName the name of the collection
     * @param queryBuilder   a function that builds the query
     * @return a list of maps, each containing a document's data
     * @throws Exception if an error occurs during the operation
     */
    public List<Map<String, Object>> queryDocuments(
            String collectionName, Function<CollectionReference, Query> queryBuilder) throws Exception {
        Firestore db = getFirestore();
        CollectionReference collectionRef = db.collection(collectionName);
        Query query = queryBuilder.apply(collectionRef);

        ApiFuture<QuerySnapshot> querySnapshotFuture = query.get();
        QuerySnapshot querySnapshot = querySnapshotFuture.get(firestoreQueryTimeout, TimeUnit.SECONDS);
        return querySnapshot.getDocuments().stream()
                .map(DocumentSnapshot::getData)
                .collect(Collectors.toList());
    }

    /**
     * Query documents from a Firestore collection using a custom query builder and map them to a specific class type.
     *
     * @param collectionName the name of the collection
     * @param valueType      the class type to map the documents to
     * @param queryBuilder   a function that builds the query
     * @param <T>            the type parameter
     * @return a list of instances of the specified class type containing the documents' data
     * @throws Exception if an error occurs during the operation
     */
    public <T> List<T> queryDocuments(
            String collectionName, Class<T> valueType, Function<CollectionReference, Query> queryBuilder)
            throws Exception {
        Firestore db = getFirestore();
        CollectionReference collectionRef = db.collection(collectionName);
        Query query = queryBuilder.apply(collectionRef);

        ApiFuture<QuerySnapshot> querySnapshotFuture = query.get();
        QuerySnapshot querySnapshot = querySnapshotFuture.get(firestoreQueryTimeout, TimeUnit.SECONDS);
        return querySnapshot.getDocuments().stream()
                .map(doc -> doc.toObject(valueType))
                .collect(Collectors.toList());
    }
}
