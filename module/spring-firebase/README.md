# Spring Firebase Module

A comprehensive Spring Boot module for Firebase integration, providing seamless access to Firebase Realtime Database and Cloud Firestore with zero-configuration setup.

> **✨ Spring Boot 2.7.x & 3.x Compatible** | **☕ Java 11+ (Spring Boot 2) / Java 17+ (Spring Boot 3)**

## Features

- 🚀 **Spring Boot Auto-Configuration** - Zero setup, works out of the box
- 🔥 **Firebase Realtime Database** - Full CRUD operations with real-time listeners
- ☁️ **Cloud Firestore** - Complete NoSQL document database operations
- 🔐 **Flexible Authentication** - Multiple credential configuration options
- ⚡ **Async Operations** - Non-blocking operations with CompletableFuture
- 📊 **Advanced Queries** - Rich query support with conditions and pagination
- 🔄 **Transactions** - Atomic operations for data consistency
- 🎯 **Type-Safe** - Generic methods with automatic type conversion
- 📝 **Batch Operations** - Efficient multi-document operations
- 🌐 **Real-Time Updates** - Value and child event listeners for live data
- ⏱️ **Configurable Timeouts** - Customizable query timeout settings
- 🛡️ **Error Handling** - Comprehensive exception handling

## Installation

### Gradle

```gradle
dependencies {
    implementation 'com.javaquery:spring-firebase:1.0.1'
    implementation 'com.google.firebase:firebase-admin:9.4.3'
}
```

### Maven

```xml
<dependencies>
    <dependency>
        <groupId>com.javaquery</groupId>
        <artifactId>spring-firebase</artifactId>
        <version>1.0.1</version>
    </dependency>
    <dependency>
        <groupId>com.google.firebase</groupId>
        <artifactId>firebase-admin</artifactId>
        <version>9.4.3</version>
    </dependency>
</dependencies>
```

> **✨ Auto-Configuration**: This module uses Spring Boot auto-configuration. Simply add the dependency and configure credentials - `FirebaseService` and `FirestoreService` will be automatically available for injection!

> **🚀 Spring Boot 3 Compatible**: This module works seamlessly with Spring Boot 3.x (including 3.5.7). No code changes needed - same API works for both Spring Boot 2.x and 3.x!

## Configuration

### application.yml

#### Option 1: Credentials from File (Classpath)

```yaml
firebase:
  credentials:
    file: classpath:firebase-credentials.json
  
  # Realtime Database URL (required for Realtime Database)
  database:
    url: https://your-project.firebaseio.com
    queryTimeout: 10  # seconds (default: 10)
  
  # Firestore settings
  firestore:
    queryTimeout: 10  # seconds (default: 10)
    pageSize: 50      # default: 50
```

#### Option 2: Credentials from File (Filesystem)

```yaml
firebase:
  credentials:
    file: /path/to/firebase-credentials.json
  database:
    url: https://your-project.firebaseio.com
```

#### Option 3: Credentials from String (Environment Variable)

```yaml
firebase:
  credentials:
    string: ${FIREBASE_CREDENTIALS_JSON}
  database:
    url: https://your-project.firebaseio.com
```

#### Option 4: Application Default Credentials

```yaml
# No credentials configuration needed
# Uses Google Application Default Credentials (ADC)
firebase:
  database:
    url: https://your-project.firebaseio.com
```

### application.properties

```properties
# Credentials (choose one option)
firebase.credentials.file=classpath:firebase-credentials.json
# OR
firebase.credentials.string=${FIREBASE_CREDENTIALS_JSON}

# Realtime Database
firebase.database.url=https://your-project.firebaseio.com
firebase.database.queryTimeout=10

# Firestore
firebase.firestore.queryTimeout=10
firebase.firestore.pageSize=50
```

### Getting Firebase Credentials

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project
3. Go to Project Settings → Service Accounts
4. Click "Generate New Private Key"
5. Save the JSON file securely

## Quick Start

### Basic Usage - Realtime Database

