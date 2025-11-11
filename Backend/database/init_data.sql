-- 校园社团管理系统数据初始化脚本
-- 用于插入基础测试数据

USE campus_club_system;

-- 1. 插入更多测试用户
INSERT INTO users (username, real_name, email, phone, password_hash, role, status)
VALUES 
-- 学生用户
('student1', '张三', 'zhangsan@campus.edu', '13800010001', 
 SHA2(CONCAT('student123', 'campus_club_salt_2025'), 256), 'STUDENT', 'ACTIVE'),
('student2', '李四', 'lisi@campus.edu', '13800010002', 
 SHA2(CONCAT('student123', 'campus_club_salt_2025'), 256), 'STUDENT', 'ACTIVE'),
('student3', '王五', 'wangwu@campus.edu', '13800010003', 
 SHA2(CONCAT('student123', 'campus_club_salt_2025'), 256), 'STUDENT', 'ACTIVE'),
-- 老师用户
('teacher1', '刘老师', 'teacher1@campus.edu', '13900010001', 
 SHA2(CONCAT('teacher123', 'campus_club_salt_2025'), 256), 'TEACHER', 'ACTIVE'),
('teacher2', '陈老师', 'teacher2@campus.edu', '13900010002', 
 SHA2(CONCAT('teacher123', 'campus_club_salt_2025'), 256), 'TEACHER', 'ACTIVE'),
-- 社团管理员用户
('clubadmin1', '赵社长', 'clubadmin1@campus.edu', '13700010001', 
 SHA2(CONCAT('clubadmin123', 'campus_club_salt_2025'), 256), 'CLUB_ADMIN', 'ACTIVE'),
('clubadmin2', '钱副社长', 'clubadmin2@campus.edu', '13700010002', 
 SHA2(CONCAT('clubadmin123', 'campus_club_salt_2025'), 256), 'CLUB_ADMIN', 'ACTIVE')
ON DUPLICATE KEY UPDATE 
    real_name = VALUES(real_name),
    email = VALUES(email),
    phone = VALUES(phone),
    password_hash = VALUES(password_hash),
    role = VALUES(role),
    status = VALUES(status);

-- 2. 插入用户档案信息
INSERT INTO user_profiles (user_id, student_id, major, grade, class_name, gender, hobbies, bio)
VALUES 
((SELECT id FROM users WHERE username = 'student1' LIMIT 1), '20220001', '计算机科学与技术', '2022级', '计科1班', '男', '编程,篮球,摄影', '热爱技术，喜欢挑战'),
((SELECT id FROM users WHERE username = 'student2' LIMIT 1), '20220002', '软件工程', '2022级', '软件2班', '女', '画画,阅读,编程', '喜欢编程和艺术的结合'),
((SELECT id FROM users WHERE username = 'student3' LIMIT 1), '20220003', '人工智能', '2022级', 'AI1班', '男', '机器学习,数学建模', 'AI爱好者'),
((SELECT id FROM users WHERE username = 'teacher1' LIMIT 1), null, null, null, null, '男', '教学,科研', '计算机系教授'),
((SELECT id FROM users WHERE username = 'teacher2' LIMIT 1), null, null, null, null, '女', '教育管理,心理学', '学生处老师'),
((SELECT id FROM users WHERE username = 'clubadmin1' LIMIT 1), '20210001', '电子信息工程', '2021级', '电信1班', '男', '机器人,电子制作', '科技创新社社长'),
((SELECT id FROM users WHERE username = 'clubadmin2' LIMIT 1), '20210002', '机械工程', '2021级', '机械2班', '男', '机械设计,3D打印', '科技创新社副社长')
ON DUPLICATE KEY UPDATE 
    student_id = VALUES(student_id),
    major = VALUES(major),
    grade = VALUES(grade),
    class_name = VALUES(class_name),
    gender = VALUES(gender),
    hobbies = VALUES(hobbies),
    bio = VALUES(bio);

-- 3. 插入社团类别（可以作为字典表使用）
INSERT INTO clubs (name, description, content, category, founder_id, advisor_id, status, member_count, establish_date, logo_url)
VALUES 
('科技创新社', '致力于推动校园科技创新活动，组织技术分享、项目实践等活动。', 
 '科技创新社是我校最大的科技类社团之一，致力于培养学生的创新能力和实践能力...', 
 '科技', (SELECT id FROM users WHERE username = 'clubadmin1' LIMIT 1), (SELECT id FROM users WHERE username = 'teacher1' LIMIT 1), 1, 0, '2020-09-01', 'https://example.com/logos/tech-club.png'),
('文学社', '文学爱好者的聚集地，举办读书会、写作比赛等活动。', 
 '文学社成立于2018年，是校园文化建设的重要力量，定期举办各类文学活动...', 
 '文化', (SELECT id FROM users WHERE username = 'student2' LIMIT 1), (SELECT id FROM users WHERE username = 'teacher2' LIMIT 1), 1, 0, '2018-10-15', 'https://example.com/logos/literature-club.png'),
('篮球队', '校园篮球爱好者组织，代表学校参加各类比赛。', 
 '篮球队是我校传统优势社团，多次在市级比赛中获奖...', 
 '体育', (SELECT id FROM users WHERE username = 'student1' LIMIT 1), (SELECT id FROM users WHERE username = 'teacher1' LIMIT 1), 1, 0, '2019-03-20', 'https://example.com/logos/basketball-club.png'),
('摄影协会', '记录校园美好瞬间，分享摄影技巧和作品。', 
 '摄影协会致力于培养学生的审美能力和摄影技术...', 
 '艺术', (SELECT id FROM users WHERE username = 'student3' LIMIT 1), (SELECT id FROM users WHERE username = 'teacher2' LIMIT 1), 1, 0, '2021-05-10', 'https://example.com/logos/photography-club.png')
