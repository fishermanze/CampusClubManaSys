# 校园社团管理系统后端操作文档

## 1. 系统架构概述

校园社团管理系统采用微服务架构设计，基于Spring Cloud构建，实现了高内聚、低耦合的分布式系统。系统包含以下核心组件：

### 1.1 服务架构图

```
┌─────────────────────────────────────────────────────────────────┐
│                          客户端 (前端)                            │
└───────────────────────────────────────────────┬─────────────────┘
                                                │
┌───────────────────────────────────────────────▼─────────────────┐
│                       API Gateway (api-gateway)                  │
│                     端口: 8080                                   │
└─────────────────┬─────────────────┬─────────────┬───────────────┘
                  │                 │             │
┌─────────────────▼───┐   ┌─────────▼─────────────▼───────────────┐
│ 注册中心            │   │        微服务集群                       │
│ (registry-server)  │   │                                         │
│ 端口: 8761          │   │  ┌───────────────┐    ┌───────────────┐ │
└─────────────────────┘   │  │ 认证服务      │    │ 用户服务      │ │
                          │  │ (auth-service)│    │ (user-service)│ │
                          │  │ 端口: 8081    │    │ 端口: 8082    │ │
                          │  └───────────────┘    └───────────────┘ │
                          │                                         │
                          │  ┌───────────────┐    ┌───────────────┐ │
                          │  │ 社团服务      │    │ 活动服务      │ │
                          │  │ (club-service)│    │(activity-svc) │ │
                          │  │ 端口: 8083    │    │ 端口: 8084    │ │
                          │  └───────────────┘    └───────────────┘ │
                          │                                         │
                          │  ┌───────────────┐    ┌───────────────┐ │
                          │  │ 消息服务      │    │ 统计服务      │ │
                          │  │(message-svc)  │    │ (stats-svc)   │ │
                          │  │ 端口: 8085    │    │ 端口: 8086    │ │
                          │  └───────────────┘    └───────────────┘ │
                          │                                         │
                          │  ┌───────────────┐                      │
                          │  │ AI服务        │                      │
                          │  │ (ai-service)  │                      │
                          │  │ 端口: 8087    │                      │
                          │  └───────────────┘                      │
                          └─────────────────────────────────────────┘
                                      │
┌──────────────────────────────────────▼───────────────────────────┐
│                         数据存储层                                │
│  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐ │
│  │   MySQL         │    │    Redis        │    │ 文件存储         │ │
│  │  关系型数据库    │    │    缓存数据库    │    │ (上传文件)       │ │
│  └─────────────────┘    └─────────────────┘    └─────────────────┘ │
└─────────────────────────────────────────────────────────────────────┘
```

### 1.2 技术栈

- **核心框架**: Spring Boot 2.7.15, Spring Cloud 2021.0.8
- **服务发现**: Netflix Eureka
- **API网关**: Spring Cloud Gateway
- **RPC调用**: Spring Cloud OpenFeign
- **安全认证**: JWT (JSON Web Token)
- **数据库**: MySQL 8.0.33
- **缓存**: Redis
- **ORM框架**: Spring Data JPA
- **构建工具**: Maven
- **WebSocket**: 支持实时消息通信

## 2. 服务功能说明

### 2.1 注册中心 (registry-server)

服务注册与发现中心，负责管理所有微服务实例的注册与健康检查。

- **主要功能**:
  - 服务实例注册与注销
  - 健康状态检查
  - 服务列表维护
  - 服务发现功能提供

### 2.2 API网关 (api-gateway)

统一的API入口，提供路由转发、认证授权、负载均衡等功能。

- **主要功能**:
  - 请求路由转发
  - 统一认证拦截
  - 跨域请求处理
  - 限流与熔断
  - 统一异常处理

### 2.3 认证服务 (auth-service)

负责用户认证、授权和会话管理。

- **主要功能**:
  - 用户注册
  - 用户登录 (账号密码/验证码登录)
  - 令牌生成与验证
  - 密码重置
  - 发送验证码

### 2.4 用户服务 (user-service)

管理用户基本信息、个人资料和用户关系。

- **主要功能**:
  - 用户信息管理
  - 个人资料维护
  - 头像上传
  - 用户关注/粉丝管理
  - 用户状态管理

### 2.5 社团服务 (club-service)

管理社团信息、成员和社团申请。

- **主要功能**:
  - 社团创建与管理
  - 成员管理 (加入/退出/踢出)
  - 社团申请处理
  - 社团信息维护
  - 社团统计数据

### 2.6 活动服务 (activity-service)

管理社团活动的创建、报名、签到等功能。

