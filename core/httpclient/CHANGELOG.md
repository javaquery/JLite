# Changelog

All notable changes to the core:httpclient module will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.7] - 2026-01-29

### Changed
- Minor updates and improvements
  - Updated dependencies to latest versions
  - Upgraded to Java 11

## [1.0.6] - Earlier Release

### Changed
- Bug fixes and enhancements

## [1.0.0] - Initial Release

### Added

#### Core Features
- **Full HTTP Method Support**
  - GET - Retrieve resources
  - POST - Create resources with payload
  - PUT - Update resources
  - DELETE - Remove resources
  - PATCH - Partial resource updates
- **Fluent API** - Clean, builder-based request construction
- **Apache HttpComponents** - Built on industry-standard HTTP library (4.5.14)

#### Request Building
- `HttpRequest.HttpRequestBuilder` - Fluent builder for HTTP requests
  - `withHost()` - Set host URL
  - `withPort()` - Set custom port
  - `withEndPoint()` - Define endpoint path
  - `withHeader()` - Add single header
  - `withHeaders()` - Add multiple headers at once
  - `withQueryParameter()` - Add query parameters (single or multiple)
  - `withBasicAuth()` - Configure Basic Authentication
  - `withHttpPayload()` - Set request payload
  - `withRetryPolicy()` - Configure retry behavior
  - `build()` - Create immutable HttpRequest
- Support for multiple payload types:
  - JSON (application/json)
  - Form data (application/x-www-form-urlencoded)
  - Multipart form data (multipart/form-data) with file uploads
  - Plain text (text/plain)
  - XML (application/xml)
  - Custom content types

#### Response Handling
- `HttpResponse` - Comprehensive response object
  - `getStatusCode()` - Get HTTP status code
  - `getHeaders()` - Access response headers as Map
  - `getBody()` - Get raw response body as String
  - `getJSONObjectBody()` - Parse response as JSONObject
  - `getJSONArrayBody()` - Parse response as JSONArray
- `HttpResponseHandler<R>` - Interface for custom response processing
  - `onResponse()` - Process successful responses with type-safe return
  - `onMaxRetryAttempted()` - Handle scenarios when all retries exhausted
  - Generic return type support for flexible response parsing

#### Retry Mechanism
- `RetryPolicy` - Configurable retry behavior
  - Customizable retry conditions
  - Pluggable backoff strategies
  - Configurable maximum retry attempts
- `DefaultRetryPolicy` - Pre-configured retry policy
  - `get(int maxRetries)` - Quick retry policy creation
  - Sensible defaults for common scenarios
- `RetryCondition` - Interface for custom retry conditions
  - Determine when to retry based on response
- `DefaultRetryCondition` - Built-in retry logic
  - Retries on server errors (5xx)
  - Retries on timeout errors
  - Retries on connection failures
- `BackoffStrategy` - Interface for delay calculation
  - Control wait time between retry attempts
- `DefaultBackoffStrategy` - Built-in backoff implementation
  - Progressive delay between retries
  - Prevents overwhelming servers

#### Authentication
- **Basic Authentication**
  - Username/password authentication
  - Automatic header encoding
- **OAuth 1.0 Support**
  - `OAuthConfig` - OAuth configuration builder
    - `consumerKey()` - OAuth consumer key
    - `consumerSecret()` - OAuth consumer secret
    - `accessToken()` - OAuth access token
    - `accessTokenSecret()` - OAuth access token secret
  - `OAuth10HttpRequestHandler` - Automatic OAuth signature generation
  - Integration with ScribeJava 8.3.3
  - Automatic request signing

#### Request/Response Handlers
- `HttpRequestHandler` - Pre-request processing hook
  - `onRequest()` - Modify request before execution
  - Add custom headers dynamically
  - Implement cross-cutting concerns (logging, metrics, etc.)
  - Multiple handlers support with chaining
- Extensible handler system
  - Add authentication headers
  - Inject correlation IDs
  - Add timestamps
  - Implement custom logic before requests

#### Execution Context
- `HttpExecutionContext` - Request execution context and metadata
  - `setMetaData()` - Attach metadata map to request
  - `addMetaData()` - Add individual metadata entries
  - `addHttpRequestHandler()` - Register request handlers
  - `setHttpRequestHandlers()` - Register multiple handlers at once
  - Pass contextual information through request lifecycle
  - Support for correlation IDs and request tracking
  - Handler chain execution

