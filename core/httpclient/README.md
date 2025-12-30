# HTTP Client

A powerful and flexible HTTP client library for Java projects, built on top of Apache HttpComponents. This library provides a clean, fluent API for making HTTP requests with support for retries, OAuth authentication, custom handlers, and comprehensive logging.

## Features

- 🌐 **Full HTTP Support** - GET, POST, PUT, DELETE, and PATCH methods
- 🔄 **Retry Mechanism** - Configurable retry policies with backoff strategies
- 🔐 **Authentication** - Built-in support for Basic Auth and OAuth 1.0
- 📝 **Multiple Payload Types** - JSON, Form data, Multipart, and String entities
- 🎯 **Request/Response Handlers** - Extensible hooks for request and response processing
- 📊 **Structured Logging** - Logstash integration for detailed request/response logging
- ⚙️ **Flexible Configuration** - Custom headers, query parameters, and execution context
- 🛠️ **Error Handling** - Comprehensive exception handling and retry mechanisms

## Installation

### Gradle

```gradle
dependencies {
    implementation 'com.javaquery:httpclient:1.0.7'
}
```

### Maven

```xml
<dependency>
    <groupId>com.javaquery</groupId>
    <artifactId>httpclient</artifactId>
    <version>1.0.7</version>
</dependency>
```

## Quick Start

### Simple GET Request

```java
import com.javaquery.http.*;
import com.javaquery.http.handler.HttpResponseHandler;

// Build the request
HttpRequest httpRequest = new HttpRequest.HttpRequestBuilder("GetUsers", HttpMethod.GET)
    .withHost("https://api.example.com")
    .withEndPoint("/users")
    .build();

// Create execution context
HttpExecutionContext context = new HttpExecutionContext();

// Execute request
HttpClient httpClient = new HttpClient();
httpClient.execute(context, httpRequest, new HttpResponseHandler<String>() {
    @Override
    public String onResponse(HttpResponse httpResponse) {
        return httpResponse.getBody();
    }

    @Override
    public void onMaxRetryAttempted(HttpResponse httpResponse) {
        // Handle max retry scenario
    }
});
```

### POST Request with JSON Payload

```java
String jsonPayload = "{\"name\":\"John Doe\",\"email\":\"john@example.com\"}";

HttpRequest httpRequest = new HttpRequest.HttpRequestBuilder("CreateUser", HttpMethod.POST)
    .withHost("https://api.example.com")
    .withEndPoint("/users")
    .withHeader("Content-Type", "application/json")
    .withHttpPayload(new HttpRequest.HttpPayload(
        StringPool.UTF8, 
        "application/json", 
        jsonPayload
    ))
    .build();

HttpExecutionContext context = new HttpExecutionContext();
HttpClient httpClient = new HttpClient();

httpClient.execute(context, httpRequest, new HttpResponseHandler<JSONObject>() {
    @Override
    public JSONObject onResponse(HttpResponse httpResponse) {
        return httpResponse.getJSONObjectBody();
    }

    @Override
    public void onMaxRetryAttempted(HttpResponse httpResponse) {
        System.err.println("Max retries reached");
    }
});
```

## HTTP Methods

### GET Request

```java
HttpRequest getRequest = new HttpRequest.HttpRequestBuilder("GetRequest", HttpMethod.GET)
    .withHost("https://api.example.com")
    .withEndPoint("/resource")
    .withQueryParameter("page", "1")
    .withQueryParameter("size", "20")
    .build();
```

### POST Request with Form Data

```java
Map<String, Object> formData = new HashMap<>();
formData.put("username", "john.doe");
formData.put("email", "john@example.com");

HttpRequest postRequest = new HttpRequest.HttpRequestBuilder("PostForm", HttpMethod.POST)
    .withHost("https://api.example.com")
    .withEndPoint("/submit")
    .withHttpPayload(new HttpRequest.HttpPayload(
        StringPool.UTF8,
        "application/x-www-form-urlencoded",
        formData
    ))
    .build();
```

### POST Request with Multipart File Upload

```java
Map<String, Object> multipartData = new HashMap<>();
multipartData.put("file", new File("/path/to/file.pdf"));
multipartData.put("description", "Document upload");

HttpRequest uploadRequest = new HttpRequest.HttpRequestBuilder("FileUpload", HttpMethod.POST)
    .withHost("https://api.example.com")
    .withEndPoint("/upload")
    .withHttpPayload(new HttpRequest.HttpPayload(
        StringPool.UTF8,
        "multipart/form-data",
        multipartData
    ))
    .build();
```