- **主要功能**:
  - 活动创建与管理
  - 活动报名与审批
  - 签到/退签管理
  - 活动评论
  - 活动统计

### 2.7 消息服务 (message-service)

提供实时消息通信和系统通知功能。

- **主要功能**:
  - 私信发送与接收
  - 会话管理
  - 消息已读状态
  - 系统通知
  - WebSocket实时通信

### 2.8 统计服务 (stats-service)

提供系统各类统计数据。

- **主要功能**:
  - 用户统计数据
  - 活动统计数据
  - 社团统计数据
  - 数据图表生成

### 2.9 AI服务 (ai-service)

提供智能推荐等AI功能。

- **主要功能**:
  - 社团推荐
  - 活动推荐
  - 智能问答

## 3. API接口文档

### 3.1 认证相关API

| 接口路径 | 方法 | 功能描述 | 请求参数 | 成功响应 | 失败响应 |
|---------|------|---------|----------|---------|----------|
| `/api/auth/login` | POST | 用户登录 | `{"username": "...", "password": "..."}` | `{"success": true, "data": {"token": "...", "user": {...}}}` | `{"success": false, "message": "用户名或密码错误"}` |
| `/api/auth/register` | POST | 用户注册 | `{"username": "...", "password": "...", "confirmPassword": "...", "phone": "...", "verificationCode": "...", "realName": "...", "uid": "..."}` | `{"success": true, "message": "注册成功"}` | `{"success": false, "message": "注册失败原因"}` |
| `/api/auth/logout` | POST | 退出登录 | 无 | `{"success": true, "message": "退出成功"}` | `{"success": false, "message": "退出失败"}` |
| `/api/auth/refresh` | POST | 刷新Token | `{"oldToken": "..."}` | `{"success": true, "data": {"token": "..."}}` | `{"success": false, "message": "刷新失败"}` |
| `/api/auth/send-code` | POST | 发送验证码 | `{"phone": "...", "type": "login/register/forgot"}` | `{"success": true, "message": "验证码已发送"}` | `{"success": false, "message": "发送失败"}` |
| `/api/auth/current-user` | GET | 获取当前用户信息 | 无 | `{"success": true, "data": {"id": 1, "username": "...", "role": "..."}}` | `{"success": false, "message": "获取失败"}` |
| `/api/auth/reset-password` | POST | 重置密码 | `{"phone": "...", "verificationCode": "...", "newPassword": "...", "confirmPassword": "..."}` | `{"success": true, "message": "密码重置成功"}` | `{"success": false, "message": "重置失败"}` |
| `/api/auth/change-password` | POST | 修改密码 | `{"oldPassword": "...", "newPassword": "...", "confirmPassword": "..."}` | `{"success": true, "message": "密码修改成功"}` | `{"success": false, "message": "修改失败"}` |

### 3.2 用户相关API

| 接口路径 | 方法 | 功能描述 | 请求参数 | 成功响应 | 失败响应 |
|---------|------|---------|----------|---------|----------|
| `/api/user/list` | GET | 获取用户列表 | `page, pageSize, keyword, role, status` | `{"success": true, "data": {"list": [...], "total": 100}}` | `{"success": false, "message": "获取失败"}` |
| `/api/user/{id}` | GET | 获取用户详情 | 无 | `{"success": true, "data": {"id": 1, "username": "...", "profile": {...}}}` | `{"success": false, "message": "用户不存在"}` |
| `/api/user/{id}` | PUT | 更新用户信息 | `{"username": "...", "email": "...", ...}` | `{"success": true, "data": {...}}` | `{"success": false, "message": "更新失败"}` |
| `/api/user/{id}/profile` | PUT | 更新用户档案 | `{"bio": "...", "preferences": {...}}` | `{"success": true, "data": {...}}` | `{"success": false, "message": "更新失败"}` |
| `/api/user/{id}/avatar` | POST | 上传头像 | `multipart/form-data` 文件 | `{"success": true, "data": {"avatarUrl": "..."}}` | `{"success": false, "message": "上传失败"}` |
| `/api/user/{id}/follow` | POST | 关注用户 | 无 | `{"success": true, "message": "关注成功"}` | `{"success": false, "message": "关注失败"}` |
| `/api/user/{id}/unfollow` | POST | 取消关注 | 无 | `{"success": true, "message": "取消关注成功"}` | `{"success": false, "message": "取消失败"}` |
| `/api/user/{id}/followings` | GET | 获取关注列表 | `page, pageSize` | `{"success": true, "data": {"list": [...], "total": 50}}` | `{"success": false, "message": "获取失败"}` |
| `/api/user/{id}/followers` | GET | 获取粉丝列表 | `page, pageSize` | `{"success": true, "data": {"list": [...], "total": 100}}` | `{"success": false, "message": "获取失败"}` |
| `/api/user/{id}/status` | PUT | 更新用户状态 | `{"status": "active/inactive"}` | `{"success": true, "data": {...}}` | `{"success": false, "message": "更新失败"}` |

