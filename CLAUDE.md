# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Multi-Module Gradle** project implementing **Clean Architecture** with Apache Kafka integration. Each module represents an independent bounded context, following microservices architecture principles within a monorepo.

**Architecture**: 3 independent modules
- `platform-kafka` - Shared Kafka infrastructure + Message contract (library)
- `producer-api` - REST API for message publishing (Spring Boot app)
- `consumer-worker` - Background worker for message consumption (Spring Boot app)

**Key Technologies:**
- Java 17
- Spring Boot 3.5.5
- Spring Kafka
- AWS MSK IAM Auth
- Gradle Multi-Module
- Lombok

---

## Multi-Module Structure

```
kafka-demo/                              [Root Project]
├── settings.gradle                      - Module definitions
├── build.gradle                         - Shared configuration
│
├── platform-kafka/                      [Library Module]
│   ├── build.gradle                     - Kafka infrastructure dependencies
│   └── src/main/java/.../platform/
│       ├── config/
│       │   └── KafkaProperties.java     - Configuration properties
│       └── infrastructure/kafka/
│           ├── KafkaProducerConfig.java - Producer configuration
│           ├── KafkaConsumerConfig.java - Consumer configuration
│           ├── KafkaAdminConfig.java    - Admin configuration
│           ├── KafkaProducerTemplateFactory.java
│           └── dto/
│               └── Message.java         - Kafka message contract (DTO)
│
├── producer-api/                        [Application Module - Producer]
│   ├── build.gradle                     - Web + platform-kafka dependencies
│   └── src/main/java/.../producer/
│       ├── ProducerApplication.java     - Spring Boot main class
│       └── message/                     [Bounded Context]
│           ├── domain/
│           │   ├── model/
│           │   │   ├── MessageId.java           - Value Object
│           │   │   └── MessageContent.java      - Domain Entity
│           │   └── repository/
│           │       └── MessagePublisher.java    - Repository Port
│           ├── application/
│           │   ├── usecase/
│           │   │   ├── PublishMessageUseCase.java
│           │   │   └── PublishMessageService.java
│           │   └── dto/
│           │       ├── PublishMessageRequest.java
│           │       └── PublishMessageResponse.java
│           ├── infrastructure/
│           │   ├── kafka/
│           │   │   └── KafkaMessagePublisher.java - Adapter implementation
│           │   └── persistence/
│           │       └── MessageMapper.java        - Domain ↔ DTO mapper
│           └── adapter/
│               └── web/
│                   ├── MessageController.java    - REST Controller
│                   └── dto/
│                       └── MessageWebRequest.java
│
└── consumer-worker/                     [Application Module - Consumer]
    ├── build.gradle                     - No web + platform-kafka dependencies
    └── src/main/java/.../consumer/
        ├── ConsumerApplication.java     - Spring Boot main class
        └── message/
            └── infrastructure/kafka/
                └── KafkaMessageListener.java - Kafka @KafkaListener

```

---

## Common Commands

### Build and Run

```bash
# Build all modules
./gradlew build

# Build specific module
./gradlew :producer-api:build
./gradlew :consumer-worker:build
./gradlew :platform-kafka:build

# Run tests for all modules
./gradlew test

# Run tests for specific module
./gradlew :producer-api:test
```

### Local Development (Docker Compose)

```bash
# Start Kafka and Kafka UI
docker-compose up -d

# Run Producer API (port 8080)
java -jar producer-api/build/libs/producer-api.jar --spring.profiles.active=local

# Run Consumer Worker
java -jar consumer-worker/build/libs/consumer-worker.jar --spring.profiles.active=local

# Or using Gradle
./gradlew :producer-api:bootRun --args='--spring.profiles.active=local'
./gradlew :consumer-worker:bootRun --args='--spring.profiles.active=local'
```

### AWS MSK Environment

```bash
# Set MSK endpoint
export KAFKA_BOOTSTRAP_SERVERS="b-1.xxx.kafka.region.amazonaws.com:9092,b-2.xxx.kafka.region.amazonaws.com:9092"

# Run Producer with MSK
java -jar producer-api/build/libs/producer-api.jar --spring.profiles.active=msk

# Run Consumer with MSK
java -jar consumer-worker/build/libs/consumer-worker.jar --spring.profiles.active=msk
```

**Important**: See `RUNNING.md` for detailed deployment and configuration guide.

---

## Clean Architecture + Bounded Context

