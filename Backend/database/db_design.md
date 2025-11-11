# 校园社团管理系统数据库设计文档

## 1. 系统概述

校园社团管理系统是一个基于微服务架构的分布式系统，包含用户服务、社团服务、活动服务、消息服务等多个核心模块。本数据库设计根据系统的业务需求和功能模块，设计了完整的关系型数据库结构。

## 2. 数据库设计原则

- **高内聚、低耦合**：每个微服务拥有独立的数据库或表空间
- **数据完整性**：通过外键约束、唯一索引等确保数据一致性
- **可扩展性**：设计考虑未来业务增长和功能扩展
- **性能优化**：合理设计索引，优化查询性能
- **安全性**：敏感数据加密存储，权限控制

## 3. 数据库表结构设计

### 3.1 用户服务 (user-service)

#### 3.1.1 users表

| 字段名 | 数据类型 | 约束 | 描述 |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY, AUTO_INCREMENT` | 用户ID |
| `username` | `VARCHAR(50)` | `NOT NULL, UNIQUE` | 用户名 |
| `real_name` | `VARCHAR(100)` | | 真实姓名 |
| `email` | `VARCHAR(100)` | `UNIQUE` | 邮箱 |
| `phone` | `VARCHAR(20)` | `UNIQUE` | 手机号 |
| `password_hash` | `VARCHAR(255)` | `NOT NULL` | 密码哈希值 |
| `role` | `VARCHAR(50)` | `NOT NULL` | 角色(STUDENT, TEACHER, ADMIN, CLUB_ADMIN) |
| `status` | `VARCHAR(20)` | `NOT NULL DEFAULT 'ACTIVE'` | 状态(ACTIVE, INACTIVE, SUSPENDED) |
| `last_login` | `DATETIME` | | 最后登录时间 |
| `login_count` | `INT` | `DEFAULT 0` | 登录次数 |
| `created_at` | `DATETIME` | `NOT NULL` | 创建时间 |
| `updated_at` | `DATETIME` | `NOT NULL` | 更新时间 |

#### 3.1.2 user_profiles表

| 字段名 | 数据类型 | 约束 | 描述 |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY, AUTO_INCREMENT` | 档案ID |
| `user_id` | `BIGINT` | `NOT NULL, UNIQUE, FOREIGN KEY` | 用户ID |
| `student_id` | `VARCHAR(20)` | | 学号 |
| `major` | `VARCHAR(100)` | | 专业 |
| `grade` | `VARCHAR(20)` | | 年级 |
| `class_name` | `VARCHAR(20)` | | 班级 |
| `gender` | `VARCHAR(10)` | | 性别 |
| `birth_date` | `DATE` | | 出生日期 |
| `hobbies` | `VARCHAR(500)` | | 爱好 |
| `bio` | `VARCHAR(1000)` | | 个人简介 |
| `address` | `VARCHAR(500)` | | 地址 |
| `emergency_contact` | `VARCHAR(20)` | | 紧急联系人 |
| `emergency_phone` | `VARCHAR(20)` | | 紧急联系电话 |
| `social_media` | `VARCHAR(500)` | | 社交媒体信息(JSON) |
| `preferred_club_types` | `VARCHAR(500)` | | 偏好社团类型(JSON) |
| `avatar` | `VARCHAR(500)` | | 头像URL |
| `created_at` | `DATETIME` | `NOT NULL` | 创建时间 |
| `updated_at` | `DATETIME` | `NOT NULL` | 更新时间 |

#### 3.1.3 user_follows表

| 字段名 | 数据类型 | 约束 | 描述 |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY, AUTO_INCREMENT` | 关注ID |
| `follower_id` | `BIGINT` | `NOT NULL, FOREIGN KEY` | 关注者ID |
| `followed_id` | `BIGINT` | `NOT NULL, FOREIGN KEY` | 被关注者ID |
| `is_mutual` | `BOOLEAN` | `DEFAULT FALSE` | 是否互相关注 |
| `created_at` | `DATETIME` | `NOT NULL` | 创建时间 |

### 3.2 社团服务 (club-service)

#### 3.2.1 clubs表

| 字段名 | 数据类型 | 约束 | 描述 |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY, AUTO_INCREMENT` | 社团ID |
| `name` | `VARCHAR(200)` | `NOT NULL, UNIQUE` | 社团名称 |
| `description` | `TEXT` | | 社团描述 |
| `content` | `LONGTEXT` | | 社团详细介绍(富文本) |
| `category` | `VARCHAR(50)` | `NOT NULL` | 社团类别 |
| `founder_id` | `BIGINT` | `NOT NULL, FOREIGN KEY` | 创始人ID |
| `advisor_id` | `BIGINT` | `FOREIGN KEY` | 指导老师ID |
| `status` | `INT` | `NOT NULL DEFAULT 0` | 状态(0-待审核 1-正常 2-暂停 3-解散) |
| `member_count` | `INT` | `DEFAULT 0` | 成员数量 |
| `establish_date` | `DATE` | | 成立日期 |
| `logo_url` | `VARCHAR(500)` | | 社团Logo |
| `contact_email` | `VARCHAR(100)` | | 联系邮箱 |
| `contact_phone` | `VARCHAR(20)` | | 联系电话 |
| `activity_count` | `INT` | `DEFAULT 0` | 活动数量 |
| `rating` | `DECIMAL(3,2)` | `DEFAULT 0.00` | 社团评分 |
| `created_at` | `DATETIME` | `NOT NULL` | 创建时间 |
| `updated_at` | `DATETIME` | `NOT NULL` | 更新时间 |