### 3.3 社团相关API

| 接口路径 | 方法 | 功能描述 | 请求参数 | 成功响应 | 失败响应 |
|---------|------|---------|----------|---------|----------|
| `/api/club/list` | GET | 获取社团列表 | `page, pageSize, keyword, category` | `{"success": true, "data": {"list": [...], "total": 50}}` | `{"success": false, "message": "获取失败"}` |
| `/api/club/{id}` | GET | 获取社团详情 | 无 | `{"success": true, "data": {"id": 1, "name": "...", "members": [...]}}` | `{"success": false, "message": "社团不存在"}` |
| `/api/club/create` | POST | 创建社团 | `{"name": "...", "description": "...", "category": "..."}` | `{"success": true, "data": {"id": 1}}` | `{"success": false, "message": "创建失败"}` |
| `/api/club/apply` | POST | 申请加入社团 | `{"clubId": 1, "reason": "..."}` | `{"success": true, "message": "申请已提交"}` | `{"success": false, "message": "申请失败"}` |
| `/api/club/apply/pending` | GET | 获取待审批申请 | `clubId, page, pageSize` | `{"success": true, "data": {"list": [...], "total": 10}}` | `{"success": false, "message": "获取失败"}` |
| `/api/club/apply/handle` | POST | 处理申请 | `{"applyId": 1, "status": "approved/rejected", "reason": "..."}` | `{"success": true, "message": "处理成功"}` | `{"success": false, "message": "处理失败"}` |
| `/api/club/kick` | POST | 踢出成员 | `{"clubId": 1, "userId": 2, "reason": "..."}` | `{"success": true, "message": "已踢出"}` | `{"success": false, "message": "操作失败"}` |
| `/api/club/my-clubs` | GET | 获取用户加入的社团 | `page, pageSize` | `{"success": true, "data": {"list": [...], "total": 5}}` | `{"success": false, "message": "获取失败"}` |
| `/api/club/{id}/members` | GET | 获取社团成员列表 | `page, pageSize, role` | `{"success": true, "data": {"list": [...], "total": 100}}` | `{"success": false, "message": "获取失败"}` |
| `/api/club/{id}/join` | POST | 加入社团 | 无 | `{"success": true, "message": "加入成功"}` | `{"success": false, "message": "加入失败"}` |
| `/api/club/{id}/leave` | POST | 退出社团 | 无 | `{"success": true, "message": "退出成功"}` | `{"success": false, "message": "退出失败"}` |
| `/api/club/recommended` | GET | 获取推荐社团 | 无 | `{"success": true, "data": [...]}` | `{"success": false, "message": "获取失败"}` |

### 3.4 活动相关API

| 接口路径 | 方法 | 功能描述 | 请求参数 | 成功响应 | 失败响应 |
|---------|------|---------|----------|---------|----------|
| `/api/activities` | GET | 获取活动列表 | `page, pageSize, status, keyword` | `{"success": true, "data": {"list": [...], "total": 100}}` | `{"success": false, "message": "获取失败"}` |
| `/api/activities/{id}` | GET | 获取活动详情 | 无 | `{"success": true, "data": {"id": 1, "title": "...", "details": {...}}}` | `{"success": false, "message": "活动不存在"}` |
| `/api/activities` | POST | 创建活动 | `{"title": "...", "content": "...", "startTime": "...", "endTime": "...", "clubId": 1}` | `{"success": true, "data": {"id": 1}}` | `{"success": false, "message": "创建失败"}` |
| `/api/activities/{id}` | PUT | 更新活动 | `{"title": "...", "content": "...", "status": 1}` | `{"success": true, "data": {...}}` | `{"success": false, "message": "更新失败"}` |
| `/api/activities/{id}/join` | POST | 报名参加活动 | 无 | `{"success": true, "message": "报名成功"}` | `{"success": false, "message": "报名失败"}` |
| `/api/activities/{id}/cancel` | POST | 取消活动报名 | 无 | `{"success": true, "message": "取消成功"}` | `{"success": false, "message": "取消失败"}` |
| `/api/activities/{id}/checkin` | POST | 活动签到 | 无 | `{"success": true, "message": "签到成功"}` | `{"success": false, "message": "签到失败"}` |
| `/api/activities/{id}/checkout` | POST | 活动退签 | 无 | `{"success": true, "message": "退签成功"}` | `{"success": false, "message": "退签失败"}` |
| `/api/activities/{id}/participants` | GET | 获取活动参与者 | `page, pageSize, status` | `{"success": true, "data": {"list": [...], "total": 50}}` | `{"success": false, "message": "获取失败"}` |
| `/api/activities/{id}/comments` | GET | 获取活动评论 | `page, pageSize` | `{"success": true, "data": {"list": [...], "total": 20}}` | `{"success": false, "message": "获取失败"}` |
| `/api/activities/{id}/comments` | POST | 添加评论 | `{"content": "...", "parentId": null}` | `{"success": true, "data": {"id": 1}}` | `{"success": false, "message": "添加失败"}` |

