# Kafka Demo 실행 가이드

## 목차
1. [로컬 환경 (Docker Compose)](#로컬-환경)
2. [AWS MSK 환경](#aws-msk-환경)
3. [Producer/Consumer 분리 실행](#producerconsumer-분리-실행)

---

## 로컬 환경

### 1. Kafka 실행
```bash
docker-compose up -d
```

### 2. 통합 모드 (Producer + Consumer)
```bash
# 기본 실행 (port 8080, Producer + Consumer 모두 활성)
./gradlew bootRun --args='--spring.profiles.active=local'
```

### 3. Producer만 실행
```bash
./gradlew bootRun --args='--spring.profiles.active=producer,local'
```

### 4. Consumer만 실행
```bash
./gradlew bootRun --args='--spring.profiles.active=consumer,local --server.port=8081'
```

### 5. 메시지 발행 테스트
```bash
curl -X POST http://localhost:8080/api/v1/message/publish \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "topic": "kafka-demo-topic",
    "message": "Hello, Kafka!"
  }'
```

---

## AWS MSK 환경

### 1. MSK 엔드포인트 환경 변수 설정
```bash
# MSK 브로커 엔드포인트 (MSK 콘솔에서 확인)
export KAFKA_BOOTSTRAP_SERVERS="b-1.mycluster.xxxxx.kafka.ap-northeast-2.amazonaws.com:9092,b-2.mycluster.xxxxx.kafka.ap-northeast-2.amazonaws.com:9092"
```

### 2. IAM 인증 사용 (권장)

#### EC2/ECS에서 실행 (IAM Role 사용)
```bash
# IAM Role이 자동으로 적용됨
./gradlew bootRun --args='--spring.profiles.active=producer,msk'
```

#### 로컬에서 실행 (AWS Credentials 사용)
```bash
# AWS CLI 설정 필요
aws configure

# Producer 실행
./gradlew bootRun --args='--spring.profiles.active=producer,msk'

# Consumer 실행
./gradlew bootRun --args='--spring.profiles.active=consumer,msk --server.port=8081'
```

### 3. SASL/SCRAM 인증 사용

#### 환경 변수 설정
```bash
export KAFKA_BOOTSTRAP_SERVERS="b-1.xxx.kafka.region.amazonaws.com:9098"
export KAFKA_USERNAME="your-username"
export KAFKA_PASSWORD="your-password"
```

#### application-msk.yml 수정
```yaml
application:
  kafka:
    security-protocol: SASL_SSL
    sasl-mechanism: SCRAM-SHA-512
    sasl-jaas-config: org.apache.kafka.common.security.scram.ScramLoginModule required username="${KAFKA_USERNAME}" password="${KAFKA_PASSWORD}";
```

#### 실행
```bash
./gradlew bootRun --args='--spring.profiles.active=producer,msk'
```

---

## Producer/Consumer 분리 실행

### 시나리오 1: Producer는 API 서버, Consumer는 백그라운드 워커

#### Producer 실행 (port 8080)
```bash
./gradlew bootRun --args='--spring.profiles.active=producer,msk --server.port=8080'
```

#### Consumer 실행 (port 8081)
```bash
./gradlew bootRun --args='--spring.profiles.active=consumer,msk --server.port=8081'
```

### 시나리오 2: Multi Consumer (Scale Out)

#### Consumer Group 동일 (로드 밸런싱)
```bash
# Consumer 1
./gradlew bootRun --args='--spring.profiles.active=consumer,msk --server.port=8081'

# Consumer 2
./gradlew bootRun --args='--spring.profiles.active=consumer,msk --server.port=8082'

# Consumer 3
./gradlew bootRun --args='--spring.profiles.active=consumer,msk --server.port=8083'
```

#### Consumer Group 분리 (브로드캐스트)
```bash
# Group A
./gradlew bootRun --args='--spring.profiles.active=consumer,msk --server.port=8081 --application.kafka.consumer.group-id=group-a'

# Group B
./gradlew bootRun --args='--spring.profiles.active=consumer,msk --server.port=8082 --application.kafka.consumer.group-id=group-b'
```

---

## Jar 배포 및 실행

### 1. Jar 빌드
```bash
./gradlew clean bootJar
```

### 2. Producer 실행
```bash
java -jar build/libs/kafka-demo-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=producer,msk \
  --server.port=8080
```

### 3. Consumer 실행
```bash
java -jar build/libs/kafka-demo-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=consumer,msk \
  --server.port=8081 \
  --application.kafka.consumer.group-id=kafka-demo-consumer-group
```

---

## Docker 실행

### Dockerfile 생성
```dockerfile
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY build/libs/kafka-demo-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Producer 컨테이너 실행
```bash
docker build -t kafka-demo:latest .

docker run -d \
  --name kafka-producer \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=producer,msk \
  -e KAFKA_BOOTSTRAP_SERVERS="b-1.xxx.kafka.region.amazonaws.com:9092" \
  kafka-demo:latest
```

### Consumer 컨테이너 실행
```bash
docker run -d \
  --name kafka-consumer \
  -p 8081:8081 \
  -e SPRING_PROFILES_ACTIVE=consumer,msk \
  -e SERVER_PORT=8081 \
  -e KAFKA_BOOTSTRAP_SERVERS="b-1.xxx.kafka.region.amazonaws.com:9092" \
  kafka-demo:latest
```

---

## 헬스 체크

```bash
# Producer 헬스 체크
curl http://localhost:8080/actuator/health

# Consumer 헬스 체크
curl http://localhost:8081/actuator/health
```

---

## 트러블슈팅

### 1. MSK 연결 실패
- **증상**: `Connection refused` 또는 `Timeout`
- **해결**:
  - Security Group에서 9092/9094/9098 포트 허용 확인
  - MSK 클러스터와 동일한 VPC 내에서 실행 확인
  - Bootstrap Servers 주소 확인

### 2. IAM 인증 실패
- **증상**: `Authentication failed`
- **해결**:
  - EC2/ECS IAM Role에 `kafka-cluster:Connect` 권한 확인
  - MSK 클러스터 정책에서 IAM 인증 활성화 확인

### 3. Consumer가 메시지를 수신하지 못함
- **증상**: Producer는 성공하지만 Consumer가 처리 안함
- **해결**:
  - Topic이 제대로 생성되었는지 확인
  - Consumer Group ID 확인
  - Offset이 최신인지 확인 (`auto-offset-reset: earliest`)

### 4. Serialization/Deserialization 오류
- **증상**: `SerializationException` 또는 `DeserializationException`
- **해결**:
  - Message DTO 구조가 Producer와 Consumer에서 동일한지 확인
  - JsonDeserializer 설정 확인
