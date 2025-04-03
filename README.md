# Kafka Message Processing Application

This is a Spring Boot application that demonstrates reactive Kafka message processing with H2 database integration. The application provides REST APIs to send messages to Kafka topics and processes them using a reactive consumer.

## Table of Contents
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Setup Instructions](#setup-instructions)
- [API Documentation](#api-documentation)
- [Code Structure](#code-structure)
- [Database Schema](#database-schema)
- [Configuration](#configuration)

## Features
- Reactive Kafka message processing
- REST API endpoints for message operations
- H2 in-memory database integration
- Message persistence and retrieval
- Error handling and retry mechanisms

## Prerequisites
- Java 17 or higher
- Maven
- Kafka server running locally
- H2 database (included as dependency)

## Setup Instructions

1. Clone the repository:
```bash
git clone <repository-url>
cd <project-directory>
```

2. Start Kafka server:
```bash
# Start Zookeeper
bin/zookeeper-server-start.sh config/zookeeper.properties

# Start Kafka server
bin/kafka-server-start.sh config/server.properties
```

3. Build the application:
```bash
mvn clean install
```

4. Run the application:
```bash
mvn spring-boot:run
```

The application will start on port 8080.

## API Documentation

### 1. Send Message to Kafka

#### Basic Message Format
```bash
curl -X POST http://localhost:8080/api/messages/send \
-H "Content-Type: application/json" \
-d '{
    "topic": "test-topic",
    "message": "Hello, Kafka!",
    "key": "test-key"
}'
```

#### Financial Transaction Message Format
```bash
curl --location 'http://localhost:8080/api/kafka/send' \
--header 'Content-Type: application/json' \
--data '{
    "FORACID": "1234567890",
    "ACCT_NAME": "Test Account",
    "TRAN_AMT": 1000.0,
    "TRAN_DATE": "2024-04-03"
}'
```

### Message Format Details

#### Financial Transaction Message Fields
| Field Name | Type | Description |
|------------|------|-------------|
| FORACID | String | Account ID |
| ACCT_NAME | String | Account Name |
| TRAN_AMT | Double | Transaction Amount |
| TRAN_DATE | String | Transaction Date (YYYY-MM-DD) |

### 2. Get All Messages
```bash
# Get all messages from the database
curl -X GET http://localhost:8080/api/messages

# Get all Kafka messages
curl --location 'http://localhost:8080/api/kafka/messages'
```

### 3. Get Message by ID
```bash
curl -X GET http://localhost:8080/api/messages/{id}
```

### 4. Get Messages by Topic
```bash
curl -X GET http://localhost:8080/api/messages/topic/{topicName}
```

## Code Structure

### Main Components

1. **KafkaConfig** (`com.example.kafka.config.KafkaConfig`)
   - Configures Kafka producer and consumer properties
   - Sets up reactive Kafka template
   - Configures message listener container

2. **MessageController** (`com.example.kafka.controller.MessageController`)
   - REST endpoints for message operations
   - Handles message sending and retrieval

3. **ReactiveKafkaConsumer** (`com.example.kafka.consumer.ReactiveKafkaConsumer`)
   - Reactive Kafka message consumer
   - Processes messages and saves to database
   - Implements error handling and retry logic

4. **MessageService** (`com.example.kafka.service.MessageService`)
   - Business logic for message operations
   - Database interactions

5. **MessageRepository** (`com.example.kafka.repository.MessageRepository`)
   - JPA repository for message persistence
   - Custom query methods

6. **Message** (`com.example.kafka.model.Message`)
   - Entity class representing message data
   - JPA annotations for database mapping

## Database Schema

The application uses H2 in-memory database with the following schema:

```sql
CREATE TABLE message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    topic VARCHAR(255),
    message_key VARCHAR(255),
    content TEXT,
    created_at TIMESTAMP
);
```

## Configuration

### Application Properties
```properties
# Kafka Configuration
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=my-group
spring.kafka.consumer.auto-offset-reset=earliest
spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.value-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.apache.kafka.common.serialization.StringSerializer

# H2 Database Configuration
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update
```

### Kafka Consumer Configuration
- Group ID: my-group
- Auto offset reset: earliest
- Concurrency: 3
- Batch mode: true
- Poll timeout: 1000ms
- Max poll records: 500

## Error Handling

The application includes comprehensive error handling:
- Kafka message processing errors
- Database operation errors
- REST API validation errors
- Retry mechanism for failed operations

## Monitoring

The application provides:
- H2 console for database monitoring (http://localhost:8080/h2-console)
- Spring Boot Actuator endpoints for application metrics
- Kafka consumer metrics
- Database operation logs

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details. 