```java
import com.javaquery.spring.firebase.FirebaseService;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    
    private final FirebaseService firebaseService;
    
    public UserService(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }
    
    public void saveUser(String userId, User user) throws Exception {
        firebaseService.setValue("users/" + userId, user);
    }
    
    public User getUser(String userId) throws Exception {
        return firebaseService.getValue("users/" + userId, User.class);
    }
}
```

### Basic Usage - Firestore

```java
import com.javaquery.spring.firebase.FirestoreService;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    
    private final FirestoreService firestoreService;
    
    public ProductService(FirestoreService firestoreService) {
        this.firestoreService = firestoreService;
    }
    
    public void saveProduct(String productId, Product product) throws Exception {
        firestoreService.saveOrUpdate("products", productId, product);
    }
    
    public Product getProduct(String productId) throws Exception {
        return firestoreService.getById("products", productId, Product.class);
    }
}
```

## Examples

### Firebase Realtime Database Examples

#### 1. Save/Update Data

```java
// Save a simple value
firebaseService.setValue("users/user123/name", "John Doe");

// Save an object
User user = new User("John Doe", "john@example.com", 30);
firebaseService.setValue("users/user123", user);

// Save with map
Map<String, Object> userData = new HashMap<>();
userData.put("name", "John Doe");
userData.put("email", "john@example.com");
userData.put("age", 30);
firebaseService.setValue("users/user123", userData);
```

#### 2. Update Specific Fields

```java
Map<String, Object> updates = new HashMap<>();
updates.put("age", 31);
updates.put("lastLogin", System.currentTimeMillis());

firebaseService.updateChildren("users/user123", updates);
```

#### 3. Push with Auto-Generated Key

```java
Post post = new Post("My First Post", "This is the content");
String newKey = firebaseService.push("posts", post);
System.out.println("New post created with key: " + newKey);
```

#### 4. Read Data

```java
// Get value as specific type
User user = firebaseService.getValue("users/user123", User.class);

// Get value as Map
Map<String, Object> userData = firebaseService.getValueAsMap("users/user123");

// Check if exists
boolean exists = firebaseService.exists("users/user123");
```

#### 5. Get Child Nodes

```java
// Get all children as List of Maps
List<Map<String, Object>> posts = firebaseService.getChildren("posts");

// Get all children as List of specific type
List<Post> posts = firebaseService.getChildren("posts", Post.class);
```

#### 6. Query with Limits

```java
// Get first 5 items
DataSnapshot snapshot = firebaseService.limitToFirst("posts", 5);

// Get last 10 items
DataSnapshot snapshot = firebaseService.limitToLast("posts", 10);
```

#### 7. Advanced Queries

```java
// Query with conditions
DataSnapshot snapshot = firebaseService.query(
    "users",
    query -> query.orderByChild("age")
                  .startAt(18)
                  .endAt(65)
                  .limitToFirst(100)
);

// Process results
for (DataSnapshot child : snapshot.getChildren()) {
    User user = child.getValue(User.class);
    System.out.println(user.getName());
}
```

#### 8. Delete Data

```java
// Simple delete
firebaseService.delete("users/user123");

// Delete and return deleted data
DataSnapshot deletedData = firebaseService.deleteAndReturn("users/user123");
if (deletedData.exists()) {
    User deletedUser = deletedData.getValue(User.class);
    System.out.println("Deleted: " + deletedUser.getName());
}
```

#### 9. Increment Values

```java
// Increment by 1 (default)
firebaseService.incrementValue("posts/post123/views");

// Increment by specific amount
firebaseService.incrementValue("users/user123/score", 10);
```

#### 10. Server Timestamp

```java
// Set server timestamp
firebaseService.setServerTimestamp("users/user123/lastSeen");
```

#### 11. Real-Time Listeners

```java
// Value event listener
ValueEventListener valueListener = firebaseService.addValueEventListener(
    "users/user123",
    new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot snapshot) {
            User user = snapshot.getValue(User.class);
            System.out.println("User updated: " + user.getName());
        }
        
        @Override
        public void onCancelled(DatabaseError error) {
            System.err.println("Error: " + error.getMessage());
        }
    }
);

// Remove listener when done
firebaseService.removeValueEventListener("users/user123", valueListener);
```

#### 12. Child Event Listener

