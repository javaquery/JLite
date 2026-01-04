package com.javaquery.spring.firebase;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.springframework.stereotype.Service;

/**
 * @author vicky.thakor
 * @since 1.0.0
 */
@Service
public class FirestoreService {

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
        DocumentSnapshot document = getFuture.get();

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
     * Retrieve a document from Firestore.
     *
     * @param collection the name of the collection
     * @param documentId the ID of the document
     * @return a map containing the document's data
     * @throws Exception if an error occurs during the operation
     */
    public Map<String, Object> getDocument(String collection, String documentId) throws Exception {
        Firestore db = getFirestore();
        DocumentReference docRef = db.collection(collection).document(documentId);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        if (document.exists()) {
            return document.getData();
        } else {
            throw new RuntimeException("Document not found");
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
        Firestore db = getFirestore();
        DocumentReference docRef = db.collection(collection).document(documentId);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        if (document.exists()) {
            return document.toObject(valueType);
        } else {
            throw new RuntimeException("Document not found");
        }
    }
}