Each application module (`producer-api`, `consumer-worker`) is a **complete bounded context** with its own Clean Architecture layers:

### Producer-API Module

```
producer-api/src/main/java/.../producer/
├── ProducerApplication.java             - Main class
└── message/                             [Bounded Context]
    ├── domain/                          - Business logic (framework-independent)
    │   ├── model/                       - Entities & Value Objects
    │   └── repository/                  - Port interfaces
    ├── application/                     - Use cases & orchestration
    │   ├── usecase/                     - Use case interfaces & implementations
    │   └── dto/                         - Application DTOs
    ├── infrastructure/                  - External integrations
    │   ├── kafka/                       - Kafka adapter implementation
    │   └── persistence/                 - Mappers
    └── adapter/                         - Entry points
        └── web/                         - REST controllers
```

### Consumer-Worker Module

```
consumer-worker/src/main/java/.../consumer/
├── ConsumerApplication.java             - Main class
└── message/
    └── infrastructure/kafka/
        └── KafkaMessageListener.java   - Event listener (simple processing)
```

**Note**: Consumer is simplified - it processes messages directly without complex domain logic. For business-heavy consumers, add domain/application layers.

### Platform-Kafka Module (Shared Infrastructure)

```
platform-kafka/src/main/java/.../platform/
├── config/
│   └── KafkaProperties.java            - @ConfigurationProperties
└── infrastructure/kafka/
    ├── KafkaProducerConfig.java
    ├── KafkaConsumerConfig.java
    ├── KafkaAdminConfig.java
    └── dto/
        └── Message.java                - Integration contract (DTO)
```

### Dependency Flow (Clean Architecture Principle)

```
producer-api:
  Adapter (Web) → Application (Use Case) → Domain (Entity + Port)
                                                ↑
                                                │ implements
                                           Infrastructure (Kafka) → platform-kafka

consumer-worker:
  Infrastructure (Kafka Listener) → platform-kafka
```

**Key Points:**
- **Bounded Context**: Each module owns its domain (no shared domain module)
- **Shared Contract**: `Message` DTO in `platform-kafka` is the integration contract
- **Domain Independence**: Producer's domain layer has zero framework dependencies
- **Infrastructure Sharing**: Kafka configuration is centralized in `platform-kafka`
- **Module Isolation**: Each module can be deployed, tested, and scaled independently

---

## Configuration Management

### Externalized Configuration

All Kafka settings are in each module's `application.yml` (inherited from `platform-kafka`) and can be overridden by environment variables:

```yaml
application:
  kafka:
    bootstrap-servers: ${KAFKA_BOOTSTRAP_SERVERS:localhost:9092}
    security-protocol: ${KAFKA_SECURITY_PROTOCOL:PLAINTEXT}
    sasl-mechanism: ${KAFKA_SASL_MECHANISM:}
    sasl-jaas-config: ${KAFKA_SASL_JAAS_CONFIG:}
    topic:
      name: ${KAFKA_TOPIC_NAME:kafka-demo-topic}
    consumer:
      group-id: ${KAFKA_CONSUMER_GROUP:kafka-demo-consumer-group}
      concurrency: ${KAFKA_CONSUMER_CONCURRENCY:10}
```

### Profiles

- `local` - Docker Compose Kafka (PLAINTEXT, localhost:9092)
- `msk` - AWS MSK (SASL_SSL with IAM auth)

**Configuration Files:**
- `platform-kafka/src/main/resources/application.yml` - Base configuration
- `platform-kafka/src/main/resources/application-local.yml` - Local profile
- `platform-kafka/src/main/resources/application-msk.yml` - MSK profile

### Module Independence

Producer and Consumer are **physically separated** into different modules:

- **producer-api**: Contains `MessageController` (REST API)
- **consumer-worker**: Contains `KafkaMessageListener` (Event consumer)

**Benefits:**
1. **Independent Scaling**: Scale producer and consumer separately
2. **Independent Deployment**: Deploy only what changed
3. **Clear Separation**: No conditional logic, true physical boundaries
4. **Resource Optimization**: Consumer doesn't need web server dependencies

---

## Kafka Configuration

### Producer Settings (MSK Optimized)

- `acks=all` - Wait for all replicas
- `retries=3` - Retry failed sends
- `max.in.flight.requests=5` - Pipeline requests
- `enable.adaptive.partitioning=true` - Smart partitioning

### Consumer Settings