```java
ChildEventListener childListener = firebaseService.addChildEventListener(
    "posts",
    new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
            Post post = snapshot.getValue(Post.class);
            System.out.println("New post: " + post.getTitle());
        }
        
        @Override
        public void onChildChanged(DataSnapshot snapshot, String previousChildName) {
            Post post = snapshot.getValue(Post.class);
            System.out.println("Post updated: " + post.getTitle());
        }
        
        @Override
        public void onChildRemoved(DataSnapshot snapshot) {
            System.out.println("Post deleted");
        }
        
        @Override
        public void onChildMoved(DataSnapshot snapshot, String previousChildName) {
            System.out.println("Post moved");
        }
        
        @Override
        public void onCancelled(DatabaseError error) {
            System.err.println("Error: " + error.getMessage());
        }
    }
);
```

### Cloud Firestore Examples

#### 1. Save/Update Document

```java
// Save with specific ID
Product product = new Product("Laptop", 999.99, "Electronics");
firestoreService.saveOrUpdate("products", "product123", product);

// Save with map
Map<String, Object> productData = new HashMap<>();
productData.put("name", "Laptop");
productData.put("price", 999.99);
productData.put("category", "Electronics");
firestoreService.saveOrUpdate("products", "product123", productData);
```

#### 2. Add Document (Auto-Generated ID)

```java
Product product = new Product("Mouse", 29.99, "Accessories");
String docId = firestoreService.addDocument("products", product);
System.out.println("Created document: " + docId);
```

#### 3. Update Specific Fields

```java
Map<String, Object> updates = new HashMap<>();
updates.put("price", 899.99);
updates.put("lastUpdated", System.currentTimeMillis());

firestoreService.updateDocumentFields("products", "product123", updates);
```

#### 4. Get Document

```java
// Get as specific type
Product product = firestoreService.getById("products", "product123", Product.class);

// Get as map
Map<String, Object> productData = firestoreService.getByIdAsMap("products", "product123");

// Check if exists
boolean exists = firestoreService.exists("products", "product123");
```

#### 5. Query Documents

```java
// Get all documents
List<Product> allProducts = firestoreService.getAll("products", Product.class);

// Query by field
List<Product> electronics = firestoreService.getByField(
    "products",
    "category",
    "Electronics",
    Product.class
);

// Get as maps
List<Map<String, Object>> products = firestoreService.getAllAsMap("products");
```

#### 6. Paginated Retrieval

```java
// Get first page (uses configured pageSize)
List<Product> page1 = firestoreService.getAllPaginated(
    "products",
    null,  // no start document for first page
    Product.class
);

// Get next page (pass last document from previous page)
DocumentSnapshot lastDoc = /* last document from page1 */;
List<Product> page2 = firestoreService.getAllPaginated(
    "products",
    lastDoc,
    Product.class
);
```

#### 7. Complex Queries

```java
// Query with multiple conditions
List<Product> results = firestoreService.queryWithConditions(
    "products",
    query -> query
        .whereEqualTo("category", "Electronics")
        .whereGreaterThan("price", 500.0)
        .whereLessThan("price", 1500.0)
        .orderBy("price")
        .limit(10),
    Product.class
);
```

#### 8. Transactions

```java
// Run atomic transaction
String result = firestoreService.runTransaction(transaction -> {
    DocumentReference docRef = firestoreService.getFirestore()
        .collection("accounts")
        .document("account123");
    
    DocumentSnapshot snapshot = transaction.get(docRef).get();
    double currentBalance = snapshot.getDouble("balance");
    double newBalance = currentBalance + 100.0;
    
    transaction.update(docRef, "balance", newBalance);
    return "New balance: " + newBalance;
});

System.out.println(result);
```

#### 9. Batch Operations

```java
// Save multiple documents
List<Product> products = Arrays.asList(
    new Product("Item1", 10.0, "Category1"),
    new Product("Item2", 20.0, "Category2"),
    new Product("Item3", 30.0, "Category3")
);

firestoreService.saveAll("products", products, product -> product.getId());

// Delete multiple documents
List<String> docIds = Arrays.asList("doc1", "doc2", "doc3");
firestoreService.deleteAll("products", docIds);
```