ON DUPLICATE KEY UPDATE 
    description = VALUES(description),
    content = VALUES(content),
    category = VALUES(category),
    founder_id = VALUES(founder_id),
    advisor_id = VALUES(advisor_id),
    status = VALUES(status),
    establish_date = VALUES(establish_date),
    logo_url = VALUES(logo_url);

-- 4. 插入社团成员
INSERT INTO club_members (club_id, user_id, role, join_time, status)
VALUES 
-- 科技创新社成员
((SELECT id FROM clubs WHERE name = '科技创新社' LIMIT 1), (SELECT id FROM users WHERE username = 'clubadmin1' LIMIT 1), 'PRESIDENT', '2020-09-01', 1),
((SELECT id FROM clubs WHERE name = '科技创新社' LIMIT 1), (SELECT id FROM users WHERE username = 'clubadmin2' LIMIT 1), 'VICE_PRESIDENT', '2020-09-05', 1),
((SELECT id FROM clubs WHERE name = '科技创新社' LIMIT 1), (SELECT id FROM users WHERE username = 'student1' LIMIT 1), 'MANAGER', '2022-10-01', 1),
((SELECT id FROM clubs WHERE name = '科技创新社' LIMIT 1), (SELECT id FROM users WHERE username = 'student3' LIMIT 1), 'MEMBER', '2022-11-15', 1),
-- 文学社成员
((SELECT id FROM clubs WHERE name = '文学社' LIMIT 1), (SELECT id FROM users WHERE username = 'student2' LIMIT 1), 'PRESIDENT', '2018-10-15', 1),
((SELECT id FROM clubs WHERE name = '文学社' LIMIT 1), (SELECT id FROM users WHERE username = 'student3' LIMIT 1), 'MEMBER', '2022-09-01', 1),
-- 篮球队成员
((SELECT id FROM clubs WHERE name = '篮球队' LIMIT 1), (SELECT id FROM users WHERE username = 'student1' LIMIT 1), 'PRESIDENT', '2019-03-20', 1),
((SELECT id FROM clubs WHERE name = '篮球队' LIMIT 1), (SELECT id FROM users WHERE username = 'clubadmin1' LIMIT 1), 'MEMBER', '2021-04-10', 1),
-- 摄影协会成员
((SELECT id FROM clubs WHERE name = '摄影协会' LIMIT 1), (SELECT id FROM users WHERE username = 'student3' LIMIT 1), 'PRESIDENT', '2021-05-10', 1),
((SELECT id FROM clubs WHERE name = '摄影协会' LIMIT 1), (SELECT id FROM users WHERE username = 'student2' LIMIT 1), 'MEMBER', '2021-06-01', 1)
ON DUPLICATE KEY UPDATE 
    role = VALUES(role),
    status = VALUES(status);

-- 更新社团成员数量
UPDATE clubs c
SET member_count = (
    SELECT COUNT(*) FROM club_members cm 
    WHERE cm.club_id = c.id AND cm.status = 1
);

-- 5. 插入活动数据
INSERT INTO activities (title, description, content, cover_image, start_time, end_time, location, organizer_id, club_id, status, max_participants, current_participants, need_approval)
VALUES 
('编程马拉松比赛', '为期两天的编程挑战赛，组队完成项目开发。', 
 '<h3>活动详情</h3><p>欢迎参加校园编程马拉松比赛...</p>', 
 'https://example.com/images/programming-marathon.jpg', 
 '2023-12-15 09:00:00', '2023-12-17 18:00:00', '科技楼101会议室', (SELECT id FROM users WHERE username = 'clubadmin1' LIMIT 1), (SELECT id FROM clubs WHERE name = '科技创新社' LIMIT 1), 1, 50, 0, true),
('诗歌朗诵会', '分享经典诗歌，展示朗诵才艺。', 
 '<h3>活动详情</h3><p>热爱诗歌的同学们不要错过...</p>', 
 'https://example.com/images/poetry-recital.jpg', 
 '2023-12-20 14:00:00', '2023-12-20 17:00:00', '图书馆报告厅', (SELECT id FROM users WHERE username = 'student2' LIMIT 1), (SELECT id FROM clubs WHERE name = '文学社' LIMIT 1), 1, 100, 0, false),
('篮球友谊赛', '与其他高校篮球队的友谊交流赛。', 
 '<h3>活动详情</h3><p>欢迎同学们前来观赛...</p>', 
 'https://example.com/images/basketball-friendship.jpg', 
 '2023-12-23 15:00:00', '2023-12-23 17:30:00', '体育馆篮球场', (SELECT id FROM users WHERE username = 'student1' LIMIT 1), (SELECT id FROM clubs WHERE name = '篮球队' LIMIT 1), 1, 20, 0, true),
('摄影技巧讲座', '专业摄影师分享摄影技巧和经验。', 
 '<h3>活动详情</h3><p>从入门到精通的摄影技巧...</p>', 
 'https://example.com/images/photography-lecture.jpg', 
 '2023-12-25 19:00:00', '2023-12-25 21:00:00', '教学楼A301', (SELECT id FROM users WHERE username = 'student3' LIMIT 1), (SELECT id FROM clubs WHERE name = '摄影协会' LIMIT 1), 1, 150, 0, false),