#### 3.2.2 club_members表

| 字段名 | 数据类型 | 约束 | 描述 |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY, AUTO_INCREMENT` | 成员ID |
| `club_id` | `BIGINT` | `NOT NULL, FOREIGN KEY` | 社团ID |
| `user_id` | `BIGINT` | `NOT NULL, FOREIGN KEY` | 用户ID |
| `role` | `VARCHAR(50)` | `NOT NULL` | 成员角色(PRESIDENT, VICE_PRESIDENT, MANAGER, MEMBER) |
| `join_time` | `DATETIME` | `NOT NULL` | 加入时间 |
| `status` | `INT` | `NOT NULL DEFAULT 1` | 状态(0-待审核 1-正常 2-已退出 3-已踢出) |
| `last_active_time` | `DATETIME` | | 最后活跃时间 |
| `created_at` | `DATETIME` | `NOT NULL` | 创建时间 |
| `updated_at` | `DATETIME` | `NOT NULL` | 更新时间 |

#### 3.2.3 club_applications表

| 字段名 | 数据类型 | 约束 | 描述 |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY, AUTO_INCREMENT` | 申请ID |
| `club_id` | `BIGINT` | `NOT NULL, FOREIGN KEY` | 社团ID |
| `user_id` | `BIGINT` | `NOT NULL, FOREIGN KEY` | 申请人ID |
| `apply_reason` | `TEXT` | `NOT NULL` | 申请理由 |
| `status` | `INT` | `NOT NULL DEFAULT 0` | 状态(0-待审核 1-已通过 2-已拒绝) |
| `review_time` | `DATETIME` | | 审核时间 |
| `reviewer_id` | `BIGINT` | `FOREIGN KEY` | 审核人ID |
| `review_remark` | `TEXT` | | 审核备注 |
| `created_at` | `DATETIME` | `NOT NULL` | 创建时间 |
| `updated_at` | `DATETIME` | `NOT NULL` | 更新时间 |

### 3.3 活动服务 (activity-service)

#### 3.3.1 activities表

| 字段名 | 数据类型 | 约束 | 描述 |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY, AUTO_INCREMENT` | 活动ID |
| `title` | `VARCHAR(200)` | `NOT NULL` | 活动名称 |
| `description` | `TEXT` | | 活动描述 |
| `content` | `LONGTEXT` | | 活动内容(富文本) |
| `cover_image` | `VARCHAR(500)` | | 活动封面图 |
| `images` | `TEXT` | | 活动图片列表(JSON) |
| `start_time` | `DATETIME` | `NOT NULL` | 活动开始时间 |
| `end_time` | `DATETIME` | `NOT NULL` | 活动结束时间 |
| `location` | `VARCHAR(500)` | `NOT NULL` | 活动地点 |
| `organizer_id` | `BIGINT` | `NOT NULL, FOREIGN KEY` | 活动负责人ID |
| `club_id` | `BIGINT` | `NOT NULL, FOREIGN KEY` | 社团ID |
| `status` | `INT` | `NOT NULL DEFAULT 0` | 状态(0-草稿 1-已发布 2-进行中 3-已完成 4-已取消) |
| `max_participants` | `INT` | | 最大参与人数 |
| `current_participants` | `INT` | `DEFAULT 0` | 当前参与人数 |
| `enrollment_deadline` | `DATETIME` | | 报名截止时间 |
| `tags` | `TEXT` | | 活动标签(JSON) |
| `need_approval` | `BOOLEAN` | `NOT NULL DEFAULT FALSE` | 是否需要审核 |
| `view_count` | `INT` | `DEFAULT 0` | 浏览次数 |
| `like_count` | `INT` | `DEFAULT 0` | 点赞数 |
| `created_at` | `DATETIME` | `NOT NULL` | 创建时间 |
| `updated_at` | `DATETIME` | `NOT NULL` | 更新时间 |

#### 3.3.2 activity_participants表

| 字段名 | 数据类型 | 约束 | 描述 |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY, AUTO_INCREMENT` | 参与记录ID |
| `activity_id` | `BIGINT` | `NOT NULL, FOREIGN KEY` | 活动ID |
| `user_id` | `BIGINT` | `NOT NULL, FOREIGN KEY` | 用户ID |
| `status` | `INT` | `NOT NULL DEFAULT 0` | 状态(0-待审核 1-已通过 2-已拒绝 3-已参加 4-未参加) |
| `enrollment_time` | `DATETIME` | `NOT NULL` | 报名时间 |
| `approval_time` | `DATETIME` | | 审核时间 |
| `approved_by` | `BIGINT` | `FOREIGN KEY` | 审核人ID |
| `approval_remark` | `VARCHAR(500)` | | 审核备注 |
| `check_in_time` | `DATETIME` | | 签到时间 |
| `check_out_time` | `DATETIME` | | 退签时间 |
| `created_at` | `DATETIME` | `NOT NULL` | 创建时间 |
| `updated_at` | `DATETIME` | `NOT NULL` | 更新时间 |