#### 10. Delete Document

```java
// Simple delete
firestoreService.deleteDocument("products", "product123");

// Delete entire collection (all documents)
firestoreService.deleteAll("products");
```

#### 11. Count Documents

```java
// Count all documents
long totalProducts = firestoreService.count("products");

// Count with condition
long electronicsCount = firestoreService.count("products", "category", "Electronics");
```

## Advanced Usage

### Custom Transformations

```java
// Transform Firestore results
List<String> productNames = firestoreService.getAll(
    "products",
    snapshot -> snapshot.getString("name")
);

// Transform with complex logic
List<ProductSummary> summaries = firestoreService.getAll(
    "products",
    snapshot -> new ProductSummary(
        snapshot.getId(),
        snapshot.getString("name"),
        snapshot.getDouble("price")
    )
);
```

### Real-Time Dashboard

```java
@Service
public class DashboardService {
    
    private final FirebaseService firebaseService;
    
    public void setupRealTimeDashboard() {
        firebaseService.addValueEventListener("stats/sales", new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Map<String, Object> stats = (Map<String, Object>) snapshot.getValue();
                updateDashboard(stats);
            }
            
            @Override
            public void onCancelled(DatabaseError error) {
                log.error("Dashboard update failed", error.toException());
            }
        });
    }
}
```

### Multi-Tenant Architecture

```java
@Service
public class TenantService {
    
    private final FirestoreService firestoreService;
    
    public void saveTenantData(String tenantId, String collection, Object data) throws Exception {
        String tenantPath = "tenants/" + tenantId + "/" + collection;
        firestoreService.addDocument(tenantPath, data);
    }
    
    public <T> List<T> getTenantData(String tenantId, String collection, Class<T> type) 
            throws Exception {
        String tenantPath = "tenants/" + tenantId + "/" + collection;
        return firestoreService.getAll(tenantPath, type);
    }
}
```

## API Reference

### FirebaseService Methods

#### Write Operations

| Method | Parameters | Return | Description |
|--------|-----------|--------|-------------|
| `setValue` | `path, data` | `void` | Save or update data at path |
| `updateChildren` | `path, updates` | `void` | Update specific fields |
| `push` | `path, data` | `String` | Add with auto-generated key |

#### Read Operations

| Method | Parameters | Return | Description |
|--------|-----------|--------|-------------|
| `getValue` | `path` | `DataSnapshot` | Get data snapshot |
| `getValue` | `path, type` | `T` | Get data as specific type |
| `getValueAsMap` | `path` | `Map<String, Object>` | Get data as map |
| `getChildren` | `path` | `List<Map<String, Object>>` | Get all children as maps |
| `getChildren` | `path, type` | `List<T>` | Get all children as type |
| `exists` | `path` | `boolean` | Check if path exists |

#### Query Operations

| Method | Parameters | Return | Description |
|--------|-----------|--------|-------------|
| `limitToFirst` | `path, limit` | `DataSnapshot` | Query first N items |
| `limitToLast` | `path, limit` | `DataSnapshot` | Query last N items |
| `query` | `path, function` | `DataSnapshot` | Custom query with conditions |

#### Delete Operations

| Method | Parameters | Return | Description |
|--------|-----------|--------|-------------|
| `delete` | `path` | `void` | Delete data at path |
| `deleteAndReturn` | `path` | `DataSnapshot` | Delete and return data |

#### Advanced Operations

| Method | Parameters | Return | Description |
|--------|-----------|--------|-------------|
| `incrementValue` | `path, amount` | `void` | Atomic increment |
| `setServerTimestamp` | `path` | `void` | Set server timestamp |
| `addValueEventListener` | `path, listener` | `ValueEventListener` | Add real-time listener |
| `addChildEventListener` | `path, listener` | `ChildEventListener` | Add child listener |
| `removeValueEventListener` | `path, listener` | `void` | Remove listener |
| `removeChildEventListener` | `path, listener` | `void` | Remove listener |

### FirestoreService Methods

#### Write Operations

