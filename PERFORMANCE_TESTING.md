# SQS & Kafka 성능 테스트 가이드

로컬 환경에서 Grafana 대시보드를 통해 SQS와 Kafka의 성능을 측정하고 비교하는 가이드입니다.

## 사전 준비

### 필수 도구
- Docker & Docker Compose
- Java 17
- Gradle

### 포트 사용 현황
- `3000`: Grafana 대시보드
- `4566`: LocalStack (SQS)
- `8080`: Producer API
- `8081`: Consumer Worker
- `8090`: Kafka UI
- `9090`: Prometheus
- `9092`: Kafka

## 빠른 시작

### 1. 인프라 시작

```bash
# Docker Compose로 모든 인프라 시작
docker-compose up -d

# 컨테이너 상태 확인
docker-compose ps
```

실행되는 서비스:
- **Kafka**: 메시지 브로커
- **Kafka UI**: Kafka 모니터링 (http://localhost:8090)
- **LocalStack**: AWS SQS 로컬 에뮬레이터
- **Prometheus**: 메트릭 수집기
- **Grafana**: 시각화 대시보드

### 2. SQS 큐 확인

LocalStack이 자동으로 SQS 큐를 생성합니다. 확인하려면:

```bash
# LocalStack 컨테이너 내부에서 큐 목록 확인
docker exec -it localstack awslocal sqs list-queues

# 예상 출력:
# {
#     "QueueUrls": [
#         "http://sqs.ap-northeast-2.localhost.localstack.cloud:4566/000000000000/messaging-demo-queue",
#         "http://sqs.ap-northeast-2.localhost.localstack.cloud:4566/000000000000/messaging-demo-queue.fifo"
#     ]
# }
```

### 3. 애플리케이션 빌드

```bash
# 전체 프로젝트 빌드
./gradlew clean build

# 또는 개별 모듈 빌드
./gradlew :producer-api:build
./gradlew :consumer-worker:build
```

### 4. Producer API 시작

```bash
# Local 프로파일로 Producer 실행
./gradlew :producer-api:bootRun --args='--spring.profiles.active=local'

# 또는 JAR로 실행
java -jar producer-api/build/libs/producer-api.jar --spring.profiles.active=local
```

Producer API가 시작되면:
- REST API: http://localhost:8080
- Actuator: http://localhost:8080/actuator
- Prometheus Metrics: http://localhost:8080/actuator/prometheus

### 5. Consumer Worker 시작

```bash
# Local 프로파일로 Consumer 실행 (새 터미널)
./gradlew :consumer-worker:bootRun --args='--spring.profiles.active=local'

# 또는 JAR로 실행
java -jar consumer-worker/build/libs/consumer-worker.jar --spring.profiles.active=local
```

Consumer Worker가 시작되면:
- Actuator: http://localhost:8081/actuator
- Prometheus Metrics: http://localhost:8081/actuator/prometheus

### 6. Grafana 대시보드 접속

1. 브라우저에서 http://localhost:3000 접속
2. 로그인 정보:
   - Username: `admin`
   - Password: `admin`
3. 첫 로그인 후 비밀번호 변경 또는 Skip
4. 왼쪽 메뉴 > Dashboards > "Messaging Performance Dashboard (SQS & Kafka)" 선택

## 성능 테스트 실행

### Kafka 메시지 발행 테스트

```bash
# 단일 메시지 발행
curl -X POST http://localhost:8080/api/messages \
  -H "Content-Type: application/json" \
  -d '{
    "content": "Test message from Kafka"
  }'

# 부하 테스트 (Apache Bench 사용)
ab -n 10000 -c 100 -p request.json -T application/json \
  http://localhost:8080/api/messages
```

**request.json** 파일 내용:
```json
{
  "content": "Performance test message"
}
```

### SQS 메시지 발행 테스트

SQS 엔드포인트를 사용하여 테스트합니다 (구현 필요 시):

```bash
# SQS 메시지 발행 엔드포인트 (예시)
curl -X POST http://localhost:8080/api/sqs/messages \
  -H "Content-Type: application/json" \
  -d '{
    "content": "Test message from SQS"
  }'
```

### 부하 테스트 도구

#### 1. Apache Bench (간단한 HTTP 부하 테스트)

```bash
# 10,000개 요청, 동시 100개 연결
ab -n 10000 -c 100 -p request.json -T application/json \
  http://localhost:8080/api/messages
```

#### 2. wrk (고성능 HTTP 부하 테스트)

```bash
# 30초 동안 100개 연결로 부하 테스트
wrk -t12 -c100 -d30s --latency \
  -s post.lua \
  http://localhost:8080/api/messages
```

**post.lua** 파일:
```lua
wrk.method = "POST"
wrk.body   = '{"content":"Performance test"}'
wrk.headers["Content-Type"] = "application/json"
```

#### 3. K6 (현대적인 부하 테스트 도구)

```bash
# k6 설치
brew install k6

# 테스트 스크립트 실행
k6 run load-test.js
```

**load-test.js** 파일:
```javascript
import http from 'k6/http';
import { sleep, check } from 'k6';

export let options = {
  stages: [
    { duration: '30s', target: 50 },   // Ramp up to 50 users
    { duration: '1m', target: 100 },   // Stay at 100 users
    { duration: '30s', target: 0 },    // Ramp down to 0
  ],
};

export default function() {
  const payload = JSON.stringify({
    content: 'Performance test message',
  });

  const params = {
    headers: {
      'Content-Type': 'application/json',
    },
  };

  let res = http.post('http://localhost:8080/api/messages', payload, params);

  check(res, {
    'status is 200': (r) => r.status === 200,
    'response time < 500ms': (r) => r.timings.duration < 500,
  });

  sleep(0.1);
}
```

## Grafana 대시보드 메트릭 설명

### HTTP Request Rate
- **설명**: Producer API의 초당 요청 수 (RPS)
- **용도**: API 처리량 측정
- **목표**: 높을수록 좋음

### HTTP Request Latency (P95, P99)
- **설명**: 요청 응답 시간의 95번째, 99번째 백분위수
- **용도**: 사용자 체감 성능 측정
- **목표**: P95 < 100ms, P99 < 200ms (일반적인 목표)

### SQS Message Throughput
- **설명**: SQS 메시지 송수신 속도
- **메트릭**:
  - `spring_cloud_aws_sqs_sent_messages_total`: 발행된 메시지 수
  - `spring_cloud_aws_sqs_received_messages_total`: 수신된 메시지 수
- **용도**: SQS 처리량 측정

### SQS Messages In Flight
- **설명**: 현재 처리 중인 SQS 메시지 수
- **용도**: 처리 지연 모니터링
- **목표**: 낮게 유지 (지연 없음)

### Kafka Message Throughput
- **설명**: Kafka 메시지 송수신 속도
- **메트릭**:
  - `kafka_producer_record_send_total`: Producer가 발행한 메시지
  - `kafka_consumer_records_consumed_total`: Consumer가 소비한 메시지
- **용도**: Kafka 처리량 측정

### Kafka Latency
- **설명**: Kafka 프로듀서/컨슈머 지연 시간
- **메트릭**:
  - `kafka_producer_request_latency_avg`: Producer 평균 지연
  - `kafka_consumer_fetch_latency_avg`: Consumer fetch 평균 지연
- **용도**: Kafka 성능 측정

### CPU Usage
- **설명**: 애플리케이션 CPU 사용률
- **용도**: 리소스 사용 모니터링
- **목표**: 70% 미만 (여유 확보)

### JVM Memory Usage
- **설명**: Heap 메모리 사용량
- **메트릭**:
  - `jvm_memory_used_bytes`: 현재 사용 중인 메모리
  - `jvm_memory_max_bytes`: 최대 Heap 크기
- **용도**: 메모리 누수 및 GC 부담 모니터링

## 성능 비교 방법

### 1. Kafka vs SQS 처리량 비교

1. **Kafka 테스트**:
   - `/api/messages` 엔드포인트로 부하 생성
   - "Kafka Message Throughput" 패널에서 초당 메시지 수 확인

2. **SQS 테스트**:
   - `/api/sqs/messages` 엔드포인트로 부하 생성
   - "SQS Message Throughput" 패널에서 초당 메시지 수 확인

3. **비교 지표**:
   - 최대 처리량 (messages/sec)
   - CPU 사용률
   - 메모리 사용량

### 2. Latency 비교

1. Grafana에서 "HTTP Request Latency" 패널 확인
2. P95, P99 레이턴시를 Kafka/SQS 각각 측정
3. wrk 또는 k6의 결과와 비교

### 3. 안정성 테스트

1. **장시간 부하 테스트** (30분 이상):
   ```bash
   k6 run --duration 30m load-test.js
   ```

2. **메모리 누수 확인**:
   - "JVM Memory Usage" 패널에서 Heap 사용량이 지속적으로 증가하는지 확인
   - GC 빈도 확인

3. **Error Rate 확인**:
   - HTTP 5xx 에러 발생 빈도 확인
   - Consumer lag 확인 (Kafka UI 사용)

## 모니터링 팁

### Prometheus 쿼리 예시

Grafana 또는 Prometheus UI (http://localhost:9090)에서 직접 쿼리 실행:

```promql
# HTTP 요청 성공률
sum(rate(http_server_requests_seconds_count{status!~"5.."}[1m]))
/
sum(rate(http_server_requests_seconds_count[1m])) * 100

# Kafka Consumer Lag
kafka_consumer_records_lag_max

# SQS 에러율
rate(spring_cloud_aws_sqs_errors_total[1m])

# JVM GC 시간
rate(jvm_gc_pause_seconds_sum[1m])
```

### Alert 설정 (선택 사항)

Prometheus Alertmanager를 사용하여 알림 설정 가능:

```yaml
# prometheus/alerts.yml
groups:
  - name: messaging-alerts
    rules:
      - alert: HighLatency
        expr: histogram_quantile(0.99, rate(http_server_requests_seconds_bucket[1m])) > 1
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "High API latency detected"

      - alert: HighErrorRate
        expr: rate(http_server_requests_seconds_count{status="500"}[1m]) > 0.01
        for: 5m
        labels:
          severity: critical
        annotations:
          summary: "High error rate detected"
```

## 트러블슈팅

### LocalStack SQS 연결 실패

```bash
# LocalStack 로그 확인
docker logs localstack

# SQS 큐 재생성
docker exec -it localstack awslocal sqs create-queue --queue-name messaging-demo-queue
```

### Prometheus가 메트릭을 수집하지 않음

1. **애플리케이션 Actuator 확인**:
   ```bash
   curl http://localhost:8080/actuator/prometheus
   curl http://localhost:8081/actuator/prometheus
   ```

2. **Prometheus Targets 확인**:
   - http://localhost:9090/targets
   - producer-api, consumer-worker가 "UP" 상태인지 확인

3. **Docker 네트워크 문제** (macOS):
   - `host.docker.internal` 대신 실제 IP 사용 필요할 수 있음
   - `prometheus.yml`에서 targets를 실제 IP로 변경

### Grafana 대시보드가 비어있음

1. **데이터소스 확인**:
   - Grafana > Configuration > Data Sources
   - Prometheus 연결 테스트

2. **시간 범위 확인**:
   - 대시보드 우측 상단 시간 범위를 "Last 15 minutes"로 설정

3. **메트릭 존재 확인**:
   - Prometheus UI (http://localhost:9090)에서 직접 쿼리 테스트

## 환경 정리

```bash
# 모든 컨테이너 중지 및 제거
docker-compose down

# 볼륨까지 제거 (데이터 초기화)
docker-compose down -v

# Gradle 빌드 캐시 정리
./gradlew clean
```

## 참고 자료

- [Prometheus 쿼리 가이드](https://prometheus.io/docs/prometheus/latest/querying/basics/)
- [Grafana 대시보드 튜토리얼](https://grafana.com/docs/grafana/latest/getting-started/)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/reference/actuator/)
- [Micrometer Metrics](https://micrometer.io/docs)
- [LocalStack SQS](https://docs.localstack.cloud/user-guide/aws/sqs/)
- [Apache Kafka Monitoring](https://kafka.apache.org/documentation/#monitoring)

## 다음 단계

1. **SQS Producer/Consumer 구현**: 현재 Kafka만 구현되어 있으므로, SQS 발행/소비 로직 추가
2. **커스텀 메트릭 추가**: 비즈니스 로직에 맞는 커스텀 메트릭 구현
3. **분산 추적**: Zipkin 또는 Jaeger를 추가하여 End-to-End 추적
4. **성능 테스트 자동화**: CI/CD 파이프라인에 성능 테스트 통합