- `group-id`: `kafka-demo-consumer-group` (configurable via env var)
- `auto-offset-reset`: `earliest`
- `concurrency`: 10 concurrent consumers

### Topic Configuration

- Name: `kafka-demo-topic` (via `application.kafka.topic.name`)
- Partitions: 3
- Replication Factor: 2 (MSK) / 1 (Local)
- Retention: 7 days

**Auto Topic Creation**: `KafkaAdminConfig` creates topic on ApplicationReadyEvent if not exists.

---

## AWS MSK Integration

### IAM Authentication (Recommended)

**Dependencies**: `aws-msk-iam-auth:2.0.3` (already in build.gradle)

**Configuration** (application-msk.yml):
```yaml
application:
  kafka:
    security-protocol: SASL_SSL
    sasl-mechanism: AWS_MSK_IAM
    sasl-jaas-config: software.amazon.msk.auth.iam.IAMLoginModule required;
```

**Required IAM Permissions:**
```json
{
  "Version": "2012-10-17",
  "Statement": [{
    "Effect": "Allow",
    "Action": [
      "kafka-cluster:Connect",
      "kafka-cluster:DescribeTopic",
      "kafka-cluster:ReadData",
      "kafka-cluster:WriteData"
    ],
    "Resource": "arn:aws:kafka:region:account-id:cluster/cluster-name/*"
  }]
}
```

### SASL/SCRAM Authentication

Alternative to IAM (requires MSK configuration):

```yaml
application:
  kafka:
    security-protocol: SASL_SSL
    sasl-mechanism: SCRAM-SHA-512
    sasl-jaas-config: org.apache.kafka.common.security.scram.ScramLoginModule required username="${KAFKA_USERNAME}" password="${KAFKA_PASSWORD}";
```

---

## Message Flow

### Publishing Flow

1. **Web Layer**: `MessageController` receives HTTP POST request with `MessageWebRequest`
2. **Application Layer**: Converts to `PublishMessageRequest` → calls `PublishMessageUseCase`
3. **Use Case**: Creates `MessageContent` domain model → calls `MessagePublisher.publish()`
4. **Infrastructure**: `KafkaMessagePublisher` converts to Kafka `Message` DTO → sends via `KafkaTemplate`

### Consumption Flow

1. **Infrastructure**: `KafkaMessageListener` receives `ConsumerRecord<String, Message>`
2. **Mapper**: `MessageMapper` converts Kafka DTO → `MessageContent` domain model
3. **Business Logic**: Process `MessageContent` (currently logs, extend as needed)

---

## Testing Strategy

### Integration Tests

- **@EmbeddedKafka**: Spins up in-memory Kafka broker on port 9092
- **Test Profile**: Uses same configuration as production
- **Korean Naming**: Test methods use Korean (e.g., `카프카_메시지_발행이_정상적으로_되는가`)

### Test Execution

```bash
# All tests
./gradlew test

# Specific test class
./gradlew test --tests "com.easttwave.pr.kafkademo.message.POST_specs"
```

---

## Adding New Message Types

When adding a new message type (e.g., `OrderMessage`):

### In platform-kafka module:
1. **DTO**: Create `platform-kafka/src/.../platform/infrastructure/kafka/dto/Order.java`
2. **Kafka Config**: Add `OrderProducerTemplateFactory.java` in platform/infrastructure/kafka

### In producer-api module:
3. **Domain Model**: Create `producer-api/src/.../producer/order/domain/model/OrderContent.java`
4. **Repository Interface**: Create `OrderPublisher.java` in order/domain/repository
5. **Use Case**: Create `PublishOrderService.java` in order/application/usecase
6. **Kafka Adapter**: Create `KafkaOrderPublisher.java` in order/infrastructure/kafka
7. **Mapper**: Create `OrderMapper.java` in order/infrastructure/persistence
8. **Web Controller**: Create `OrderController.java` in order/adapter/web

### In consumer-worker module:
9. **Listener**: Create `KafkaOrderListener.java` in consumer/order/infrastructure/kafka

**Follow existing patterns in `message/` bounded context for consistency.**

**Note**: Each message type should be its own feature package (bounded context) within each module.

---

## Key Design Decisions

1. **Multi-Module Monorepo**: Three separate modules in a single repository
   - Easier dependency management than polyrepo
   - Shared infrastructure (platform-kafka) without code duplication
   - Independent deployment and scaling

2. **Bounded Context per Module**: Each module owns its domain
   - No shared `common-domain` module (violates DDD principles)
   - Producer has its own domain model for business logic
   - Consumer is simplified (works directly with DTOs for event processing)