| Method | Parameters | Return | Description |
|--------|-----------|--------|-------------|
| `saveOrUpdate` | `collection, docId, data` | `void` | Save or update document |
| `addDocument` | `collection, data` | `String` | Add with auto-generated ID |
| `updateDocumentFields` | `collection, docId, fields` | `void` | Update specific fields |
| `saveAll` | `collection, items, idExtractor` | `void` | Batch save documents |

#### Read Operations

| Method | Parameters | Return | Description |
|--------|-----------|--------|-------------|
| `getById` | `collection, docId, type` | `T` | Get document by ID |
| `getByIdAsMap` | `collection, docId` | `Map<String, Object>` | Get as map |
| `getByField` | `collection, field, value, type` | `List<T>` | Query by field |
| `getAll` | `collection, type` | `List<T>` | Get all documents |
| `getAllAsMap` | `collection` | `List<Map<String, Object>>` | Get all as maps |
| `getAllPaginated` | `collection, start, type` | `List<T>` | Get paginated results |
| `exists` | `collection, docId` | `boolean` | Check if document exists |
| `count` | `collection` | `long` | Count all documents |
| `count` | `collection, field, value` | `long` | Count with condition |

#### Query Operations

| Method | Parameters | Return | Description |
|--------|-----------|--------|-------------|
| `queryWithConditions` | `collection, function, type` | `List<T>` | Complex queries |
| `getAll` | `collection, transformer` | `List<R>` | Get with transformation |

#### Delete Operations

| Method | Parameters | Return | Description |
|--------|-----------|--------|-------------|
| `deleteDocument` | `collection, docId` | `void` | Delete document |
| `deleteAll` | `collection, docIds` | `void` | Batch delete |
| `deleteAll` | `collection` | `void` | Delete all in collection |

#### Transaction Operations

| Method | Parameters | Return | Description |
|--------|-----------|--------|-------------|
| `runTransaction` | `function` | `R` | Run atomic transaction |

### Configuration Properties

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `firebase.credentials.file` | `String` | - | Path to credentials JSON file |
| `firebase.credentials.string` | `String` | - | Credentials as JSON string |
| `firebase.database.url` | `String` | - | Realtime Database URL |
| `firebase.database.queryTimeout` | `int` | `10` | Query timeout (seconds) |
| `firebase.firestore.queryTimeout` | `int` | `10` | Firestore query timeout (seconds) |
| `firebase.firestore.pageSize` | `int` | `50` | Default page size for pagination |

## Best Practices

### 1. Use Specific Types

```java
// ✅ Good - Type-safe
User user = firebaseService.getValue("users/123", User.class);

// ❌ Avoid - Requires casting
Map<String, Object> userData = firebaseService.getValueAsMap("users/123");
User user = new User(userData);
```

### 2. Handle Exceptions

```java
try {
    firebaseService.setValue("users/123", user);
} catch (Exception e) {
    log.error("Failed to save user", e);
    // Implement retry logic or error notification
}
```

### 3. Clean Up Listeners

```java
ValueEventListener listener = firebaseService.addValueEventListener(path, handler);

// Always remove when done
@PreDestroy
public void cleanup() {
    firebaseService.removeValueEventListener(path, listener);
}
```

### 4. Use Transactions for Atomic Operations

```java
// ✅ Good - Atomic
firestoreService.runTransaction(transaction -> {
    // Read, modify, write atomically
    return result;
});

// ❌ Avoid - Race conditions possible
double balance = getBalance();
updateBalance(balance + 100);
```

### 5. Batch Operations

```java
// ✅ Good - Single batch operation
firestoreService.saveAll("items", items, Item::getId);

// ❌ Avoid - Multiple separate calls
for (Item item : items) {
    firestoreService.saveOrUpdate("items", item.getId(), item);
}
```

### 6. Use Pagination for Large Collections

```java
// ✅ Good - Paginated
List<Product> page = firestoreService.getAllPaginated("products", lastDoc, Product.class);

// ❌ Avoid - Load everything
List<Product> all = firestoreService.getAll("products", Product.class);
```

## Security Considerations

### 1. Secure Credentials

