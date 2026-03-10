#!/bin/bash

# 성능 테스트 환경 빠른 시작 스크립트
# Kafka, LocalStack SQS, Prometheus, Grafana를 모두 시작합니다.

set -e

echo "========================================"
echo "Messaging Demo - 성능 테스트 환경 시작"
echo "========================================"
echo ""

# 색상 정의
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Docker Compose 확인
if ! command -v docker-compose &> /dev/null; then
    echo -e "${RED}❌ docker-compose가 설치되어 있지 않습니다.${NC}"
    echo "설치 가이드: https://docs.docker.com/compose/install/"
    exit 1
fi

# Docker 실행 확인
if ! docker info &> /dev/null; then
    echo -e "${RED}❌ Docker가 실행되고 있지 않습니다.${NC}"
    echo "Docker Desktop을 실행해주세요."
    exit 1
fi

echo -e "${YELLOW}📦 인프라 컨테이너 시작 중...${NC}"
docker-compose up -d

echo ""
echo -e "${YELLOW}⏳ 서비스 초기화 대기 중 (30초)...${NC}"
sleep 30

echo ""
echo -e "${GREEN}✅ 인프라 시작 완료!${NC}"
echo ""
echo "========================================"
echo "접속 정보"
echo "========================================"
echo ""
echo -e "${GREEN}📊 Grafana Dashboard:${NC}  http://localhost:3000"
echo "   - Username: admin"
echo "   - Password: admin"
echo ""
echo -e "${GREEN}📈 Prometheus:${NC}         http://localhost:9090"
echo -e "${GREEN}🔍 Kafka UI:${NC}           http://localhost:8090"
echo ""
echo "========================================"
echo "다음 단계"
echo "========================================"
echo ""
echo "1. 애플리케이션 빌드:"
echo "   ./gradlew clean build"
echo ""
echo "2. Producer API 시작 (새 터미널):"
echo "   ./gradlew :producer-api:bootRun --args='--spring.profiles.active=local'"
echo ""
echo "3. Consumer Worker 시작 (새 터미널):"
echo "   ./gradlew :consumer-worker:bootRun --args='--spring.profiles.active=local'"
echo ""
echo "4. 메트릭 확인:"
echo "   - Producer: http://localhost:8080/actuator/prometheus"
echo "   - Consumer: http://localhost:8081/actuator/prometheus"
echo ""
echo "5. 성능 테스트 실행:"
echo "   자세한 가이드는 PERFORMANCE_TESTING.md를 참고하세요."
echo ""
echo -e "${YELLOW}📚 전체 가이드:${NC} PERFORMANCE_TESTING.md"
echo ""

# 컨테이너 상태 확인
echo "========================================"
echo "컨테이너 상태"
echo "========================================"
docker-compose ps