#### 3.3.3 activity_comments表

| 字段名 | 数据类型 | 约束 | 描述 |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY, AUTO_INCREMENT` | 评论ID |
| `activity_id` | `BIGINT` | `NOT NULL, FOREIGN KEY` | 活动ID |
| `user_id` | `BIGINT` | `NOT NULL, FOREIGN KEY` | 用户ID |
| `content` | `VARCHAR(2000)` | `NOT NULL` | 评论内容 |
| `parent_id` | `BIGINT` | `FOREIGN KEY` | 父评论ID(用于回复) |
| `likes_count` | `INT` | `DEFAULT 0` | 点赞数 |
| `status` | `INT` | `NOT NULL DEFAULT 0` | 状态(0-正常 1-已删除 2-已禁用) |
| `created_at` | `DATETIME` | `NOT NULL` | 创建时间 |
| `updated_at` | `DATETIME` | `NOT NULL` | 更新时间 |

#### 3.3.4 activity_likes表

| 字段名 | 数据类型 | 约束 | 描述 |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY, AUTO_INCREMENT` | 点赞ID |
| `activity_id` | `BIGINT` | `NOT NULL, FOREIGN KEY` | 活动ID |
| `user_id` | `BIGINT` | `NOT NULL, FOREIGN KEY` | 用户ID |
| `created_at` | `DATETIME` | `NOT NULL` | 点赞时间 |

#### 3.3.5 comment_likes表

| 字段名 | 数据类型 | 约束 | 描述 |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY, AUTO_INCREMENT` | 点赞ID |
| `comment_id` | `BIGINT` | `NOT NULL, FOREIGN KEY` | 评论ID |
| `user_id` | `BIGINT` | `NOT NULL, FOREIGN KEY` | 用户ID |
| `created_at` | `DATETIME` | `NOT NULL` | 点赞时间 |

### 3.4 消息服务 (message-service)

#### 3.4.1 messages表

| 字段名 | 数据类型 | 约束 | 描述 |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY, AUTO_INCREMENT` | 消息ID |
| `sender_id` | `BIGINT` | `NOT NULL, FOREIGN KEY` | 发送者ID |
| `receiver_id` | `BIGINT` | `NOT NULL, FOREIGN KEY` | 接收者ID |
| `message_type` | `INT` | `NOT NULL` | 消息类型(1-聊天消息 2-通知消息) |
| `content` | `VARCHAR(2000)` | `NOT NULL` | 消息内容 |
| `status` | `INT` | `NOT NULL DEFAULT 0` | 状态(0-未读 1-已读 2-已删除) |
| `related_id` | `BIGINT` | | 关联ID |
| `related_type` | `VARCHAR(50)` | | 关联类型 |
| `created_at` | `DATETIME` | `NOT NULL` | 创建时间 |
| `updated_at` | `DATETIME` | `NOT NULL` | 更新时间 |

#### 3.4.2 notifications表