### 3.5 消息相关API

| 接口路径 | 方法 | 功能描述 | 请求参数 | 成功响应 | 失败响应 |
|---------|------|---------|----------|---------|----------|
| `/api/messages/conversations` | GET | 获取会话列表 | 无 | `{"success": true, "data": [...]}` | `{"success": false, "message": "获取失败"}` |
| `/api/messages/conversations/{userId}` | GET | 获取与指定用户的消息 | `page, pageSize` | `{"success": true, "data": {"list": [...], "total": 100}}` | `{"success": false, "message": "获取失败"}` |
| `/api/messages` | POST | 发送消息 | `{"receiverId": 2, "content": "..."}` | `{"success": true, "data": {"id": 1}}` | `{"success": false, "message": "发送失败"}` |
| `/api/messages/{id}/read` | PUT | 标记消息已读 | 无 | `{"success": true, "message": "已标记"}` | `{"success": false, "message": "操作失败"}` |
| `/api/messages/unread-count` | GET | 获取未读消息数 | 无 | `{"success": true, "data": {"count": 5}}` | `{"success": false, "message": "获取失败"}` |
| `/api/messages/notifications` | GET | 获取通知列表 | `type, page, pageSize` | `{"success": true, "data": {"list": [...], "total": 20}}` | `{"success": false, "message": "获取失败"}` |
| `/api/messages/notifications/{id}/read` | PUT | 标记通知已读 | 无 | `{"success": true, "message": "已标记"}` | `{"success": false, "message": "操作失败"}` |
| `/api/messages/notifications/read-all` | PUT | 标记所有通知已读 | 无 | `{"success": true, "message": "已全部标记"}` | `{"success": false, "message": "操作失败"}` |

### 3.6 统计相关API

| 接口路径 | 方法 | 功能描述 | 请求参数 | 成功响应 | 失败响应 |
|---------|------|---------|----------|---------|----------|
| `/api/stats/user` | GET | 获取用户统计数据 | 无 | `{"success": true, "data": {"joinedClubs": 5, "participatedActivities": 10}}` | `{"success": false, "message": "获取失败"}` |
| `/api/stats/activities` | GET | 获取活动统计数据 | `startDate, endDate, type` | `{"success": true, "data": {...}}` | `{"success": false, "message": "获取失败"}` |
| `/api/stats/club/{id}` | GET | 获取社团统计数据 | 无 | `{"success": true, "data": {"memberCount": 100, "activityCount": 20}}` | `{"success": false, "message": "获取失败"}` |

### 3.7 AI服务API

| 接口路径 | 方法 | 功能描述 | 请求参数 | 成功响应 | 失败响应 |
|---------|------|---------|----------|---------|----------|
| `/api/ai/recommend/clubs` | GET | 获取推荐社团 | `userId` | `{"success": true, "data": [...]}` | `{"success": false, "message": "获取失败"}` |
| `/api/ai/recommend/activities` | GET | 获取推荐活动 | `userId` | `{"success": true, "data": [...]}` | `{"success": false, "message": "获取失败"}` |
| `/api/ai/chat` | POST | 智能问答 | `{"question": "..."}` | `{"success": true, "data": {"answer": "..."}}` | `{"success": false, "message": "处理失败"}` |

## 4. 部署与运行指南

### 4.1 环境准备

- JDK 1.8+
- Maven 3.6+
- MySQL 8.0+
- Redis 5.0+

### 4.2 数据库配置

1. 创建MySQL数据库：
   ```sql
   CREATE DATABASE campus_club DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

2. 创建数据库用户并授权：
   ```sql
   CREATE USER 'campus_club'@'localhost' IDENTIFIED BY 'campus_club_password';
   GRANT ALL PRIVILEGES ON campus_club.* TO 'campus_club'@'localhost';
   FLUSH PRIVILEGES;
   ```

### 4.3 配置修改

1. 修改各服务的`application.yml`文件中的数据库连接信息：
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/campus_club?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=UTC
       username: campus_club
       password: campus_club_password
   ```