('人工智能应用讲座', '探讨AI技术在各个领域的应用案例。', 
 '<h3>活动详情</h3><p>AI技术的最新发展和应用前景...</p>', 
 'https://example.com/images/ai-lecture.jpg', 
 '2023-12-30 10:00:00', '2023-12-30 12:00:00', '科技楼205', (SELECT id FROM users WHERE username = 'clubadmin1' LIMIT 1), (SELECT id FROM clubs WHERE name = '科技创新社' LIMIT 1), 1, 200, 0, false)
ON DUPLICATE KEY UPDATE 
    description = VALUES(description),
    content = VALUES(content),
    cover_image = VALUES(cover_image),
    start_time = VALUES(start_time),
    end_time = VALUES(end_time),
    location = VALUES(location),
    organizer_id = VALUES(organizer_id),
    status = VALUES(status),
    max_participants = VALUES(max_participants);

-- 更新社团活动数量
UPDATE clubs c
SET activity_count = (
    SELECT COUNT(*) FROM activities a 
    WHERE a.club_id = c.id AND a.status IN (1, 2, 3)
);

-- 6. 插入活动报名记录
INSERT INTO activity_participants (activity_id, user_id, status, enrollment_time, check_in_time)
VALUES 
-- 编程马拉松报名
((SELECT id FROM activities WHERE title = '编程马拉松比赛' LIMIT 1), (SELECT id FROM users WHERE username = 'student1' LIMIT 1), 3, '2023-12-01 10:00:00', '2023-12-15 08:50:00'),
((SELECT id FROM activities WHERE title = '编程马拉松比赛' LIMIT 1), (SELECT id FROM users WHERE username = 'student3' LIMIT 1), 3, '2023-12-02 14:30:00', '2023-12-15 08:55:00'),
((SELECT id FROM activities WHERE title = '编程马拉松比赛' LIMIT 1), (SELECT id FROM users WHERE username = 'clubadmin1' LIMIT 1), 3, '2023-12-01 09:00:00', '2023-12-15 08:45:00'),
((SELECT id FROM activities WHERE title = '编程马拉松比赛' LIMIT 1), (SELECT id FROM users WHERE username = 'student2' LIMIT 1), 1, '2023-12-03 16:20:00', null),
-- 诗歌朗诵会报名
((SELECT id FROM activities WHERE title = '诗歌朗诵会' LIMIT 1), (SELECT id FROM users WHERE username = 'student2' LIMIT 1), 3, '2023-12-10 11:00:00', '2023-12-20 13:50:00'),
((SELECT id FROM activities WHERE title = '诗歌朗诵会' LIMIT 1), (SELECT id FROM users WHERE username = 'student3' LIMIT 1), 3, '2023-12-11 09:30:00', '2023-12-20 13:45:00'),
-- 篮球友谊赛报名
((SELECT id FROM activities WHERE title = '篮球友谊赛' LIMIT 1), (SELECT id FROM users WHERE username = 'student1' LIMIT 1), 3, '2023-12-15 10:00:00', '2023-12-23 14:40:00'),
((SELECT id FROM activities WHERE title = '篮球友谊赛' LIMIT 1), (SELECT id FROM users WHERE username = 'clubadmin1' LIMIT 1), 3, '2023-12-15 09:30:00', '2023-12-23 14:35:00'),
((SELECT id FROM activities WHERE title = '篮球友谊赛' LIMIT 1), (SELECT id FROM users WHERE username = 'clubadmin2' LIMIT 1), 1, '2023-12-16 15:20:00', null),
-- 摄影讲座报名
((SELECT id FROM activities WHERE title = '摄影技巧讲座' LIMIT 1), (SELECT id FROM users WHERE username = 'student3' LIMIT 1), 3, '2023-12-20 10:00:00', '2023-12-25 18:50:00'),
((SELECT id FROM activities WHERE title = '摄影技巧讲座' LIMIT 1), (SELECT id FROM users WHERE username = 'student2' LIMIT 1), 3, '2023-12-20 11:30:00', '2023-12-25 18:45:00'),
((SELECT id FROM activities WHERE title = '摄影技巧讲座' LIMIT 1), (SELECT id FROM users WHERE username = 'student1' LIMIT 1), 3, '2023-12-21 14:20:00', '2023-12-25 18:55:00')
ON DUPLICATE KEY UPDATE 
    status = VALUES(status),
    check_in_time = VALUES(check_in_time);

-- 更新活动当前参与人数
UPDATE activities a
SET current_participants = (
    SELECT COUNT(*) FROM activity_participants ap 
    WHERE ap.activity_id = a.id AND ap.status IN (1, 3, 4)
);

-- 7. 插入活动评论
-- 先插入所有没有父评论的评论
INSERT INTO activity_comments (activity_id, user_id, content, parent_id, likes_count)
VALUES 
-- 编程马拉松评论
((SELECT id FROM activities WHERE title = '编程马拉松比赛' LIMIT 1), (SELECT id FROM users WHERE username = 'student1' LIMIT 1), '活动很精彩，学到了很多东西！', null, 3),
((SELECT id FROM activities WHERE title = '编程马拉松比赛' LIMIT 1), (SELECT id FROM users WHERE username = 'student3' LIMIT 1), '感谢组织，期待下一次活动！', null, 2),
((SELECT id FROM activities WHERE title = '编程马拉松比赛' LIMIT 1), (SELECT id FROM users WHERE username = 'student2' LIMIT 1), '请问需要自带电脑吗？', null, 0),
-- 诗歌朗诵会评论
((SELECT id FROM activities WHERE title = '诗歌朗诵会' LIMIT 1), (SELECT id FROM users WHERE username = 'student2' LIMIT 1), '今天的朗诵很感人，谢谢分享！', null, 4),
((SELECT id FROM activities WHERE title = '诗歌朗诵会' LIMIT 1), (SELECT id FROM users WHERE username = 'student3' LIMIT 1), '希望能多举办这样的文化活动。', null, 3),
-- 篮球友谊赛评论
((SELECT id FROM activities WHERE title = '篮球友谊赛' LIMIT 1), (SELECT id FROM users WHERE username = 'clubadmin1' LIMIT 1), '我们的球队表现不错！', null, 5),
((SELECT id FROM activities WHERE title = '篮球友谊赛' LIMIT 1), (SELECT id FROM users WHERE username = 'student1' LIMIT 1), '下次要加强配合，争取更好的成绩。', null, 2),
-- 摄影讲座评论（没有父评论的）
((SELECT id FROM activities WHERE title = '摄影技巧讲座' LIMIT 1), (SELECT id FROM users WHERE username = 'student3' LIMIT 1), '讲座内容很实用，收获满满！', null, 6),
((SELECT id FROM activities WHERE title = '摄影技巧讲座' LIMIT 1), (SELECT id FROM users WHERE username = 'student2' LIMIT 1), '请问有讲座的PPT分享吗？', null, 1)
ON DUPLICATE KEY UPDATE 
    content = VALUES(content),
    likes_count = VALUES(likes_count);

