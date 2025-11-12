-- 插入测试用户
-- 密码都是 123456（经过SHA256加密 + salt: campus_club_salt_2025）

USE campus_club_system;

-- 清空现有数据（可选）
-- TRUNCATE TABLE users;

-- 插入测试用户
INSERT INTO users (username, real_name, email, phone, password, role, status, enabled, created_at, updated_at)
VALUES 
-- 管理员用户 (用户名: admin, 密码: 123456)
('admin', '系统管理员', 'admin@campus.edu', '13800000001', 
 'e49b5adc05e03f28feb6dfb5b8a317fc5149ee5c783720d4be4c09f17f69c93b', 
 'ADMIN', 'ACTIVE', 1, NOW(), NOW()),

-- 学生用户 (用户名: student1, 密码: 123456)
('student1', '张三', 'zhangsan@campus.edu', '13800010001', 
 'e49b5adc05e03f28feb6dfb5b8a317fc5149ee5c783720d4be4c09f17f69c93b', 
 'STUDENT', 'ACTIVE', 1, NOW(), NOW()),

-- 学生用户 (用户名: student2, 密码: 123456)
('student2', '李四', 'lisi@campus.edu', '13800010002', 
 'e49b5adc05e03f28feb6dfb5b8a317fc5149ee5c783720d4be4c09f17f69c93b', 
 'STUDENT', 'ACTIVE', 1, NOW(), NOW()),

-- 老师用户 (用户名: teacher1, 密码: 123456)
('teacher1', '刘老师', 'teacher1@campus.edu', '13900010001', 
 'e49b5adc05e03f28feb6dfb5b8a317fc5149ee5c783720d4be4c09f17f69c93b', 
 'TEACHER', 'ACTIVE', 1, NOW(), NOW()),

-- 社团管理员 (用户名: clubadmin1, 密码: 123456)
('clubadmin1', '赵社长', 'clubadmin1@campus.edu', '13700010001', 
 'e49b5adc05e03f28feb6dfb5b8a317fc5149ee5c783720d4be4c09f17f69c93b', 
 'CLUB_ADMIN', 'ACTIVE', 1, NOW(), NOW())

ON DUPLICATE KEY UPDATE 
    real_name = VALUES(real_name),
    email = VALUES(email),
    phone = VALUES(phone),
    password = VALUES(password),
    role = VALUES(role),
    status = VALUES(status),
    enabled = VALUES(enabled);

-- 插入用户档案信息
INSERT INTO user_profiles (user_id, student_id, major, grade, class_name, gender, hobbies, bio, created_at, updated_at)
VALUES 
((SELECT id FROM users WHERE username = 'student1' LIMIT 1), '20220001', '计算机科学与技术', '2022级', '计科1班', '男', '编程,篮球,摄影', '热爱技术，喜欢挑战', NOW(), NOW()),
((SELECT id FROM users WHERE username = 'student2' LIMIT 1), '20220002', '软件工程', '2022级', '软件2班', '女', '画画,阅读,编程', '喜欢编程和艺术的结合', NOW(), NOW()),
((SELECT id FROM users WHERE username = 'teacher1' LIMIT 1), null, null, null, null, '男', '教学,科研', '计算机系教授', NOW(), NOW()),
((SELECT id FROM users WHERE username = 'clubadmin1' LIMIT 1), '20210001', '电子信息工程', '2021级', '电信1班', '男', '机器人,电子制作', '科技创新社社长', NOW(), NOW())
ON DUPLICATE KEY UPDATE 
    student_id = VALUES(student_id),
    major = VALUES(major),
    grade = VALUES(grade),
    class_name = VALUES(class_name),
    gender = VALUES(gender),
    hobbies = VALUES(hobbies),
    bio = VALUES(bio);

SELECT '测试用户创建成功！' AS message;
SELECT 'admin / student1 / student2 / teacher1 / clubadmin1' AS 可用用户名;
SELECT '密码都是: 123456' AS 默认密码;