### PUT Request

```java
String updatePayload = "{\"status\":\"active\"}";

HttpRequest putRequest = new HttpRequest.HttpRequestBuilder("UpdateUser", HttpMethod.PUT)
    .withHost("https://api.example.com")
    .withEndPoint("/users/123")
    .withHttpPayload(new HttpRequest.HttpPayload(
        StringPool.UTF8,
        "application/json",
        updatePayload
    ))
    .build();
```

### DELETE Request

```java
HttpRequest deleteRequest = new HttpRequest.HttpRequestBuilder("DeleteUser", HttpMethod.DELETE)
    .withHost("https://api.example.com")
    .withEndPoint("/users/123")
    .build();
```

## Authentication

### Basic Authentication

```java
HttpRequest authRequest = new HttpRequest.HttpRequestBuilder("SecureEndpoint", HttpMethod.GET)
    .withHost("https://api.example.com")
    .withEndPoint("/secure")
    .withBasicAuth("username", "password")
    .build();
```

### OAuth 1.0

```java
import com.javaquery.http.oauth.OAuthConfig;

OAuthConfig oauthConfig = OAuthConfig.builder()
    .consumerKey("your-consumer-key")
    .consumerSecret("your-consumer-secret")
    .accessToken("your-access-token")
    .accessTokenSecret("your-access-token-secret")
    .build();

HttpRequest oauthRequest = new HttpRequest.HttpRequestBuilder("OAuthRequest", HttpMethod.GET)
    .withHost("https://api.example.com")
    .withEndPoint("/oauth/resource")
    .build();

// Add OAuth handler to execution context
HttpExecutionContext context = new HttpExecutionContext();
context.addHttpRequestHandler(new OAuth10HttpRequestHandler(oauthConfig));

httpClient.execute(context, oauthRequest, responseHandler);
```

## Headers and Query Parameters

### Adding Headers

```java
// Single header
HttpRequest request = new HttpRequest.HttpRequestBuilder("Request", HttpMethod.GET)
    .withHost("https://api.example.com")
    .withHeader("Authorization", "Bearer token123")
    .withHeader("X-Custom-Header", "value")
    .build();

// Multiple headers
Map<String, String> headers = new HashMap<>();
headers.put("Content-Type", "application/json");
headers.put("Accept", "application/json");

HttpRequest request = new HttpRequest.HttpRequestBuilder("Request", HttpMethod.GET)
    .withHost("https://api.example.com")
    .withHeaders(headers)
    .build();
```

### Adding Query Parameters

```java
// Single parameter
HttpRequest request = new HttpRequest.HttpRequestBuilder("Request", HttpMethod.GET)
    .withHost("https://api.example.com")
    .withEndPoint("/search")
    .withQueryParameter("q", "java")
    .withQueryParameter("limit", "10")
    .build();

// Multiple parameters
Map<String, String> params = new HashMap<>();
params.put("page", "1");
params.put("size", "20");
params.put("sort", "name");

HttpRequest request = new HttpRequest.HttpRequestBuilder("Request", HttpMethod.GET)
    .withHost("https://api.example.com")
    .withEndPoint("/users")
    .withQueryParameter(params)
    .build();
```

## Retry Policies

### Default Retry Policy

```java
import com.javaquery.http.DefaultRetryPolicy;

HttpRequest request = new HttpRequest.HttpRequestBuilder("RetryRequest", HttpMethod.GET)
    .withHost("https://api.example.com")
    .withEndPoint("/unstable")
    .withRetryPolicy(DefaultRetryPolicy.get(3))  // Retry up to 3 times
    .build();
```

### Custom Retry Policy

```java
import com.javaquery.http.retry.*;

RetryPolicy customRetry = new RetryPolicy(
    new DefaultRetryCondition(),      // When to retry
    new DefaultBackoffStrategy(),     // How long to wait between retries
    5                                 // Max retry attempts
);

HttpRequest request = new HttpRequest.HttpRequestBuilder("CustomRetry", HttpMethod.GET)
    .withHost("https://api.example.com")
    .withEndPoint("/resource")
    .withRetryPolicy(customRetry)
    .build();
```

### Implementing Custom Retry Condition