2. 修改Redis连接信息：
   ```yaml
   spring:
     redis:
       host: localhost
       port: 6379
       password: your_redis_password
       database: 0
   ```

### 4.4 启动服务

按照以下顺序启动服务：

1. 先启动注册中心：
   ```bash
   cd Backend/registry-server
   mvn spring-boot:run
   ```

2. 启动API网关：
   ```bash
   cd Backend/api-gateway
   mvn spring-boot:run
   ```

3. 启动其他微服务：
   ```bash
   cd Backend/auth-service
   mvn spring-boot:run
   
   cd Backend/user-service
   mvn spring-boot:run
   
   cd Backend/club-service
   mvn spring-boot:run
   
   cd Backend/activity-service
   mvn spring-boot:run
   
   cd Backend/message-service
   mvn spring-boot:run
   
   cd Backend/stats-service
   mvn spring-boot:run
   
   cd Backend/ai-service
   mvn spring-boot:run
   ```

### 4.5 使用Docker部署（可选）

1. 构建各服务的Docker镜像：
   ```bash
   # 在每个服务目录下执行
   mvn clean package
   docker build -t campus-club/{service-name}:1.0 .
   ```

2. 使用Docker Compose启动所有服务：
   ```yaml
   # docker-compose.yml
   version: '3'
   services:
     registry-server:
       image: campus-club/registry-server:1.0
       ports:
         - "8761:8761"
     
     mysql:
       image: mysql:8.0
       environment:
         MYSQL_DATABASE: campus_club_system
         MYSQL_USER: root
         MYSQL_PASSWORD: 1234
         MYSQL_ROOT_PASSWORD: 1234
       ports:
         - "3306:3306"
     
     redis:
       image: redis:5.0
       ports:
         - "6379:6379"
     
     # 其他服务配置...
   ```

   然后执行：
   ```bash
   docker-compose up -d
   ```

## 5. 开发指南

### 5.1 项目结构

每个微服务采用标准的Spring Boot项目结构：

```
src/main/java/com/campusclub/{service}/
├── config/         # 配置类
├── controller/     # 控制器
├── dto/            # 数据传输对象
├── entity/         # 实体类
├── repository/     # 数据访问层
├── service/        # 服务层
│   └── impl/       # 服务实现类
├── utils/          # 工具类
├── exception/      # 异常处理
└── {Service}Application.java  # 应用入口
```

### 5.2 开发流程

1. 克隆代码仓库
2. 创建新的分支进行开发
3. 实现功能（遵循三层架构）
4. 编写单元测试
5. 提交代码并发起PR

### 5.3 代码规范

- 类名使用大驼峰命名法
- 方法名和变量名使用小驼峰命名法
- 常量使用全大写加下划线
- 使用Javadoc注释关键类和方法
- 遵循Spring Boot最佳实践

## 6. 安全配置

### 6.1 JWT配置

系统使用JWT进行身份认证，相关配置：

- JWT密钥：`campus_club_management_system_secret_key_2025`
- 访问令牌过期时间：24小时
- 刷新令牌过期时间：7天

### 6.2 密码加密

密码使用SHA256加密，并添加盐值：`campus_club_salt_2025`

### 6.3 权限控制

系统实现了基于角色的权限控制（RBAC）：

- ADMIN：系统管理员，拥有所有权限
- CLUB_ADMIN：社团管理员，管理社团相关功能
- STUDENT：普通学生，使用基础功能

## 7. 常见问题与排查

### 7.1 服务注册失败

- 检查Eureka服务器是否正常运行
- 检查网络连接是否正常
- 检查服务配置中的Eureka URL是否正确

### 7.2 数据库连接失败

- 检查MySQL服务是否正常运行
- 验证数据库用户名和密码是否正确
- 确认数据库是否已创建

### 7.3 权限不足错误

- 检查用户角色是否正确
- 验证JWT令牌是否有效
- 检查接口权限配置是否正确

### 7.4 跨域问题

系统已在API网关配置了CORS，允许所有来源的请求。如需修改，请调整`api-gateway`的`application.yml`文件中的CORS配置。

## 8. 版本历史

- **v1.0.0**：初始版本，包含核心功能
- **v1.1.0**：添加活动统计和数据分析功能
- **v1.2.0**：优化用户体验，添加实时消息推送

---

本文档由校园社团管理系统开发团队维护，如有问题请联系技术支持。