-- 再插入有父评论的回复，使用临时表避免直接引用正在更新的表

-- 插入编程马拉松的回复
INSERT INTO activity_comments (activity_id, user_id, content, parent_id, likes_count)
SELECT 
    (SELECT id FROM activities WHERE title = '编程马拉松比赛' LIMIT 1),
    (SELECT id FROM users WHERE username = 'student1' LIMIT 1),
    '是的，需要自带笔记本电脑。',
    temp_comment.id,
    1
FROM (SELECT id FROM activity_comments WHERE content = '请问需要自带电脑吗？' AND user_id = (SELECT id FROM users WHERE username = 'student2' LIMIT 1) LIMIT 1) AS temp_comment;

-- 插入摄影讲座的回复
INSERT INTO activity_comments (activity_id, user_id, content, parent_id, likes_count)
SELECT 
    (SELECT id FROM activities WHERE title = '摄影技巧讲座' LIMIT 1),
    (SELECT id FROM users WHERE username = 'student3' LIMIT 1),
    '稍后会分享到群里，请关注通知。',
    temp_comment.id,
    2
FROM (SELECT id FROM activity_comments WHERE content = '请问有讲座的PPT分享吗？' AND user_id = (SELECT id FROM users WHERE username = 'student2' LIMIT 1) LIMIT 1) AS temp_comment;

-- 8. 插入活动点赞
INSERT INTO activity_likes (activity_id, user_id)
VALUES 
((SELECT id FROM activities WHERE title = '编程马拉松比赛' LIMIT 1), (SELECT id FROM users WHERE username = 'student1' LIMIT 1)),
((SELECT id FROM activities WHERE title = '编程马拉松比赛' LIMIT 1), (SELECT id FROM users WHERE username = 'student2' LIMIT 1)),
((SELECT id FROM activities WHERE title = '编程马拉松比赛' LIMIT 1), (SELECT id FROM users WHERE username = 'student3' LIMIT 1)),
((SELECT id FROM activities WHERE title = '诗歌朗诵会' LIMIT 1), (SELECT id FROM users WHERE username = 'student2' LIMIT 1)),
((SELECT id FROM activities WHERE title = '诗歌朗诵会' LIMIT 1), (SELECT id FROM users WHERE username = 'student3' LIMIT 1)),
((SELECT id FROM activities WHERE title = '诗歌朗诵会' LIMIT 1), (SELECT id FROM users WHERE username = 'clubadmin1' LIMIT 1)),
((SELECT id FROM activities WHERE title = '篮球友谊赛' LIMIT 1), (SELECT id FROM users WHERE username = 'student1' LIMIT 1)),
((SELECT id FROM activities WHERE title = '篮球友谊赛' LIMIT 1), (SELECT id FROM users WHERE username = 'clubadmin1' LIMIT 1)),
((SELECT id FROM activities WHERE title = '篮球友谊赛' LIMIT 1), (SELECT id FROM users WHERE username = 'clubadmin2' LIMIT 1)),
((SELECT id FROM activities WHERE title = '摄影技巧讲座' LIMIT 1), (SELECT id FROM users WHERE username = 'student3' LIMIT 1)),
((SELECT id FROM activities WHERE title = '摄影技巧讲座' LIMIT 1), (SELECT id FROM users WHERE username = 'student2' LIMIT 1)),
((SELECT id FROM activities WHERE title = '摄影技巧讲座' LIMIT 1), (SELECT id FROM users WHERE username = 'student1' LIMIT 1)),
((SELECT id FROM activities WHERE title = '摄影技巧讲座' LIMIT 1), (SELECT id FROM users WHERE username = 'clubadmin2' LIMIT 1)),
((SELECT id FROM activities WHERE title = '人工智能应用讲座' LIMIT 1), (SELECT id FROM users WHERE username = 'student1' LIMIT 1)),
((SELECT id FROM activities WHERE title = '人工智能应用讲座' LIMIT 1), (SELECT id FROM users WHERE username = 'student3' LIMIT 1)),
((SELECT id FROM activities WHERE title = '人工智能应用讲座' LIMIT 1), (SELECT id FROM users WHERE username = 'clubadmin1' LIMIT 1))
ON DUPLICATE KEY UPDATE activity_id = activity_id;

-- 更新活动点赞数
UPDATE activities a
SET like_count = (
    SELECT COUNT(*) FROM activity_likes al 
    WHERE al.activity_id = a.id
);