```java
import com.javaquery.http.retry.RetryCondition;

public class CustomRetryCondition implements RetryCondition {
    @Override
    public boolean shouldRetry(HttpRequestResponse httpRequestResponse) {
        HttpResponse response = httpRequestResponse.getHttpResponse();
        
        // Retry on 5xx errors or specific 4xx errors
        int statusCode = response.getStatusCode();
        return statusCode >= 500 || statusCode == 429 || statusCode == 408;
    }
}

RetryPolicy retryPolicy = new RetryPolicy(
    new CustomRetryCondition(),
    new DefaultBackoffStrategy(),
    3
);
```

### Implementing Custom Backoff Strategy

```java
import com.javaquery.http.retry.BackoffStrategy;

public class ExponentialBackoff implements BackoffStrategy {
    @Override
    public long computeDelayBeforeNextRetry(HttpRequestResponse httpRequestResponse, 
                                           int retriesAttempted) {
        // Exponential backoff: 2^attempt * 1000ms
        return (long) Math.pow(2, retriesAttempted) * 1000;
    }
}

RetryPolicy retryPolicy = new RetryPolicy(
    new DefaultRetryCondition(),
    new ExponentialBackoff(),
    5
);
```

## Request and Response Handlers

### Custom Request Handler

```java
import com.javaquery.http.handler.HttpRequestHandler;

public class CustomHeaderHandler implements HttpRequestHandler {
    @Override
    public void onRequest(HttpExecutionContext context, HttpRequest httpRequest) {
        // Add custom headers before each request
        httpRequest.addHeader("X-Request-ID", UUID.randomUUID().toString());
        httpRequest.addHeader("X-Timestamp", String.valueOf(System.currentTimeMillis()));
    }
}

// Use the handler
HttpExecutionContext context = new HttpExecutionContext();
context.addHttpRequestHandler(new CustomHeaderHandler());

httpClient.execute(context, httpRequest, responseHandler);
```

### Response Handler

```java
import com.javaquery.http.handler.HttpResponseHandler;

HttpResponseHandler<User> userHandler = new HttpResponseHandler<User>() {
    @Override
    public User onResponse(HttpResponse httpResponse) {
        if (httpResponse.getStatusCode() == 200) {
            JSONObject json = httpResponse.getJSONObjectBody();
            return parseUser(json);
        } else if (httpResponse.getStatusCode() == 404) {
            throw new UserNotFoundException("User not found");
        } else {
            throw new HttpException("Unexpected status: " + httpResponse.getStatusCode());
        }
    }

    @Override
    public void onMaxRetryAttempted(HttpResponse httpResponse) {
        LOGGER.error("Failed after maximum retry attempts. Status: {}", 
                     httpResponse.getStatusCode());
    }
    
    private User parseUser(JSONObject json) {
        // Parse JSON to User object
        return User.builder()
            .id(json.getLong("id"))
            .name(json.getString("name"))
            .email(json.getString("email"))
            .build();
    }
};
```

## Execution Context

The `HttpExecutionContext` allows you to pass metadata and configure handlers for request execution.

### Using Metadata

```java
HttpExecutionContext context = new HttpExecutionContext();

// Add metadata
Map<String, Object> metadata = new HashMap<>();
metadata.put("userId", "12345");
metadata.put("requestSource", "mobile-app");
context.setMetaData(metadata);

// Add individual metadata
context.addMetaData("correlationId", UUID.randomUUID().toString());

httpClient.execute(context, httpRequest, responseHandler);
```

### Multiple Request Handlers

```java
HttpExecutionContext context = new HttpExecutionContext();

// Add multiple handlers
context.addHttpRequestHandler(new AuthHeaderHandler());
context.addHttpRequestHandler(new LoggingHandler());
context.addHttpRequestHandler(new MetricsHandler());

// Or set all at once
List<HttpRequestHandler> handlers = Arrays.asList(
    new AuthHeaderHandler(),
    new LoggingHandler(),
    new MetricsHandler()
);
context.setHttpRequestHandlers(handlers);

httpClient.execute(context, httpRequest, responseHandler);
```

## Response Processing

### Working with HTTP Response

```java
HttpResponseHandler<Object> handler = new HttpResponseHandler<Object>() {
    @Override
    public Object onResponse(HttpResponse httpResponse) {
        // Get status code
        int statusCode = httpResponse.getStatusCode();
        
        // Get headers
        Map<String, String> headers = httpResponse.getHeaders();
        String contentType = headers.get("Content-Type");
        
        // Get response body as string
        String body = httpResponse.getBody();
        
        // Parse as JSON Object
        JSONObject jsonObject = httpResponse.getJSONObjectBody();
        
        // Parse as JSON Array
        JSONArray jsonArray = httpResponse.getJSONArrayBody();
        
        return body;
    }

    @Override
    public void onMaxRetryAttempted(HttpResponse httpResponse) {
        // Handle max retry scenario
    }
};
```

