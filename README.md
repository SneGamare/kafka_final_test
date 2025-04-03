# Kafka Avro Demo Application

This is a Spring Boot application that demonstrates Kafka integration with Avro serialization. The application provides REST endpoints to send and receive messages using Kafka with Avro serialization.

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Docker and Docker Compose (for running Kafka)
- Git (optional, for cloning the repository)

## Setup Instructions

1. **Clone the repository** (if you haven't already):
   ```bash
   git clone <repository-url>
   cd kafka-avro-demo
   ```

2. **Start Kafka using Docker Compose**:
   ```bash
   docker-compose up -d
   ```
   This will start:
   - Zookeeper (port 2181)
   - Kafka broker (port 9092)
   - Schema Registry (port 8081)

3. **Build the application**:
   ```bash
   mvn clean install
   ```

## Running the Application

1. **Start the Spring Boot application**:
   ```bash
   java -jar target/kafka-avro-demo-1.0-SNAPSHOT.jar
   ```
   The application will start on port 8080.

2. **Verify the application is running**:
   ```bash
   curl http://localhost:8080/api/kafka/health
   ```
   Expected response: `Kafka service is running`

## Available Endpoints

1. **Health Check**
   - GET `http://localhost:8080/api/kafka/health`
   - Returns the health status of the Kafka service

2. **Send Sample Message**
   - POST `http://localhost:8080/api/kafka/send`
   - Sends a predefined sample message to Kafka

3. **Send Custom Message**
   - POST `http://localhost:8080/api/kafka/send/custom`
   - Sends a custom message to Kafka
   - Request body should be a JSON object with the following structure:
     ```json
     {
       "foracid": "string",
       "acctname": "string",
       "lastTranDateCr": "date",
       "tranDate": "date",
       "tranId": "string",
       "partTranSrlNum": "string",
       "delFlg": "string",
       "tranType": "string",
       "tranSubType": "string",
       "partTranType": "string",
       "glSubHeadCode": "string",
       "acid": "string",
       "valueDate": "date",
       "tranAmt": "number",
       "tranParticular": "string",
       "entryDate": "date",
       "pstdDate": "date",
       "refNum": "string",
       "instrmntType": "string",
       "instrmntDate": "date",
       "instrmntNum": "string",
       "tranRmks": "string",
       "custId": "string",
       "brCode": "string",
       "crncyCode": "string",
       "tranCrncyCode": "string",
       "refAmt": "number",
       "solId": "string",
       "bankCode": "string",
       "treaRefNum": "string",
       "reversalDate": "date"
     }
     ```

4. **Get Messages**
   - GET `http://localhost:8080/api/kafka/messages`
   - Retrieves all messages received by the consumer

## Troubleshooting

1. **Port 8080 already in use**
   - Find the process using port 8080:
     ```bash
     netstat -ano | findstr :8080
     ```
   - Stop the process:
     ```bash
     taskkill /F /PID <process-id>
     ```

2. **Kafka connection issues**
   - Ensure Docker containers are running:
     ```bash
     docker ps
     ```
   - Check Kafka logs:
     ```bash
     docker-compose logs kafka
     ```

3. **Application startup issues**
   - Check application logs for errors
   - Verify all required services (Kafka, Zookeeper, Schema Registry) are running
   - Ensure all required environment variables are set

## Development

- The application uses Spring Boot 2.7.0
- Kafka configuration is in `src/main/java/com/example/kafka/config/KafkaConfig.java`
- Avro schema is defined in `src/main/resources/avro/plutus-finacle-data.avsc`
- Custom serializers are in `src/main/java/com/example/kafka/serializer/`

## License

[Add your license information here] 