-- 9. 插入评论点赞
-- 使用JOIN方式插入评论点赞，避免直接引用可能为空的SELECT结果
INSERT INTO comment_likes (comment_id, user_id)
SELECT 
    c.id,
    (SELECT id FROM users WHERE username = 'student2' LIMIT 1)
FROM activity_comments c
WHERE c.content = '活动很精彩，学到了很多东西！' 
AND c.user_id = (SELECT id FROM users WHERE username = 'student1' LIMIT 1)
AND c.activity_id = (SELECT id FROM activities WHERE title = '编程马拉松比赛' LIMIT 1)
LIMIT 1;

INSERT INTO comment_likes (comment_id, user_id)
SELECT 
    c.id,
    (SELECT id FROM users WHERE username = 'student3' LIMIT 1)
FROM activity_comments c
WHERE c.content = '活动很精彩，学到了很多东西！' 
AND c.user_id = (SELECT id FROM users WHERE username = 'student1' LIMIT 1)
AND c.activity_id = (SELECT id FROM activities WHERE title = '编程马拉松比赛' LIMIT 1)
LIMIT 1;

INSERT INTO comment_likes (comment_id, user_id)
SELECT 
    c.id,
    (SELECT id FROM users WHERE username = 'clubadmin1' LIMIT 1)
FROM activity_comments c
WHERE c.content = '活动很精彩，学到了很多东西！' 
AND c.user_id = (SELECT id FROM users WHERE username = 'student1' LIMIT 1)
AND c.activity_id = (SELECT id FROM activities WHERE title = '编程马拉松比赛' LIMIT 1)
LIMIT 1;

INSERT INTO comment_likes (comment_id, user_id)
SELECT 
    c.id,
    (SELECT id FROM users WHERE username = 'student1' LIMIT 1)
FROM activity_comments c
WHERE c.content = '感谢组织，期待下一次活动！' 
AND c.user_id = (SELECT id FROM users WHERE username = 'student3' LIMIT 1)
AND c.activity_id = (SELECT id FROM activities WHERE title = '编程马拉松比赛' LIMIT 1)
LIMIT 1;

INSERT INTO comment_likes (comment_id, user_id)
SELECT 
    c.id,
    (SELECT id FROM users WHERE username = 'student2' LIMIT 1)
FROM activity_comments c
WHERE c.content = '感谢组织，期待下一次活动！' 
AND c.user_id = (SELECT id FROM users WHERE username = 'student3' LIMIT 1)
AND c.activity_id = (SELECT id FROM activities WHERE title = '编程马拉松比赛' LIMIT 1)
LIMIT 1;

INSERT INTO comment_likes (comment_id, user_id)
SELECT 
    c.id,
    (SELECT id FROM users WHERE username = 'student2' LIMIT 1)
FROM activity_comments c
WHERE c.content = '是的，需要自带笔记本电脑。' 
AND c.user_id = (SELECT id FROM users WHERE username = 'student1' LIMIT 1)
AND c.activity_id = (SELECT id FROM activities WHERE title = '编程马拉松比赛' LIMIT 1)
LIMIT 1;
-- 诗歌朗诵会评论点赞
INSERT INTO comment_likes (comment_id, user_id)
SELECT 
    c.id,
    (SELECT id FROM users WHERE username = 'student1' LIMIT 1)
FROM activity_comments c
WHERE c.content = '今天的朗诵很感人，谢谢分享！' 
AND c.user_id = (SELECT id FROM users WHERE username = 'student2' LIMIT 1)
AND c.activity_id = (SELECT id FROM activities WHERE title = '诗歌朗诵会' LIMIT 1)
LIMIT 1;

INSERT INTO comment_likes (comment_id, user_id)
SELECT 
    c.id,
    (SELECT id FROM users WHERE username = 'student3' LIMIT 1)
FROM activity_comments c
WHERE c.content = '今天的朗诵很感人，谢谢分享！' 
AND c.user_id = (SELECT id FROM users WHERE username = 'student2' LIMIT 1)
AND c.activity_id = (SELECT id FROM activities WHERE title = '诗歌朗诵会' LIMIT 1)
LIMIT 1;

INSERT INTO comment_likes (comment_id, user_id)
SELECT 
    c.id,
    (SELECT id FROM users WHERE username = 'clubadmin2' LIMIT 1)
FROM activity_comments c
WHERE c.content = '今天的朗诵很感人，谢谢分享！' 
AND c.user_id = (SELECT id FROM users WHERE username = 'student2' LIMIT 1)
AND c.activity_id = (SELECT id FROM activities WHERE title = '诗歌朗诵会' LIMIT 1)
LIMIT 1;

INSERT INTO comment_likes (comment_id, user_id)
SELECT 
    c.id,
    (SELECT id FROM users WHERE username = 'clubadmin1' LIMIT 1)
FROM activity_comments c
WHERE c.content = '今天的朗诵很感人，谢谢分享！' 
AND c.user_id = (SELECT id FROM users WHERE username = 'student2' LIMIT 1)
AND c.activity_id = (SELECT id FROM activities WHERE title = '诗歌朗诵会' LIMIT 1)
LIMIT 1;

INSERT INTO comment_likes (comment_id, user_id)
SELECT 
    c.id,
    (SELECT id FROM users WHERE username = 'student1' LIMIT 1)
FROM activity_comments c
WHERE c.content = '希望能多举办这样的文化活动。' 
AND c.user_id = (SELECT id FROM users WHERE username = 'student3' LIMIT 1)
AND c.activity_id = (SELECT id FROM activities WHERE title = '诗歌朗诵会' LIMIT 1)
LIMIT 1;

INSERT INTO comment_likes (comment_id, user_id)
SELECT 
    c.id,
    (SELECT id FROM users WHERE username = 'student2' LIMIT 1)