```yaml
# ✅ Use environment variables
firebase:
  credentials:
    string: ${FIREBASE_CREDENTIALS_JSON}

# ❌ Never commit credentials
firebase:
  credentials:
    file: /hardcoded/path/credentials.json
```

### 2. Firebase Security Rules

Configure security rules in Firebase Console:

```javascript
// Realtime Database Rules
{
  "rules": {
    "users": {
      "$uid": {
        ".read": "$uid === auth.uid",
        ".write": "$uid === auth.uid"
      }
    }
  }
}

// Firestore Rules
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

### 3. Validate Input

```java
public void saveUser(User user) throws Exception {
    if (user == null || user.getId() == null) {
        throw new IllegalArgumentException("Invalid user data");
    }
    firebaseService.setValue("users/" + user.getId(), user);
}
```

## Troubleshooting

### Issue: "FirebaseApp initialization failed"

**Solution**: Check credentials configuration and ensure Firebase Admin SDK is on classpath.

```yaml
firebase:
  credentials:
    file: classpath:firebase-credentials.json  # Verify file exists
```

### Issue: "Database URL not configured"

**Solution**: Add database URL to configuration (required for Realtime Database).

```yaml
firebase:
  database:
    url: https://your-project.firebaseio.com
```

### Issue: "Timeout waiting for response"

**Solution**: Increase query timeout in configuration.

```yaml
firebase:
  database:
    queryTimeout: 30  # Increase timeout
```

### Issue: "Document not found"

**Solution**: Check if document exists before accessing.

```java
if (firestoreService.exists("collection", "docId")) {
    Product product = firestoreService.getById("collection", "docId", Product.class);
}
```

## Testing

### Unit Testing

```java
@SpringBootTest
class ProductServiceTest {
    
    @MockBean
    private FirestoreService firestoreService;
    
    @Autowired
    private ProductService productService;
    
    @Test
    void testSaveProduct() throws Exception {
        Product product = new Product("Test", 99.99, "Test");
        
        doNothing().when(firestoreService)
            .saveOrUpdate(anyString(), anyString(), any());
        
        productService.saveProduct("test123", product);
        
        verify(firestoreService, times(1))
            .saveOrUpdate("products", "test123", product);
    }
}
```

### Integration Testing

For integration tests, use Firebase Emulator Suite:

```bash
# Install Firebase Tools
npm install -g firebase-tools

# Start emulators
firebase emulators:start
```

```yaml
# application-test.yml
firebase:
  credentials:
    string: '{"type":"service_account","project_id":"test"}'
  database:
    url: http://localhost:9000/?ns=test-project  # Emulator URL
```

## Migration Guide

### From Manual Configuration

**Before:**
```java
@SpringBootApplication
@ComponentScan(basePackages = {"com.myapp", "com.javaquery.spring.firebase"})
public class MyApplication {
    // ...
}
```

**After:**
```java
@SpringBootApplication
public class MyApplication {
    // Auto-configuration handles everything!
}
```

## Version Compatibility

| Module Version | Spring Boot | Java | Firebase Admin SDK | Status |
|----------------|-------------|------|--------------------|--------|
| 1.0.1 | 3.0.x - 3.5.x | 17+ | 9.4.3+ | ✅ Stable |
| 1.0.1 | 2.7.x | 11+ | 9.4.3+ | ✅ Stable |
| 1.0.0 | 2.7.x | 11+ | 9.4.3 | ✅ Stable |

## Dependencies

- `spring-boot-autoconfigure` - Auto-configuration support
- `com.google.firebase:firebase-admin` - Firebase Admin SDK
- `javaquery:util` - Utility functions

## Contributing

Contributions are welcome! Please ensure:
- Code follows existing style conventions
- All tests pass
- New features include tests and documentation

## License

This project is licensed under the same license as the JLite project.

## Support

For issues, questions, or contributions, please visit the [JLite GitHub repository](https://github.com/javaquery/JLite).

## Related Modules

- [spring-email](../spring-email) - Email sending with auto-configuration
- [spring-aws-s3](../spring-aws-s3) - AWS S3 integration
- [spring-aws-cognito](../spring-aws-cognito) - AWS Cognito integration

---

**Built with ❤️ by the JLite Team**
