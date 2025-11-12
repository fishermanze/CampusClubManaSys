-- campus_club_system.message_read_records definition

CREATE TABLE `message_read_records` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `is_muted` bit(1) NOT NULL,
  `is_sticky` bit(1) NOT NULL,
  `last_read_message_id` bigint DEFAULT NULL,
  `last_read_time` datetime(6) DEFAULT NULL,
  `other_user_id` bigint NOT NULL,
  `unread_count` int NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- campus_club_system.stat_data definition

CREATE TABLE `stat_data` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `club_id` bigint DEFAULT NULL,
  `data_key` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `data_value` double DEFAULT NULL,
  `extra_info` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `stat_date` datetime(6) DEFAULT NULL,
  `stat_type` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- campus_club_system.users definition

CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `real_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '真实姓名',
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '123456' COMMENT '密码哈希值',
  `role` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色: STUDENT, TEACHER, ADMIN, CLUB_ADMIN',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE, INACTIVE, SUSPENDED',
  `last_login` datetime DEFAULT NULL COMMENT '最后登录时间',
  `login_count` int DEFAULT '0' COMMENT '登录次数',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `avatar` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `department` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `enabled` bit(1) NOT NULL,
  `grade` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `hobbies` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `phone` (`phone`),
  KEY `idx_users_username` (`username`),
  KEY `idx_users_email` (`email`),
  KEY `idx_users_phone` (`phone`),
  KEY `idx_users_role` (`role`),
  KEY `idx_users_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- campus_club_system.clubs definition

