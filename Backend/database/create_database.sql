-- 校园社团管理系统数据库创建脚本
-- MySQL 8.0+ 兼容

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS campus_club_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE campus_club_system;

-- 设置时区（可选）
SET GLOBAL time_zone = '+8:00';

-- 创建用户服务相关表

-- 1. 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    real_name VARCHAR(100) COMMENT '真实姓名',
    email VARCHAR(100) UNIQUE COMMENT '邮箱',
    phone VARCHAR(20) UNIQUE COMMENT '手机号',
    password_hash VARCHAR(255) NOT NULL COMMENT '密码哈希值',
    role VARCHAR(50) NOT NULL COMMENT '角色: STUDENT, TEACHER, ADMIN, CLUB_ADMIN',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE, INACTIVE, SUSPENDED',
    last_login DATETIME COMMENT '最后登录时间',
    login_count INT DEFAULT 0 COMMENT '登录次数',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建用户表索引
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_phone ON users(phone);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_status ON users(status);

-- 2. 用户档案表
CREATE TABLE IF NOT EXISTS user_profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE COMMENT '用户ID',
    student_id VARCHAR(20) COMMENT '学号',
    major VARCHAR(100) COMMENT '专业',
    grade VARCHAR(20) COMMENT '年级',
    class_name VARCHAR(20) COMMENT '班级',
    gender VARCHAR(10) COMMENT '性别',
    birth_date DATE COMMENT '出生日期',
    hobbies VARCHAR(500) COMMENT '爱好',
    bio VARCHAR(1000) COMMENT '个人简介',
    address VARCHAR(500) COMMENT '地址',
    emergency_contact VARCHAR(20) COMMENT '紧急联系人',
    emergency_phone VARCHAR(20) COMMENT '紧急联系电话',
    social_media VARCHAR(500) COMMENT '社交媒体信息(JSON)',
    preferred_club_types VARCHAR(500) COMMENT '偏好社团类型(JSON)',
    avatar VARCHAR(500) COMMENT '头像URL',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建用户档案表索引
CREATE INDEX idx_user_profiles_student_id ON user_profiles(student_id);
CREATE INDEX idx_user_profiles_major ON user_profiles(major);
CREATE INDEX idx_user_profiles_grade ON user_profiles(grade);

