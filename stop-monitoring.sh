#!/bin/bash

# 성능 테스트 환경 종료 스크립트
# 모든 Docker 컨테이너를 정리합니다.

set -e

echo "========================================"
echo "Messaging Demo - 환경 정리"
echo "========================================"
echo ""

# 색상 정의
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# 옵션 파싱
REMOVE_VOLUMES=false
while getopts "v" opt; do
  case $opt in
    v)
      REMOVE_VOLUMES=true
      ;;
    \?)
      echo "사용법: $0 [-v]"
      echo "  -v: 볼륨(데이터)까지 제거"
      exit 1
      ;;
  esac
done

echo -e "${YELLOW}🛑 컨테이너 중지 중...${NC}"
docker-compose down

if [ "$REMOVE_VOLUMES" = true ]; then
    echo -e "${YELLOW}🗑️  볼륨(데이터) 제거 중...${NC}"
    docker-compose down -v
    echo -e "${RED}⚠️  모든 Prometheus 및 Grafana 데이터가 삭제되었습니다.${NC}"
fi

echo ""
echo -e "${GREEN}✅ 환경 정리 완료!${NC}"
echo ""
echo "다시 시작하려면:"
echo "  ./start-monitoring.sh"
echo ""
echo "데이터를 포함한 전체 정리:"
echo "  ./stop-monitoring.sh -v"