| 字段名 | 数据类型 | 约束 | 描述 |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY, AUTO_INCREMENT` | 通知ID |
| `user_id` | `BIGINT` | `NOT NULL, FOREIGN KEY` | 接收者ID |
| `notification_type` | `INT` | `NOT NULL` | 通知类型(1-系统通知 2-社团通知 3-活动通知 4-互动通知) |
| `title` | `VARCHAR(200)` | `NOT NULL` | 通知标题 |
| `content` | `VARCHAR(2000)` | `NOT NULL` | 通知内容 |
| `status` | `INT` | `NOT NULL DEFAULT 0` | 状态(0-未读 1-已读) |
| `related_id` | `BIGINT` | | 关联ID |
| `related_type` | `VARCHAR(50)` | | 关联类型 |
| `need_push` | `BOOLEAN` | `NOT NULL DEFAULT TRUE` | 是否需要推送 |
| `created_at` | `DATETIME` | `NOT NULL` | 创建时间 |
| `updated_at` | `DATETIME` | `NOT NULL` | 更新时间 |

#### 3.4.3 conversations表

| 字段名 | 数据类型 | 约束 | 描述 |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY, AUTO_INCREMENT` | 会话ID |
| `user1_id` | `BIGINT` | `NOT NULL, FOREIGN KEY` | 用户1 ID |
| `user2_id` | `BIGINT` | `NOT NULL, FOREIGN KEY` | 用户2 ID |
| `last_message_id` | `BIGINT` | `FOREIGN KEY` | 最后一条消息ID |
| `unread_count` | `INT` | `DEFAULT 0` | 未读消息数 |
| `is_sticky` | `BOOLEAN` | `DEFAULT FALSE` | 是否置顶 |
| `is_muted` | `BOOLEAN` | `DEFAULT FALSE` | 是否免打扰 |
| `last_message_time` | `DATETIME` | | 最后消息时间 |
| `created_at` | `DATETIME` | `NOT NULL` | 创建时间 |
| `updated_at` | `DATETIME` | `NOT NULL` | 更新时间 |

## 4. 数据库关系图

```
用户服务
+----------------+      +----------------+      +----------------+
|     users      |<---->|   user_profiles|<---->|   user_follows |
+----------------+      +----------------+      +----------------+

社团服务
+----------------+      +----------------+      +----------------+
|     clubs      |<---->|  club_members  |      |club_applications|
+----------------+      +----------------+      +----------------+

活动服务
+----------------+      +----------------+      +----------------+
|    activities  |<---->|activity_participants|<---->|activity_likes |
+----------------+      +----------------+      +----------------+
         ^                       ^                   ^
         |                       |                   |
         |                       |                   |
         v                       v                   v
+----------------+      +----------------+      +----------------+
|activity_comments|<---->|  comment_likes |      |                |
+----------------+      +----------------+      +----------------+

消息服务
+----------------+      +----------------+      +----------------+
|    messages    |<---->| conversations  |      |  notifications |
+----------------+      +----------------+      +----------------+
```

## 5. 数据索引设计

### 5.1 用户服务索引
- `users`: `(username)`, `(email)`, `(phone)`, `(role)`, `(status)`
- `user_profiles`: `(user_id)`, `(student_id)`, `(major)`, `(grade)`
- `user_follows`: `(follower_id)`, `(followed_id)`, `(follower_id, followed_id)`

### 5.2 社团服务索引
- `clubs`: `(name)`, `(category)`, `(status)`, `(founder_id)`, `(advisor_id)`
- `club_members`: `(club_id)`, `(user_id)`, `(club_id, user_id)`, `(role)`, `(status)`
- `club_applications`: `(club_id)`, `(user_id)`, `(status)`, `(created_at)`

### 5.3 活动服务索引
- `activities`: `(club_id)`, `(organizer_id)`, `(status)`, `(start_time)`, `(end_time)`, `(title)`
- `activity_participants`: `(activity_id)`, `(user_id)`, `(activity_id, user_id)`, `(status)`
- `activity_comments`: `(activity_id)`, `(user_id)`, `(parent_id)`, `(created_at)`
- `activity_likes`: `(activity_id)`, `(user_id)`, `(activity_id, user_id)`
- `comment_likes`: `(comment_id)`, `(user_id)`, `(comment_id, user_id)`

### 5.4 消息服务索引
- `messages`: `(sender_id)`, `(receiver_id)`, `(status)`, `(created_at)`
- `notifications`: `(user_id)`, `(notification_type)`, `(status)`, `(created_at)`
- `conversations`: `(user1_id)`, `(user2_id)`, `(last_message_time)`

## 6. 数据安全与优化

### 6.1 安全措施
- 密码使用SHA256加密存储
- 敏感数据脱敏处理
- 实施数据访问权限控制
- 定期数据备份

### 6.2 性能优化
- 合理设计索引提高查询效率
- 对频繁查询的表进行缓存
- 分页查询避免大数据量加载
- 合理设置连接池参数

### 6.3 数据一致性
- 使用事务保证数据操作的原子性
- 通过外键约束维护数据完整性
- 分布式事务处理跨服务数据操作

## 7. 数据库维护计划

### 7.1 定期维护任务
- 每日数据备份
- 每周索引重建
- 每月数据库优化分析
- 定期清理过期数据

### 7.2 监控指标
- 数据库连接数
- 查询响应时间
- 慢查询日志分析
- 存储空间使用情况

## 8. 扩展考虑

### 8.1 未来扩展
- 支持分库分表以应对大数据量
- 引入读写分离提高并发性能
- 增加缓存层减少数据库压力
- 考虑NoSQL数据库用于特定场景