-- 3. 用户关注表
CREATE TABLE IF NOT EXISTS user_follows (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    follower_id BIGINT NOT NULL COMMENT '关注者ID',
    followed_id BIGINT NOT NULL COMMENT '被关注者ID',
    is_mutual BOOLEAN DEFAULT FALSE COMMENT '是否互相关注',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_follows (follower_id, followed_id),
    FOREIGN KEY (follower_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (followed_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建用户关注表索引
CREATE INDEX idx_user_follows_follower ON user_follows(follower_id);

-- 创建社团服务相关表

-- 4. 社团表
CREATE TABLE IF NOT EXISTS clubs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL UNIQUE COMMENT '社团名称',
    description TEXT COMMENT '社团描述',
    content LONGTEXT COMMENT '社团详细介绍(富文本)',
    category VARCHAR(50) NOT NULL COMMENT '社团类别',
    founder_id BIGINT NOT NULL COMMENT '创始人ID',
    advisor_id BIGINT COMMENT '指导老师ID',
    status INT NOT NULL DEFAULT 0 COMMENT '状态: 0-待审核 1-正常 2-暂停 3-解散',
    member_count INT DEFAULT 0 COMMENT '成员数量',
    establish_date DATE COMMENT '成立日期',
    logo_url VARCHAR(500) COMMENT '社团Logo',
    contact_email VARCHAR(100) COMMENT '联系邮箱',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    activity_count INT DEFAULT 0 COMMENT '活动数量',
    rating DECIMAL(3,2) DEFAULT 0.00 COMMENT '社团评分',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (founder_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (advisor_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建社团表索引
CREATE INDEX idx_clubs_name ON clubs(name);
CREATE INDEX idx_clubs_category ON clubs(category);
CREATE INDEX idx_clubs_status ON clubs(status);
CREATE INDEX idx_clubs_founder ON clubs(founder_id);
CREATE INDEX idx_clubs_advisor ON clubs(advisor_id);

-- 5. 社团成员表
CREATE TABLE IF NOT EXISTS club_members (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    club_id BIGINT NOT NULL COMMENT '社团ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role VARCHAR(50) NOT NULL COMMENT '成员角色: PRESIDENT, VICE_PRESIDENT, MANAGER, MEMBER',
    join_time DATETIME NOT NULL COMMENT '加入时间',
    status INT NOT NULL DEFAULT 1 COMMENT '状态: 0-待审核 1-正常 2-已退出 3-已踢出',
    last_active_time DATETIME COMMENT '最后活跃时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_club_members (club_id, user_id),
    FOREIGN KEY (club_id) REFERENCES clubs(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建社团成员表索引
CREATE INDEX idx_club_members_role ON club_members(role);
CREATE INDEX idx_club_members_status ON club_members(status);

-- 6. 社团申请表
CREATE TABLE IF NOT EXISTS club_applications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    club_id BIGINT NOT NULL COMMENT '社团ID',
    user_id BIGINT NOT NULL COMMENT '申请人ID',
    apply_reason TEXT NOT NULL COMMENT '申请理由',
    status INT NOT NULL DEFAULT 0 COMMENT '状态: 0-待审核 1-已通过 2-已拒绝',
    review_time DATETIME COMMENT '审核时间',
    reviewer_id BIGINT COMMENT '审核人ID',
    review_remark TEXT COMMENT '审核备注',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (club_id) REFERENCES clubs(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (reviewer_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建社团申请表索引
CREATE INDEX idx_club_applications_status ON club_applications(status);
CREATE INDEX idx_club_applications_created_at ON club_applications(created_at);

-- 创建活动服务相关表

-- 7. 活动表
CREATE TABLE IF NOT EXISTS activities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL COMMENT '活动名称',
    description TEXT COMMENT '活动描述',
    content LONGTEXT COMMENT '活动内容(富文本)',
    cover_image VARCHAR(500) COMMENT '活动封面图',
    images TEXT COMMENT '活动图片列表(JSON)',
    start_time DATETIME NOT NULL COMMENT '活动开始时间',
    end_time DATETIME NOT NULL COMMENT '活动结束时间',
    location VARCHAR(500) NOT NULL COMMENT '活动地点',
    organizer_id BIGINT NOT NULL COMMENT '活动负责人ID',
    club_id BIGINT NOT NULL COMMENT '社团ID',
    status INT NOT NULL DEFAULT 0 COMMENT '状态: 0-草稿 1-已发布 2-进行中 3-已完成 4-已取消',
    max_participants INT COMMENT '最大参与人数',
    current_participants INT DEFAULT 0 COMMENT '当前参与人数',
    enrollment_deadline DATETIME COMMENT '报名截止时间',
    tags TEXT COMMENT '活动标签(JSON)',
    need_approval BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否需要审核',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (organizer_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (club_id) REFERENCES clubs(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建活动表索引
CREATE INDEX idx_activities_club_id ON activities(club_id);
CREATE INDEX idx_activities_organizer_id ON activities(organizer_id);
CREATE INDEX idx_activities_status ON activities(status);
CREATE INDEX idx_activities_start_time ON activities(start_time);
CREATE INDEX idx_activities_end_time ON activities(end_time);
CREATE INDEX idx_activities_title ON activities(title);

-- 8. 活动参与记录表
CREATE TABLE IF NOT EXISTS activity_participants (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    activity_id BIGINT NOT NULL COMMENT '活动ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    status INT NOT NULL DEFAULT 0 COMMENT '状态: 0-待审核 1-已通过 2-已拒绝 3-已参加 4-未参加',
    enrollment_time DATETIME NOT NULL COMMENT '报名时间',
    approval_time DATETIME COMMENT '审核时间',
    approved_by BIGINT COMMENT '审核人ID',
    approval_remark VARCHAR(500) COMMENT '审核备注',
    check_in_time DATETIME COMMENT '签到时间',
    check_out_time DATETIME COMMENT '退签时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_activity_participants (activity_id, user_id),
    FOREIGN KEY (activity_id) REFERENCES activities(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (approved_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建活动参与记录表索引
CREATE INDEX idx_activity_participants_status ON activity_participants(status);

-- 9. 活动评论表
CREATE TABLE IF NOT EXISTS activity_comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    activity_id BIGINT NOT NULL COMMENT '活动ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    content VARCHAR(2000) NOT NULL COMMENT '评论内容',
    parent_id BIGINT COMMENT '父评论ID(用于回复)',
    likes_count INT DEFAULT 0 COMMENT '点赞数',
    status INT NOT NULL DEFAULT 0 COMMENT '状态: 0-正常 1-已删除 2-已禁用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (activity_id) REFERENCES activities(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (parent_id) REFERENCES activity_comments(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建活动评论表索引
CREATE INDEX idx_activity_comments_parent_id ON activity_comments(parent_id);
CREATE INDEX idx_activity_comments_created_at ON activity_comments(created_at);

-- 10. 活动点赞表
CREATE TABLE IF NOT EXISTS activity_likes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    activity_id BIGINT NOT NULL COMMENT '活动ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
    UNIQUE KEY uk_activity_likes (activity_id, user_id),
    FOREIGN KEY (activity_id) REFERENCES activities(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 11. 评论点赞表
CREATE TABLE IF NOT EXISTS comment_likes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    comment_id BIGINT NOT NULL COMMENT '评论ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
    UNIQUE KEY uk_comment_likes (comment_id, user_id),
    FOREIGN KEY (comment_id) REFERENCES activity_comments(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建消息服务相关表

-- 12. 消息表
CREATE TABLE IF NOT EXISTS messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_id BIGINT NOT NULL COMMENT '发送者ID',
    receiver_id BIGINT NOT NULL COMMENT '接收者ID',
    message_type INT NOT NULL COMMENT '消息类型: 1-聊天消息 2-通知消息',
    content VARCHAR(2000) NOT NULL COMMENT '消息内容',
    status INT NOT NULL DEFAULT 0 COMMENT '状态: 0-未读 1-已读 2-已删除',
    related_id BIGINT COMMENT '关联ID',
    related_type VARCHAR(50) COMMENT '关联类型',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建消息表索引
CREATE INDEX idx_messages_sender_id ON messages(sender_id);
CREATE INDEX idx_messages_receiver_id ON messages(receiver_id);
CREATE INDEX idx_messages_status ON messages(status);
CREATE INDEX idx_messages_created_at ON messages(created_at);

-- 13. 通知表
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '接收者ID',
    notification_type INT NOT NULL COMMENT '通知类型: 1-系统通知 2-社团通知 3-活动通知 4-互动通知',
    title VARCHAR(200) NOT NULL COMMENT '通知标题',
    content VARCHAR(2000) NOT NULL COMMENT '通知内容',
    status INT NOT NULL DEFAULT 0 COMMENT '状态: 0-未读 1-已读',
    related_id BIGINT COMMENT '关联ID',
    related_type VARCHAR(50) COMMENT '关联类型',
    need_push BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否需要推送',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建通知表索引
CREATE INDEX idx_notifications_notification_type ON notifications(notification_type);
CREATE INDEX idx_notifications_status ON notifications(status);
CREATE INDEX idx_notifications_created_at ON notifications(created_at);

-- 14. 会话表
CREATE TABLE IF NOT EXISTS conversations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user1_id BIGINT NOT NULL COMMENT '用户1 ID',
    user2_id BIGINT NOT NULL COMMENT '用户2 ID',
    last_message_id BIGINT COMMENT '最后一条消息ID',
    unread_count INT DEFAULT 0 COMMENT '未读消息数',
    is_sticky BOOLEAN DEFAULT FALSE COMMENT '是否置顶',
    is_muted BOOLEAN DEFAULT FALSE COMMENT '是否免打扰',
    last_message_time DATETIME COMMENT '最后消息时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_conversations (user1_id, user2_id),
    FOREIGN KEY (user1_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (user2_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (last_message_id) REFERENCES messages(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建会话表索引
CREATE INDEX idx_conversations_last_message_time ON conversations(last_message_time);

-- 创建初始管理员用户（示例）
INSERT INTO users (username, real_name, email, phone, password_hash, role, status)
VALUES ('admin', '系统管理员', 'admin@campus.edu', '13800138000', 
        SHA2(CONCAT('admin123', 'campus_club_salt_2025'), 256), 'ADMIN', 'ACTIVE')
ON DUPLICATE KEY UPDATE 
    real_name = VALUES(real_name),
    email = VALUES(email),
    phone = VALUES(phone),
    password_hash = VALUES(password_hash),
    role = VALUES(role),
    status = VALUES(status);

-- 创建触发器示例：当活动状态更新时自动更新相关数据
DELIMITER //
CREATE TRIGGER trg_activity_status_update
AFTER UPDATE ON activities
FOR EACH ROW
BEGIN
    IF OLD.status != NEW.status THEN
        -- 可以在这里添加额外的业务逻辑处理
        INSERT INTO notifications (user_id, notification_type, title, content, related_id, related_type)
        SELECT cm.user_id, 3, 
               CONCAT('活动状态更新: ', NEW.title), 
               CONCAT('活动"', NEW.title, '"状态已更新为', 
                      CASE NEW.status
                          WHEN 1 THEN '已发布'
                          WHEN 2 THEN '进行中'
                          WHEN 3 THEN '已完成'
                          WHEN 4 THEN '已取消'
                          ELSE '未知状态'
                      END),
               NEW.id, 'activity'
        FROM club_members cm
        WHERE cm.club_id = NEW.club_id AND cm.status = 1;
    END IF;
END//
DELIMITER ;

-- 创建存储过程示例：获取用户参加的活动统计
DELIMITER //
CREATE PROCEDURE sp_get_user_activity_stats(IN user_id_param BIGINT)
BEGIN
    SELECT 
        COUNT(*) AS total_participations,
        SUM(CASE WHEN ap.status = 3 THEN 1 ELSE 0 END) AS completed_activities,
        SUM(CASE WHEN ap.status = 1 AND a.status = 2 THEN 1 ELSE 0 END) AS ongoing_activities,
        SUM(CASE WHEN ap.status = 1 AND a.status = 1 THEN 1 ELSE 0 END) AS upcoming_activities
    FROM activity_participants ap
    JOIN activities a ON ap.activity_id = a.id
    WHERE ap.user_id = user_id_param;
END//
DELIMITER ;

-- 创建视图示例：获取社团详情视图
CREATE OR REPLACE VIEW v_club_details AS
SELECT 
    c.*,
    u.username AS founder_name,
    u2.username AS advisor_name,
    COUNT(DISTINCT cm.id) AS actual_member_count,
    COUNT(DISTINCT a.id) AS actual_activity_count
FROM clubs c
LEFT JOIN users u ON c.founder_id = u.id
LEFT JOIN users u2 ON c.advisor_id = u2.id
LEFT JOIN club_members cm ON c.id = cm.club_id AND cm.status = 1
LEFT JOIN activities a ON c.id = a.club_id AND a.status IN (1, 2, 3)
GROUP BY c.id;

-- 数据库权限设置（示例）
-- CREATE USER 'campus_club_admin'@'localhost' IDENTIFIED BY 'CampusClub2025!';
-- GRANT ALL PRIVILEGES ON campus_club_system.* TO 'campus_club_admin'@'localhost';
-- FLUSH PRIVILEGES;

-- 完成创建，显示创建成功信息
SELECT '校园社团管理系统数据库创建完成！' AS message;