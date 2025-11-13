#!/bin/bash

BASE_DIR="/Users/caorui.li/Documents/school/CampusClubManaSys/Backend"

echo "🚀 Starting Campus Club Management System..."

# 启动注册中心
echo "📍 Starting Registry Server..."
cd "$BASE_DIR/registry-server"
mvn spring-boot:run &
sleep 15

# 启动API网关
echo "🌐 Starting API Gateway..."
cd "$BASE_DIR/api-gateway"
mvn spring-boot:run &
sleep 10

# 启动其他服务
echo "🔐 Starting Auth Service..."
cd "$BASE_DIR/auth-service"
mvn spring-boot:run &

echo "👤 Starting User Service..."
cd "$BASE_DIR/user-service"
mvn spring-boot:run &

echo "🏛️ Starting Club Service..."
cd "$BASE_DIR/club-service"
mvn spring-boot:run &

echo "🎯 Starting Activity Service..."
cd "$BASE_DIR/activity-service"
mvn spring-boot:run &

echo "💬 Starting Message Service..."
cd "$BASE_DIR/message-service"
mvn spring-boot:run &

echo "📊 Starting Stats Service..."
cd "$BASE_DIR/stats-service"
mvn spring-boot:run &

echo "✅ All services are starting..."