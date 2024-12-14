# Verve-Service

## Overview

The `Verve-Service` is a high-performance REST service designed to handle at least 10,000 requests per second. The service processes incoming requests, logs unique IDs, and integrates with external systems such as Redis for deduplication, Kafka for distributed messaging, and a load balancer for horizontal scaling. It is implemented using Java and Spring Boot and is containerized for easy deployment.

## Features

1. **High-Performance REST API**
    - Endpoint: `/api/verve/accept`
    - Accepts an integer `id` as a mandatory query parameter and an optional string `endpoint`.
    - Returns:
        - "ok" for successful processing.
        - "failed" in case of errors.

2. **Unique ID Tracking**
    - Tracks unique requests based on the `id` parameter.
    - Uses Redis for distributed deduplication across multiple instances.

3. **Scheduled Reporting**
    - Logs the count of unique requests every minute.
    - Sends this count to a Kafka topic for distributed processing.

4. **Endpoint Integration**
    - When an `endpoint` is provided, sends an asynchronous POST request with the unique request count and additional payload.

## Installation

1. Clone the repository:
   ```bash
   git clone <repository-link>
   cd Verve-Service
   ```

2. Build the Docker image:
   ```bash
   docker build -t verve-service .
   ```

3. Start the application using Docker Compose:
   ```bash
   docker-compose up
   ```

## Configuration

The application is configured via the `application.properties` file. Key configurations include:

- **Redis**:
    - Host: `redis`
    - Port: `6379`
- **Kafka**:
    - Bootstrap Servers: `kafka:9092`, `kafka-2:9093`
    - Topic: `unique-requests`
- **Nginx Load Balancer**: Distributes requests among three instances of the application.

## Testing

1. Send a request to the API:
   ```bash
   curl "http://localhost/api/verve/accept?id=123&endpoint=http://example.com"
   ```

2. Verify logs for unique request counts and HTTP status codes.

3. Check Kafka and Redis for distributed processing and deduplication.

---