### JSON Response Parsing

```java
// Parse as JSONObject
JSONObject jsonObject = httpResponse.getJSONObjectBody();
String name = jsonObject.getString("name");
int age = jsonObject.getInt("age");

// Parse as JSONArray
JSONArray jsonArray = httpResponse.getJSONArrayBody();
for (int i = 0; i < jsonArray.length(); i++) {
    JSONObject item = jsonArray.getJSONObject(i);
    // Process each item
}
```

## Advanced Usage

### Complete Example with All Features

```java
import com.javaquery.http.*;
import com.javaquery.http.handler.*;
import com.javaquery.http.retry.*;

public class AdvancedHttpClientExample {
    
    public static void main(String[] args) {
        // Build request with all options
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer token123");
        headers.put("Accept", "application/json");
        
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("page", "1");
        queryParams.put("size", "20");
        
        String payload = "{\"action\":\"update\",\"data\":{\"status\":\"active\"}}";
        
        RetryPolicy retryPolicy = new RetryPolicy(
            new DefaultRetryCondition(),
            new DefaultBackoffStrategy(),
            3
        );
        
        HttpRequest request = new HttpRequest.HttpRequestBuilder("ComplexRequest", HttpMethod.POST)
            .withHost("https://api.example.com")
            .withPort(443)
            .withEndPoint("/api/v1/resources")
            .withHeaders(headers)
            .withQueryParameter(queryParams)
            .withHttpPayload(new HttpRequest.HttpPayload(
                StringPool.UTF8,
                "application/json",
                payload
            ))
            .withRetryPolicy(retryPolicy)
            .build();
        
        // Setup execution context
        HttpExecutionContext context = new HttpExecutionContext();
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("correlationId", UUID.randomUUID().toString());
        metadata.put("requestSource", "backend-service");
        context.setMetaData(metadata);
        
        context.addHttpRequestHandler(new HttpRequestHandler() {
            @Override
            public void onRequest(HttpExecutionContext ctx, HttpRequest req) {
                // Add request timestamp
                req.addHeader("X-Request-Timestamp", 
                             String.valueOf(System.currentTimeMillis()));
            }
        });
        
        // Execute request
        HttpClient httpClient = new HttpClient();
        
        Result result = httpClient.execute(context, request, 
            new HttpResponseHandler<Result>() {
                @Override
                public Result onResponse(HttpResponse httpResponse) {
                    if (httpResponse.getStatusCode() >= 200 && 
                        httpResponse.getStatusCode() < 300) {
                        return parseResult(httpResponse.getJSONObjectBody());
                    } else {
                        throw new HttpException("Request failed: " + 
                                               httpResponse.getStatusCode());
                    }
                }
                
                @Override
                public void onMaxRetryAttempted(HttpResponse httpResponse) {
                    LOGGER.error("Max retries reached. Status: {}", 
                                httpResponse.getStatusCode());
                    // Send alert, log to monitoring system, etc.
                }
            }
        );
    }
    
    private static Result parseResult(JSONObject json) {
        // Parse JSON to Result object
        return new Result(json);
    }
}
```

### Handling Different Content Types

```java
// JSON payload
HttpRequest.HttpPayload jsonPayload = new HttpRequest.HttpPayload(
    StringPool.UTF8,
    "application/json",
    "{\"key\":\"value\"}"
);

// Form data
Map<String, Object> formData = new HashMap<>();
formData.put("username", "john");
formData.put("password", "secret");
HttpRequest.HttpPayload formPayload = new HttpRequest.HttpPayload(
    StringPool.UTF8,
    "application/x-www-form-urlencoded",
    formData
);

// Multipart form data
Map<String, Object> multipartData = new HashMap<>();
multipartData.put("file", new File("/path/to/file.pdf"));
multipartData.put("description", "Document");
HttpRequest.HttpPayload multipartPayload = new HttpRequest.HttpPayload(
    StringPool.UTF8,
    "multipart/form-data",
    multipartData
);

// Plain text
HttpRequest.HttpPayload textPayload = new HttpRequest.HttpPayload(
    StringPool.UTF8,
    "text/plain",
    "Plain text content"
);

// XML
HttpRequest.HttpPayload xmlPayload = new HttpRequest.HttpPayload(
    StringPool.UTF8,
    "application/xml",
    "<root><item>value</item></root>"
);
```

