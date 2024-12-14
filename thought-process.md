# Thought Process for the Verve-Service Implementation

## Design Considerations

### Overview
The goal of the `Verve-Service` implementation was to create a REST service capable of processing at least 10,000 requests per second while adhering to the outlined requirements and extensions. Below are the key design decisions and approaches used to meet the functional and non-functional requirements:

### Core Features
1. **High Throughput:**
    - Used Spring Boot, a lightweight and efficient Java framework, to build the service.
    - Configured Nginx as a load balancer with a `least_conn` strategy to distribute incoming requests evenly across multiple application instances.

2. **Request Handling and Deduplication:**
    - Redis is used for high-performance request deduplication to ensure unique requests are processed, even in a distributed setup.
    - Each request’s uniqueness is validated using Redis’s `setIfAbsent` mechanism.

3. **Scheduled Task for Logging:**
    - Implemented a scheduled task using Spring's `@Scheduled` annotation to log the count of unique requests every minute.

4. **HTTP POST Requests to External Endpoints:**
    - When an endpoint is provided in the request, the service sends a POST request to the external endpoint using Spring WebClient.
    - Used retry mechanisms for resilience in case of transient failures.

5. **Distributed Streaming for Logging:**
    - Kafka is used to publish the count of unique requests to a topic (`unique-requests`) instead of logging to a file.
    - Configured Kafka producer properties for performance optimization.

### Extensions

#### Extension 1: HTTP POST Requests
Instead of firing an HTTP GET request, a POST request is sent with a custom JSON payload containing:
- Request ID
- Endpoint
- Unique request count in the current minute.

#### Extension 2: Deduplication in a Distributed Setup
- Redis acts as the shared deduplication store across all application instances.
- Ensured atomicity of operations using Redis’s `setIfAbsent` and TTL-based keys to avoid race conditions.

#### Extension 3: Kafka Integration
- Used Kafka for distributed logging of unique request counts. This ensures scalability and supports downstream processing.

### Scalability Considerations
- Horizontal scalability achieved using Docker containers orchestrated by `docker-compose`.
- Nginx ensures efficient load distribution across multiple application instances.
- Redis and Kafka were chosen for their performance and scalability in distributed environments.

---

## Implementation Details

### Application Architecture
- **Controller Layer**: Exposes the `/api/verve/accept` endpoint.
- **Service Layer**: Handles business logic, including deduplication, asynchronous requests, and scheduled tasks.
- **Repository Layer**: Provides an abstraction over Redis for deduplication.

### Key Components
1. **Redis Service**:
    - Provides `saveIfAbsent` for atomic deduplication.
    - Periodically clears Redis keys matching a pattern using `SCAN` for efficient cleanup.

2. **Kafka Service**:
    - Publishes unique request counts to a Kafka topic.
    - Configured for asynchronous message sending to avoid blocking.

3. **WebClient**:
    - Used for non-blocking HTTP POST requests to external endpoints.
    - Configured with retry mechanisms to handle transient errors.

4. **Scheduled Reporting**:
    - Every minute, the unique request count is reported to Kafka and the in-memory counter is reset.

---

## Testing Strategy

### Functional Testing with Postman
1. **Endpoints Tested**:
    - **GET /api/verve/accept**: Verified handling of both valid and invalid requests.
    - Payloads:
        - Valid: `id` and `endpoint` query parameters.
        - Invalid: Missing `id` or invalid data types.
    - Response Validation:
        - Expected responses: `"ok"` or `"failed"`.
        - Verified response codes (200, 500).

2. **Scenarios Covered**:
    - Duplicate `id` requests.
    - Requests with and without an external `endpoint`.

### Performance Testing with JMeter
1. **Test Setup**:
    - Configured JMeter to send concurrent requests to `/api/verve/accept`.
    - Simulated 10,000+ requests per second using thread groups.

2. **Performance Metrics**:
    - Measured average response time and throughput.
    - Ensured Redis and Kafka handled high concurrency without bottlenecks.

3. **Results**:
    - Verified that the system maintained consistent performance under load.
    - Identified and tuned Nginx and application configurations to handle peak loads.

---

## Tools and Technologies Used
1. **Backend**:
    - Spring Boot
    - Redis
    - Kafka
2. **Load Balancer**:
    - Nginx
3. **Containerization**:
    - Docker
    - Docker Compose
4. **Testing**:
    - Postman for functional testing.
    - JMeter for performance testing.

---

## Future Enhancements
1. **Observability**:
    - Add Prometheus and Grafana for detailed application metrics and monitoring.

2. **Resilience**:
    - Implement circuit breakers (e.g., using Resilience4j) for external endpoint requests.

3. **Scalability**:
    - Move to Kubernetes for orchestration to handle dynamic scaling requirements.

This approach ensures the service is robust, scalable, and capable of handling high loads in a distributed environment.