FROM activity_comments c
WHERE c.content = '希望能多举办这样的文化活动。' 
AND c.user_id = (SELECT id FROM users WHERE username = 'student3' LIMIT 1)
AND c.activity_id = (SELECT id FROM activities WHERE title = '诗歌朗诵会' LIMIT 1)
LIMIT 1;

INSERT INTO comment_likes (comment_id, user_id)
SELECT 
    c.id,
    (SELECT id FROM users WHERE username = 'clubadmin2' LIMIT 1)
FROM activity_comments c
WHERE c.content = '希望能多举办这样的文化活动。' 
AND c.user_id = (SELECT id FROM users WHERE username = 'student3' LIMIT 1)
AND c.activity_id = (SELECT id FROM activities WHERE title = '诗歌朗诵会' LIMIT 1)
LIMIT 1;
-- 篮球友谊赛评论点赞
INSERT INTO comment_likes (comment_id, user_id)
SELECT 
    c.id,
    (SELECT id FROM users WHERE username = 'student1' LIMIT 1)
FROM activity_comments c
WHERE c.content = '我们的球队表现不错！' 
AND c.user_id = (SELECT id FROM users WHERE username = 'clubadmin1' LIMIT 1)
AND c.activity_id = (SELECT id FROM activities WHERE title = '篮球友谊赛' LIMIT 1)
LIMIT 1;

INSERT INTO comment_likes (comment_id, user_id)
SELECT 
    c.id,
    (SELECT id FROM users WHERE username = 'student2' LIMIT 1)
FROM activity_comments c
WHERE c.content = '我们的球队表现不错！' 
AND c.user_id = (SELECT id FROM users WHERE username = 'clubadmin1' LIMIT 1)
AND c.activity_id = (SELECT id FROM activities WHERE title = '篮球友谊赛' LIMIT 1)
LIMIT 1;

INSERT INTO comment_likes (comment_id, user_id)
SELECT 
    c.id,
    (SELECT id FROM users WHERE username = 'student3' LIMIT 1)
FROM activity_comments c
WHERE c.content = '我们的球队表现不错！' 
AND c.user_id = (SELECT id FROM users WHERE username = 'clubadmin1' LIMIT 1)
AND c.activity_id = (SELECT id FROM activities WHERE title = '篮球友谊赛' LIMIT 1)
LIMIT 1;

INSERT INTO comment_likes (comment_id, user_id)
SELECT 
    c.id,
    (SELECT id FROM users WHERE username = 'clubadmin1' LIMIT 1)
FROM activity_comments c
WHERE c.content = '我们的球队表现不错！' 
AND c.user_id = (SELECT id FROM users WHERE username = 'clubadmin1' LIMIT 1)
AND c.activity_id = (SELECT id FROM activities WHERE title = '篮球友谊赛' LIMIT 1)
LIMIT 1;

-- 不使用admin用户，因为可能不存在
INSERT INTO comment_likes (comment_id, user_id)
SELECT 
    c.id,
    (SELECT id FROM users WHERE username = 'clubadmin2' LIMIT 1)
FROM activity_comments c
WHERE c.content = '下次要加强配合，争取更好的成绩。' 
AND c.user_id = (SELECT id FROM users WHERE username = 'student1' LIMIT 1)
AND c.activity_id = (SELECT id FROM activities WHERE title = '篮球友谊赛' LIMIT 1)
LIMIT 1;

INSERT INTO comment_likes (comment_id, user_id)
SELECT 
    c.id,
    (SELECT id FROM users WHERE username = 'clubadmin1' LIMIT 1)
FROM activity_comments c
WHERE c.content = '下次要加强配合，争取更好的成绩。' 
AND c.user_id = (SELECT id FROM users WHERE username = 'student1' LIMIT 1)
AND c.activity_id = (SELECT id FROM activities WHERE title = '篮球友谊赛' LIMIT 1)
LIMIT 1;
-- 摄影讲座评论点赞
INSERT INTO comment_likes (comment_id, user_id)
SELECT 
    c.id,
    (SELECT id FROM users WHERE username = 'student1' LIMIT 1)
FROM activity_comments c
WHERE c.content = '讲座内容很实用，收获满满！' 
AND c.user_id = (SELECT id FROM users WHERE username = 'student3' LIMIT 1)
AND c.activity_id = (SELECT id FROM activities WHERE title = '摄影技巧讲座' LIMIT 1)
LIMIT 1;

INSERT INTO comment_likes (comment_id, user_id)
SELECT 
    c.id,
    (SELECT id FROM users WHERE username = 'student2' LIMIT 1)
FROM activity_comments c
WHERE c.content = '讲座内容很实用，收获满满！' 
AND c.user_id = (SELECT id FROM users WHERE username = 'student3' LIMIT 1)
AND c.activity_id = (SELECT id FROM activities WHERE title = '摄影技巧讲座' LIMIT 1)
LIMIT 1;

INSERT INTO comment_likes (comment_id, user_id)
SELECT 
    c.id,
    (SELECT id FROM users WHERE username = 'clubadmin2' LIMIT 1)
FROM activity_comments c
WHERE c.content = '讲座内容很实用，收获满满！' 
AND c.user_id = (SELECT id FROM users WHERE username = 'student3' LIMIT 1)
AND c.activity_id = (SELECT id FROM activities WHERE title = '摄影技巧讲座' LIMIT 1)
LIMIT 1;

INSERT INTO comment_likes (comment_id, user_id)
SELECT 
    c.id,
    (SELECT id FROM users WHERE username = 'clubadmin1' LIMIT 1)