## Error Handling

```java
try {
    httpClient.execute(context, request, new HttpResponseHandler<String>() {
        @Override
        public String onResponse(HttpResponse httpResponse) {
            int statusCode = httpResponse.getStatusCode();
            
            if (statusCode >= 200 && statusCode < 300) {
                return httpResponse.getBody();
            } else if (statusCode == 401) {
                throw new AuthenticationException("Authentication required");
            } else if (statusCode == 403) {
                throw new AuthorizationException("Access denied");
            } else if (statusCode == 404) {
                throw new ResourceNotFoundException("Resource not found");
            } else if (statusCode >= 500) {
                throw new ServerException("Server error: " + statusCode);
            } else {
                throw new HttpException("HTTP error: " + statusCode);
            }
        }
        
        @Override
        public void onMaxRetryAttempted(HttpResponse httpResponse) {
            // Log or alert when max retries exhausted
            throw new MaxRetriesExceededException(
                "Failed after " + retryPolicy.getMaxErrorRetry() + " attempts"
            );
        }
    });
} catch (AuthenticationException e) {
    // Handle authentication error
} catch (ResourceNotFoundException e) {
    // Handle not found
} catch (HttpException e) {
    // Handle general HTTP errors
} catch (Exception e) {
    // Handle unexpected errors
}
```

## Logging

The HTTP client uses SLF4J for logging and integrates with Logstash for structured logging. Request and response details are automatically logged with correlation information.

### Enable Logging

Add to your `logback.xml`:

```xml
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="net.logstash.logback.encoder.LogstashEncoder" />
    </appender>
    
    <logger name="com.javaquery.http" level="INFO" />
    
    <root level="INFO">
        <appender-ref ref="CONSOLE" />
    </root>
</configuration>
```

Logged information includes:
- Request name and method
- URL and endpoint
- Headers and query parameters
- Payload information
- Response status code
- Response time
- Retry attempts
- Metadata from execution context

## API Reference

### HttpRequest.HttpRequestBuilder

Builder for creating HTTP requests.

**Methods:**
- `withHost(String host)` - Set the host URL
- `withPort(int port)` - Set the port number
- `withEndPoint(String endPoint)` - Set the endpoint path
- `withHeader(String key, String value)` - Add a single header
- `withHeaders(Map<String, String> headers)` - Add multiple headers
- `withQueryParameter(String key, String value)` - Add a query parameter
- `withQueryParameter(Map<String, String> params)` - Add multiple query parameters
- `withBasicAuth(String username, String password)` - Set basic authentication
- `withHttpPayload(HttpPayload payload)` - Set the request payload
- `withRetryPolicy(RetryPolicy policy)` - Set retry policy
- `build()` - Build the HttpRequest

### HttpResponse

Response object containing status, headers, and body.

**Methods:**
- `int getStatusCode()` - Get HTTP status code
- `Map<String, String> getHeaders()` - Get response headers
- `String getBody()` - Get response body as string
- `JSONObject getJSONObjectBody()` - Parse body as JSON object
- `JSONArray getJSONArrayBody()` - Parse body as JSON array

### HttpClient

Main client for executing requests.

**Methods:**
- `<R> R execute(HttpExecutionContext context, HttpRequest request, HttpResponseHandler<R> handler)` - Execute HTTP request

### HttpExecutionContext

Context for request execution with metadata and handlers.

**Methods:**
- `void setMetaData(Map<String, Object> metaData)` - Set metadata map
- `void addMetaData(String key, Object value)` - Add single metadata entry
- `void addHttpRequestHandler(HttpRequestHandler handler)` - Add request handler
- `void setHttpRequestHandlers(List<HttpRequestHandler> handlers)` - Set multiple handlers

## Requirements

- Java 11 or higher
- Apache HttpComponents 4.5.14
- SLF4J 2.0.16
- JSON 20250107
- ScribeJava 8.3.3 (for OAuth)

## Dependencies

This module depends on:
- `com.javaquery:util` - Utility classes

## License

This project is part of the JLite library suite.

## Contributing

Contributions are welcome! Please ensure all tests pass before submitting pull requests.

## Author

**javaquery**

## Version

Current version: **1.0.7**

---

For more information and updates, visit the [JLite GitHub repository](https://github.com/javaquery/JLite).