#### Core Classes
- `HttpClient` - Main HTTP client implementation
  - `execute()` - Execute HTTP request with context and handler
  - Automatic retry handling
  - Request/response handler execution
  - Exception handling and propagation
- `HttpMethod` - HTTP method enumeration
  - GET, POST, PUT, DELETE, PATCH constants
- `HttpRequestResponse` - Wrapper for request/response pair
  - Used in retry logic and handlers
  - Access to both request and response
- `ApacheHttpRequestBuilder` - Internal Apache HttpClient builder
  - Converts HttpRequest to Apache HttpClient format
  - Handles different payload types
  - Manages headers and authentication

#### Error Handling
- `HttpException` - Custom exception for HTTP errors
  - Comprehensive error messages
  - Wraps underlying exceptions
  - Thrown for client and server errors
- Retry mechanism for transient failures
- Max retry callback for permanent failures
- Status code-based error handling

#### Logging
- **Structured Logging** via SLF4J 2.0.16
- **Logstash Integration** with Logstash Logback Encoder 8.0
  - JSON-formatted logs
  - Structured request/response logging
- Automatic logging of:
  - Request name and HTTP method
  - Full URL with query parameters
  - Request headers (sanitized)
  - Response status code
  - Response time/latency
  - Retry attempts
  - Metadata from execution context
  - Error details and stack traces

#### Utilities
- `StringPool` - String constants and utilities
  - Common charset definitions (UTF8)
  - String pooling for memory efficiency

#### Multipart Support
- File upload support via multipart/form-data
- Mixed content in single request (files + form fields)
- Automatic MIME type detection
- Large file handling via Apache HttpMime 4.5.14

### Dependencies
- **Apache HttpComponents Client** 4.5.14 - HTTP client implementation
- **Apache HttpMime** 4.5.14 - Multipart form data support
- **SLF4J API** 2.0.16 - Logging abstraction
- **org.json** 20250107 - JSON parsing and manipulation
- **Logstash Logback Encoder** 8.0 - Structured logging
- **ScribeJava Core** 8.3.3 - OAuth 1.0 support
- **Logback Classic** 1.5.16 (test) - Logging implementation for tests
- **JUnit Jupiter** 5.8.1 (test) - Unit testing framework
- **core:util** - JLite utility classes

### Features

#### Query Parameters
- Single parameter addition
- Bulk parameter addition via Map
- Automatic URL encoding
- Type-safe parameter handling

#### Headers
- Single header addition
- Bulk header addition via Map
- Header overriding support
- Case-insensitive header access in responses

#### Payload Types
- **JSON Payloads** - Automatic JSON serialization
- **Form Data** - URL-encoded form submissions
- **Multipart Forms** - File uploads with mixed content
- **Plain Text** - Simple text payloads
- **XML** - XML document submissions
- **Custom** - Support for any content type

#### Advanced Retry Features
- Custom retry conditions based on:
  - HTTP status codes
  - Response headers
  - Response body content
  - Exception types
- Custom backoff strategies:
  - Fixed delay
  - Linear backoff
  - Exponential backoff
  - Custom algorithms
- Retry context passed to strategies
- Access to previous attempts in handlers

#### Response Processing
- Type-safe response handling with generics
- Automatic JSON parsing
- Raw response access
- Header access
- Status code checking
- Custom parsing logic

### Requirements
- Java 11 or higher

### Documentation
- Comprehensive README with 770+ lines
- Quick start guides
- Full API reference
- Multiple usage examples:
  - All HTTP methods (GET, POST, PUT, DELETE, PATCH)
  - Authentication (Basic Auth, OAuth 1.0)
  - Headers and query parameters
  - Retry policies and strategies
  - Custom request/response handlers
  - Execution context usage
  - Response processing
  - Error handling patterns
  - Multiple payload types
- Advanced usage patterns:
  - Complete examples with all features
  - Different content type handling
  - Custom retry implementations
  - Handler chaining
  - Context metadata usage
- Best practices and patterns
- Logging configuration examples

[1.0.7]: https://github.com/javaquery/JLite/compare/httpclient-1.0.6...httpclient-1.0.7
[1.0.6]: https://github.com/javaquery/JLite/compare/httpclient-1.0.0...httpclient-1.0.6
[1.0.0]: https://github.com/javaquery/JLite/releases/tag/httpclient-1.0.0