FROM activity_comments c
WHERE c.content = '讲座内容很实用，收获满满！' 
AND c.user_id = (SELECT id FROM users WHERE username = 'student3' LIMIT 1)
AND c.activity_id = (SELECT id FROM activities WHERE title = '摄影技巧讲座' LIMIT 1)
LIMIT 1;

-- 不使用admin用户
INSERT INTO comment_likes (comment_id, user_id)
SELECT 
    c.id,
    (SELECT id FROM users WHERE username = 'student3' LIMIT 1)
FROM activity_comments c
WHERE c.content = '讲座内容很实用，收获满满！' 
AND c.user_id = (SELECT id FROM users WHERE username = 'student3' LIMIT 1)
AND c.activity_id = (SELECT id FROM activities WHERE title = '摄影技巧讲座' LIMIT 1)
LIMIT 1;

INSERT INTO comment_likes (comment_id, user_id)
SELECT 
    c.id,
    (SELECT id FROM users WHERE username = 'student1' LIMIT 1)
FROM activity_comments c
WHERE c.content = '请问有讲座的PPT分享吗？' 
AND c.user_id = (SELECT id FROM users WHERE username = 'student2' LIMIT 1)
AND c.activity_id = (SELECT id FROM activities WHERE title = '摄影技巧讲座' LIMIT 1)
LIMIT 1;

INSERT INTO comment_likes (comment_id, user_id)
SELECT 
    c.id,
    (SELECT id FROM users WHERE username = 'student2' LIMIT 1)
FROM activity_comments c
WHERE c.content = '稍后会分享到群里，请关注通知。' 
AND c.user_id = (SELECT id FROM users WHERE username = 'student3' LIMIT 1)
AND c.activity_id = (SELECT id FROM activities WHERE title = '摄影技巧讲座' LIMIT 1)
LIMIT 1;

INSERT INTO comment_likes (comment_id, user_id)
SELECT 
    c.id,
    (SELECT id FROM users WHERE username = 'clubadmin1' LIMIT 1)
FROM activity_comments c
WHERE c.content = '稍后会分享到群里，请关注通知。'
AND c.user_id = (SELECT id FROM users WHERE username = 'student3' LIMIT 1)
AND c.activity_id = (SELECT id FROM activities WHERE title = '摄影技巧讲座' LIMIT 1)
LIMIT 1;


-- 更新评论点赞数
UPDATE activity_comments ac
SET likes_count = (
    SELECT COUNT(*) FROM comment_likes cl 
    WHERE cl.comment_id = ac.id
);

-- 10. 插入系统通知
INSERT INTO notifications (user_id, notification_type, title, content, related_id, related_type)
VALUES 
((SELECT id FROM users WHERE username = 'student1' LIMIT 1), 1, '系统公告：期末考试安排', '期末考试将于下周开始，请同学们做好准备。', null, null),
((SELECT id FROM users WHERE username = 'student2' LIMIT 1), 1, '系统公告：期末考试安排', '期末考试将于下周开始，请同学们做好准备。', null, null),
((SELECT id FROM users WHERE username = 'student3' LIMIT 1), 1, '系统公告：期末考试安排', '期末考试将于下周开始，请同学们做好准备。', null, null),
((SELECT id FROM users WHERE username = 'clubadmin1' LIMIT 1), 3, '活动审核通知', '您创建的"编程马拉松比赛"活动已通过审核。', (SELECT id FROM activities WHERE title = '编程马拉松比赛' LIMIT 1), 'activity'),
((SELECT id FROM users WHERE username = 'student2' LIMIT 1), 3, '活动审核通知', '您创建的"诗歌朗诵会"活动已通过审核。', (SELECT id FROM activities WHERE title = '诗歌朗诵会' LIMIT 1), 'activity'),
((SELECT id FROM users WHERE username = 'student1' LIMIT 1), 2, '社团通知：例行会议', '本周日下午2点将举行篮球队例行会议，请准时参加。', (SELECT id FROM clubs WHERE name = '篮球队' LIMIT 1), 'club'),
((SELECT id FROM users WHERE username = 'student3' LIMIT 1), 2, '社团通知：活动安排', '摄影协会下周将组织外出采风活动，请关注具体通知。', (SELECT id FROM clubs WHERE name = '摄影协会' LIMIT 1), 'club')
ON DUPLICATE KEY UPDATE 
    content = VALUES(content),
    related_id = VALUES(related_id),
    related_type = VALUES(related_type);

-- 11. 插入用户私信
INSERT INTO messages (sender_id, receiver_id, message_type, content, status)
VALUES 
((SELECT id FROM users WHERE username = 'student1' LIMIT 1), (SELECT id FROM users WHERE username = 'student2' LIMIT 1), 1, '你好，周末有空一起讨论项目吗？', 1),
((SELECT id FROM users WHERE username = 'student2' LIMIT 1), (SELECT id FROM users WHERE username = 'student1' LIMIT 1), 1, '好的，周六下午怎么样？', 1),
((SELECT id FROM users WHERE username = 'student1' LIMIT 1), (SELECT id FROM users WHERE username = 'student2' LIMIT 1), 1, '可以，下午2点在图书馆见。', 1),
((SELECT id FROM users WHERE username = 'student3' LIMIT 1), (SELECT id FROM users WHERE username = 'student1' LIMIT 1), 1, '请问上次的活动PPT还在吗？', 0),
((SELECT id FROM users WHERE username = 'clubadmin1' LIMIT 1), (SELECT id FROM users WHERE username = 'clubadmin2' LIMIT 1), 1, '关于下周的讲座，我们需要准备什么材料？', 1),
((SELECT id FROM users WHERE username = 'clubadmin2' LIMIT 1), (SELECT id FROM users WHERE username = 'clubadmin1' LIMIT 1), 1, '主要需要准备演讲稿和演示文稿，我已经开始准备了。', 1)
ON DUPLICATE KEY UPDATE 
    content = VALUES(content),
    status = VALUES(status);