CREATE TABLE `clubs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '社团名称',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '社团描述',
  `content` longtext COLLATE utf8mb4_unicode_ci COMMENT '社团详细介绍(富文本)',
  `category` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '社团类别',
  `founder_id` bigint NOT NULL COMMENT '创始人ID',
  `advisor_id` bigint DEFAULT NULL COMMENT '指导老师ID',
  `status` int NOT NULL DEFAULT '0' COMMENT '状态: 0-待审核 1-正常 2-暂停 3-解散',
  `member_count` int DEFAULT '0' COMMENT '成员数量',
  `establish_date` date DEFAULT NULL COMMENT '成立日期',
  `logo_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '社团Logo',
  `contact_email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '联系邮箱',
  `contact_phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '联系电话',
  `activity_count` int DEFAULT '0' COMMENT '活动数量',
  `rating` decimal(3,2) DEFAULT '0.00' COMMENT '社团评分',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `contact_info` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` datetime(6) DEFAULT NULL,
  `leader_id` bigint DEFAULT NULL,
  `logo` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `total_activity_count` int DEFAULT NULL,
  `update_time` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  KEY `idx_clubs_name` (`name`),
  KEY `idx_clubs_category` (`category`),
  KEY `idx_clubs_status` (`status`),
  KEY `idx_clubs_founder` (`founder_id`),
  KEY `idx_clubs_advisor` (`advisor_id`),
  CONSTRAINT `clubs_ibfk_1` FOREIGN KEY (`founder_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `clubs_ibfk_2` FOREIGN KEY (`advisor_id`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- campus_club_system.messages definition

CREATE TABLE `messages` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `sender_id` bigint NOT NULL COMMENT '发送者ID',
  `receiver_id` bigint NOT NULL COMMENT '接收者ID',
  `message_type` int NOT NULL COMMENT '消息类型: 1-聊天消息 2-通知消息',
  `content` varchar(2000) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息内容',
  `status` int NOT NULL DEFAULT '0' COMMENT '状态: 0-未读 1-已读 2-已删除',
  `related_id` bigint DEFAULT NULL COMMENT '关联ID',
  `related_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '关联类型',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_messages_sender_id` (`sender_id`),
  KEY `idx_messages_receiver_id` (`receiver_id`),
  KEY `idx_messages_status` (`status`),
  KEY `idx_messages_created_at` (`created_at`),
  CONSTRAINT `messages_ibfk_1` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `messages_ibfk_2` FOREIGN KEY (`receiver_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- campus_club_system.notifications definition

CREATE TABLE `notifications` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '接收者ID',
  `notification_type` int NOT NULL COMMENT '通知类型: 1-系统通知 2-社团通知 3-活动通知 4-互动通知',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '通知标题',
  `content` varchar(2000) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '通知内容',
  `status` int NOT NULL DEFAULT '0' COMMENT '状态: 0-未读 1-已读',
  `related_id` bigint DEFAULT NULL COMMENT '关联ID',
  `related_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '关联类型',
  `need_push` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否需要推送',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `idx_notifications_notification_type` (`notification_type`),
  KEY `idx_notifications_status` (`status`),
  KEY `idx_notifications_created_at` (`created_at`),
  CONSTRAINT `notifications_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- campus_club_system.user_follows definition

CREATE TABLE `user_follows` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `follower_id` bigint NOT NULL COMMENT '关注者ID',
  `followed_id` bigint NOT NULL COMMENT '被关注者ID',
  `is_mutual` tinyint(1) DEFAULT '0' COMMENT '是否互相关注',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_follows` (`follower_id`,`followed_id`),
  UNIQUE KEY `UKt21sqiubk1nu5epc9yieinc6h` (`follower_id`,`followed_id`),
  KEY `idx_user_follows_follower` (`follower_id`),
  KEY `idx_user_follows_followed` (`followed_id`),
  CONSTRAINT `user_follows_ibfk_1` FOREIGN KEY (`follower_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `user_follows_ibfk_2` FOREIGN KEY (`followed_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- campus_club_system.user_profiles definition

CREATE TABLE `user_profiles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `student_id` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '学号',
  `major` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '专业',
  `grade` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '年级',
  `class_name` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '班级',
  `gender` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '性别',
  `birth_date` datetime(6) DEFAULT NULL,
  `hobbies` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '爱好',
  `bio` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '个人简介',
  `address` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '地址',
  `emergency_contact` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '紧急联系人',
  `emergency_phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '紧急联系电话',
  `social_media` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '社交媒体信息(JSON)',
  `preferred_club_types` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '偏好社团类型(JSON)',
  `avatar` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像URL',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`),
  KEY `idx_user_profiles_student_id` (`student_id`),
  KEY `idx_user_profiles_major` (`major`),
  KEY `idx_user_profiles_grade` (`grade`),
  CONSTRAINT `user_profiles_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- campus_club_system.activities definition

CREATE TABLE `activities` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '活动名称',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '活动描述',
  `content` longtext COLLATE utf8mb4_unicode_ci COMMENT '活动内容(富文本)',
  `cover_image` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '活动封面图',
  `images` text COLLATE utf8mb4_unicode_ci COMMENT '活动图片列表(JSON)',
  `start_time` datetime NOT NULL COMMENT '活动开始时间',
  `end_time` datetime NOT NULL COMMENT '活动结束时间',
  `location` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '活动地点',
  `organizer_id` bigint NOT NULL COMMENT '活动负责人ID',
  `club_id` bigint NOT NULL COMMENT '社团ID',
  `status` int NOT NULL DEFAULT '0' COMMENT '状态: 0-草稿 1-已发布 2-进行中 3-已完成 4-已取消',
  `max_participants` int DEFAULT NULL COMMENT '最大参与人数',
  `current_participants` int DEFAULT '0' COMMENT '当前参与人数',
  `enrollment_deadline` datetime DEFAULT NULL COMMENT '报名截止时间',
  `tags` text COLLATE utf8mb4_unicode_ci COMMENT '活动标签(JSON)',
  `need_approval` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否需要审核',
  `view_count` int DEFAULT '0' COMMENT '浏览次数',
  `like_count` int DEFAULT '0' COMMENT '点赞数',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_activities_club_id` (`club_id`),
  KEY `idx_activities_organizer_id` (`organizer_id`),
  KEY `idx_activities_status` (`status`),
  KEY `idx_activities_start_time` (`start_time`),
  KEY `idx_activities_end_time` (`end_time`),
  KEY `idx_activities_title` (`title`),
  CONSTRAINT `activities_ibfk_1` FOREIGN KEY (`organizer_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `activities_ibfk_2` FOREIGN KEY (`club_id`) REFERENCES `clubs` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- campus_club_system.activity_comments definition

CREATE TABLE `activity_comments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `activity_id` bigint NOT NULL COMMENT '活动ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `content` varchar(2000) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '评论内容',
  `parent_id` bigint DEFAULT NULL COMMENT '父评论ID(用于回复)',
  `likes_count` int DEFAULT '0' COMMENT '点赞数',
  `status` int NOT NULL DEFAULT '0' COMMENT '状态: 0-正常 1-已删除 2-已禁用',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `activity_id` (`activity_id`),
  KEY `user_id` (`user_id`),
  KEY `idx_activity_comments_parent_id` (`parent_id`),
  KEY `idx_activity_comments_created_at` (`created_at`),
  CONSTRAINT `activity_comments_ibfk_1` FOREIGN KEY (`activity_id`) REFERENCES `activities` (`id`) ON DELETE CASCADE,
  CONSTRAINT `activity_comments_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `activity_comments_ibfk_3` FOREIGN KEY (`parent_id`) REFERENCES `activity_comments` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- campus_club_system.activity_likes definition

CREATE TABLE `activity_likes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `activity_id` bigint NOT NULL COMMENT '活动ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_activity_likes` (`activity_id`,`user_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `activity_likes_ibfk_1` FOREIGN KEY (`activity_id`) REFERENCES `activities` (`id`) ON DELETE CASCADE,
  CONSTRAINT `activity_likes_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- campus_club_system.activity_participants definition

CREATE TABLE `activity_participants` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `activity_id` bigint NOT NULL COMMENT '活动ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `status` int NOT NULL DEFAULT '0' COMMENT '状态: 0-待审核 1-已通过 2-已拒绝 3-已参加 4-未参加',
  `enrollment_time` datetime NOT NULL COMMENT '报名时间',
  `approval_time` datetime DEFAULT NULL COMMENT '审核时间',
  `approved_by` bigint DEFAULT NULL COMMENT '审核人ID',
  `approval_remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '审核备注',
  `check_in_time` datetime DEFAULT NULL COMMENT '签到时间',
  `check_out_time` datetime DEFAULT NULL COMMENT '退签时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `enrollment_info` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_activity_participants` (`activity_id`,`user_id`),
  KEY `user_id` (`user_id`),
  KEY `approved_by` (`approved_by`),
  KEY `idx_activity_participants_status` (`status`),
  CONSTRAINT `activity_participants_ibfk_1` FOREIGN KEY (`activity_id`) REFERENCES `activities` (`id`) ON DELETE CASCADE,
  CONSTRAINT `activity_participants_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `activity_participants_ibfk_3` FOREIGN KEY (`approved_by`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- campus_club_system.club_applications definition

CREATE TABLE `club_applications` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `club_id` bigint NOT NULL COMMENT '社团ID',
  `user_id` bigint NOT NULL COMMENT '申请人ID',
  `apply_reason` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '申请理由',
  `status` int NOT NULL DEFAULT '0' COMMENT '状态: 0-待审核 1-已通过 2-已拒绝',
  `review_time` datetime DEFAULT NULL COMMENT '审核时间',
  `reviewer_id` bigint DEFAULT NULL COMMENT '审核人ID',
  `review_remark` text COLLATE utf8mb4_unicode_ci COMMENT '审核备注',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `club_id` (`club_id`),
  KEY `user_id` (`user_id`),
  KEY `reviewer_id` (`reviewer_id`),
  KEY `idx_club_applications_status` (`status`),
  KEY `idx_club_applications_created_at` (`created_at`),
  CONSTRAINT `club_applications_ibfk_1` FOREIGN KEY (`club_id`) REFERENCES `clubs` (`id`) ON DELETE CASCADE,
  CONSTRAINT `club_applications_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `club_applications_ibfk_3` FOREIGN KEY (`reviewer_id`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- campus_club_system.club_members definition

CREATE TABLE `club_members` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `club_id` bigint NOT NULL COMMENT '社团ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role` int NOT NULL DEFAULT '0',
  `join_time` datetime NOT NULL COMMENT '加入时间',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态: 0-待审核 1-正常 2-已退出 3-已踢出',
  `last_active_time` datetime DEFAULT NULL COMMENT '最后活跃时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `activity_count` int DEFAULT NULL,
  `level` int DEFAULT NULL,
  `total_score` double DEFAULT NULL,
  `update_time` datetime(6) DEFAULT NULL,
  `year_evaluation_status` int DEFAULT NULL,
  `class_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `gender` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `major` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `real_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `reason` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_club_members` (`club_id`,`user_id`),
  KEY `user_id` (`user_id`),
  KEY `idx_club_members_role` (`role`),
  KEY `idx_club_members_status` (`status`),
  CONSTRAINT `club_members_ibfk_1` FOREIGN KEY (`club_id`) REFERENCES `clubs` (`id`) ON DELETE CASCADE,
  CONSTRAINT `club_members_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=64 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- campus_club_system.comment_likes definition

CREATE TABLE `comment_likes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `comment_id` bigint NOT NULL COMMENT '评论ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_comment_likes` (`comment_id`,`user_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `comment_likes_ibfk_1` FOREIGN KEY (`comment_id`) REFERENCES `activity_comments` (`id`) ON DELETE CASCADE,
  CONSTRAINT `comment_likes_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- campus_club_system.conversations definition

CREATE TABLE `conversations` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user1_id` bigint NOT NULL COMMENT '用户1 ID',
  `user2_id` bigint NOT NULL COMMENT '用户2 ID',
  `last_message_id` bigint DEFAULT NULL COMMENT '最后一条消息ID',
  `unread_count` int DEFAULT '0' COMMENT '未读消息数',
  `is_sticky` tinyint(1) DEFAULT '0' COMMENT '是否置顶',
  `is_muted` tinyint(1) DEFAULT '0' COMMENT '是否免打扰',
  `last_message_time` datetime DEFAULT NULL COMMENT '最后消息时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_conversations` (`user1_id`,`user2_id`),
  KEY `user2_id` (`user2_id`),
  KEY `last_message_id` (`last_message_id`),
  KEY `idx_conversations_last_message_time` (`last_message_time`),
  CONSTRAINT `conversations_ibfk_1` FOREIGN KEY (`user1_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `conversations_ibfk_2` FOREIGN KEY (`user2_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `conversations_ibfk_3` FOREIGN KEY (`last_message_id`) REFERENCES `messages` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



