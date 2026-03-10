#!/bin/bash

# LocalStack SQS 초기화 스크립트
# 컨테이너 시작 시 자동으로 SQS 큐를 생성합니다.

echo "Creating SQS queues..."

# Standard Queue 생성
awslocal sqs create-queue \
  --queue-name messaging-demo-queue \
  --attributes VisibilityTimeout=30,MessageRetentionPeriod=345600

# FIFO Queue 생성 (순서 보장이 필요한 경우)
awslocal sqs create-queue \
  --queue-name messaging-demo-queue.fifo \
  --attributes FifoQueue=true,ContentBasedDeduplication=true

echo "SQS queues created successfully!"

# 생성된 큐 목록 출력
echo "Available queues:"
awslocal sqs list-queues