-- 12. 创建会话记录
INSERT INTO conversations (user1_id, user2_id, last_message_id, unread_count, last_message_time)
VALUES 
((SELECT id FROM users WHERE username = 'student1' LIMIT 1), (SELECT id FROM users WHERE username = 'student2' LIMIT 1), (SELECT id FROM messages WHERE content = '可以，下午2点在图书馆见。' AND sender_id = (SELECT id FROM users WHERE username = 'student1' LIMIT 1) AND receiver_id = (SELECT id FROM users WHERE username = 'student2' LIMIT 1) LIMIT 1), 0, '2023-12-10 15:30:00'),
((SELECT id FROM users WHERE username = 'student1' LIMIT 1), (SELECT id FROM users WHERE username = 'student3' LIMIT 1), (SELECT id FROM messages WHERE content = '请问上次的活动PPT还在吗？' AND sender_id = (SELECT id FROM users WHERE username = 'student3' LIMIT 1) AND receiver_id = (SELECT id FROM users WHERE username = 'student1' LIMIT 1) LIMIT 1), 1, '2023-12-10 16:45:00'),
((SELECT id FROM users WHERE username = 'clubadmin1' LIMIT 1), (SELECT id FROM users WHERE username = 'clubadmin2' LIMIT 1), (SELECT id FROM messages WHERE content = '主要需要准备演讲稿和演示文稿，我已经开始准备了。' AND sender_id = (SELECT id FROM users WHERE username = 'clubadmin2' LIMIT 1) AND receiver_id = (SELECT id FROM users WHERE username = 'clubadmin1' LIMIT 1) LIMIT 1), 0, '2023-12-10 14:20:00')
ON DUPLICATE KEY UPDATE 
    last_message_id = VALUES(last_message_id),
    unread_count = VALUES(unread_count),
    last_message_time = VALUES(last_message_time);

-- 13. 插入用户关注关系
INSERT INTO user_follows (follower_id, followed_id, is_mutual)
VALUES 
((SELECT id FROM users WHERE username = 'student1' LIMIT 1), (SELECT id FROM users WHERE username = 'student2' LIMIT 1), true),
((SELECT id FROM users WHERE username = 'student2' LIMIT 1), (SELECT id FROM users WHERE username = 'student1' LIMIT 1), true),
((SELECT id FROM users WHERE username = 'student1' LIMIT 1), (SELECT id FROM users WHERE username = 'student3' LIMIT 1), false),
((SELECT id FROM users WHERE username = 'student3' LIMIT 1), (SELECT id FROM users WHERE username = 'student1' LIMIT 1), true),
((SELECT id FROM users WHERE username = 'clubadmin1' LIMIT 1), (SELECT id FROM users WHERE username = 'clubadmin2' LIMIT 1), true),
((SELECT id FROM users WHERE username = 'clubadmin2' LIMIT 1), (SELECT id FROM users WHERE username = 'clubadmin1' LIMIT 1), true),
((SELECT id FROM users WHERE username = 'student2' LIMIT 1), (SELECT id FROM users WHERE username = 'clubadmin1' LIMIT 1), false),
((SELECT id FROM users WHERE username = 'student3' LIMIT 1), (SELECT id FROM users WHERE username = 'student2' LIMIT 1), true)
ON DUPLICATE KEY UPDATE is_mutual = VALUES(is_mutual);

-- 14. 插入社团申请表单（测试用）
INSERT INTO club_applications (club_id, user_id, apply_reason, status, review_time, reviewer_id, review_remark)
VALUES 
((SELECT id FROM clubs WHERE name = '科技创新社' LIMIT 1), (SELECT id FROM users WHERE username = 'student3' LIMIT 1), '我对科技创新很感兴趣，希望能加入社团学习更多知识。', 1, '2023-11-20 10:00:00', (SELECT id FROM users WHERE username = 'clubadmin1' LIMIT 1), '欢迎加入！'),
((SELECT id FROM clubs WHERE name = '文学社' LIMIT 1), (SELECT id FROM users WHERE username = 'student1' LIMIT 1), '我喜欢文学创作，希望能和志同道合的同学交流。', 1, '2023-11-25 14:30:00', (SELECT id FROM users WHERE username = 'student2' LIMIT 1), '欢迎加入文学社！'),
((SELECT id FROM clubs WHERE name = '篮球队' LIMIT 1), (SELECT id FROM users WHERE username = 'student3' LIMIT 1), '我想学习篮球，提高自己的运动水平。', 0, null, null, null),
((SELECT id FROM clubs WHERE name = '摄影协会' LIMIT 1), (SELECT id FROM users WHERE username = 'clubadmin2' LIMIT 1), '我对摄影很感兴趣，希望能学习专业技巧。', 0, null, null, null)
ON DUPLICATE KEY UPDATE 
    apply_reason = VALUES(apply_reason),
    status = VALUES(status),
    review_time = VALUES(review_time),
    reviewer_id = VALUES(reviewer_id),
    review_remark = VALUES(review_remark);

-- 显示数据初始化完成信息
SELECT '校园社团管理系统数据初始化完成！' AS message;
SELECT '初始化用户信息：' AS info;
SELECT '学生用户: student1 / student123' AS student_account;
SELECT '教师用户: teacher1 / teacher123' AS teacher_account;
SELECT '社团管理员: clubadmin1 / clubadmin123' AS club_admin_account;