3. **Platform vs Common**: Package named `platform` to indicate infrastructure/platform-level concerns
   - `platform-kafka` is pure infrastructure, not domain logic
   - `Message` DTO is an integration contract, not a domain model

4. **Physical Separation**: Producer/Consumer are separate JAR files
   - No conditional logic (`@ConditionalOnProperty`)
   - True microservices architecture within monorepo
   - producer-api: 52MB JAR with web server
   - consumer-worker: 45MB JAR without web dependencies

5. **Feature-Based Packaging**: Each feature (message, order, etc.) has its own Clean Architecture structure
   - Domain, Application, Infrastructure, Adapter layers per feature
   - Supports multiple bounded contexts in one module

6. **Externalized Config**: All environment-specific settings via application.yml + environment variables
   - Base configuration in `platform-kafka` module
   - Profile-specific overrides (local, msk)

7. **MSK-Ready**: Built-in support for AWS MSK IAM authentication
   - `aws-msk-iam-auth:2.0.3` dependency in platform-kafka
   - Profile-based configuration switching

8. **Dependency Inversion**: Domain defines interfaces, Infrastructure implements them
   - Clean separation between business logic and technical implementation
   - Easy to swap Kafka for other messaging systems

---

## Multi-Module Build System

### Module Dependencies

```
producer-api ──depends on──> platform-kafka
consumer-worker ──depends on──> platform-kafka
```

**platform-kafka** is a library module:
- `build.gradle` uses `api` for transitive dependencies
- No `bootJar` task (library JAR only)
- Shared by both producer-api and consumer-worker

**Application modules** (producer-api, consumer-worker):
- `build.gradle` includes `id 'org.springframework.boot'` plugin
- `bootJar` enabled, generates executable fat JAR
- `jar` task disabled (don't need plain JAR)

### Gradle Commands Reference

```bash
# Build all modules in dependency order
./gradlew build

# Clean build (removes all build/ directories)
./gradlew clean build

# Build specific module only
./gradlew :platform-kafka:build
./gradlew :producer-api:build
./gradlew :consumer-worker:build

# Run application module
./gradlew :producer-api:bootRun
./gradlew :consumer-worker:bootRun

# Package without tests (faster)
./gradlew build -x test

# List all tasks for a module
./gradlew :producer-api:tasks
```

### Module Outputs

After `./gradlew build`:

```
platform-kafka/build/libs/platform-kafka.jar          (~5MB, library JAR)
producer-api/build/libs/producer-api.jar              (~52MB, executable JAR)
consumer-worker/build/libs/consumer-worker.jar        (~45MB, executable JAR)
```

### Adding New Modules

To add a new module (e.g., `admin-tool`):

1. **settings.gradle**: Add `include 'admin-tool'`
2. **admin-tool/build.gradle**: Create with dependencies
3. **build.gradle**: Module inherits from subprojects block
4. **Source code**: Create `admin-tool/src/main/java/...`

---

## Troubleshooting

### Common Issues

**Connection Refused (Local)**:
- Ensure `docker-compose up -d` is running
- Check ports: Kafka 9092, Kafka UI 8090
- Verify with: `docker ps` (should see kafka and kafka-ui containers)

**MSK Authentication Failed**:
- Verify IAM Role/Policy includes `kafka-cluster:Connect`
- Check Security Group allows 9092/9094/9098 ports
- Confirm MSK cluster has IAM authentication enabled

**Consumer Not Receiving Messages**:
- Check topic exists: `KafkaAdminConfig` logs topic creation
- Verify consumer group ID matches
- Check `auto-offset-reset` configuration

**Serialization Errors**:
- Ensure `Message` DTO structure matches between Producer/Consumer
- Check `JsonDeserializer` configuration

---

## Further Reading

- **Multi-Module Architecture**: See "Multi-Module Structure" and "Multi-Module Build System" sections
- **Clean Architecture + DDD**: See "Clean Architecture + Bounded Context" section
- **Bounded Context Pattern**: Each module owns its domain (no shared domain models)
- **AWS MSK Documentation**: https://docs.aws.amazon.com/msk/
- **Spring Kafka**: https://docs.spring.io/spring-kafka/reference/
- **Gradle Multi-Module**: https://docs.gradle.org/current/userguide/multi_project_builds.html
- **Running Guide**: See `RUNNING.md` for deployment scenarios and environment configurations