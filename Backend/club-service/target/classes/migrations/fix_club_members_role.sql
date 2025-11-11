-- 修复 club_members 表中 role 字段的数据迁移脚本
-- 将字符串角色值转换为整数：
-- 0 = 普通成员
-- 1 = 负责人
-- 2 = 管理员

-- 步骤1: 处理负责人相关角色
UPDATE club_members SET role = 1 
WHERE role IN ('PRESIDENT', 'VICE_PRESIDENT', 'LEADER', 'LEADER_ID', 'CHAIRMAN', 'VICE_CHAIRMAN', 'PRESIDENT_ID')
   OR role LIKE '%PRESIDENT%'
   OR role LIKE '%LEADER%'
   OR role LIKE '%CHAIRMAN%';

-- 步骤2: 处理管理员相关角色
UPDATE club_members SET role = 2 
WHERE role IN ('ADMIN', 'MANAGER', 'CLUB_ADMIN', 'CLUB-MANAGER', 'CLUB_MANAGER', 'VICE_ADMIN', 'ASSISTANT_MANAGER')
   OR role LIKE '%ADMIN%'
   OR role LIKE '%MANAGER%';

-- 步骤3: 处理普通成员角色
UPDATE club_members SET role = 0 
WHERE role IN ('MEMBER', 'USER', 'STUDENT', 'MEMBER_ID')
   OR role IS NULL
   OR role = '';

-- 步骤4: 处理任何剩余的非法值（设为普通成员）
UPDATE club_members SET role = 0 
WHERE role NOT IN ('0', '1', '2')
  AND role NOT REGEXP '^[0-9]+$';

-- 步骤5: 修改列类型为 INT（如果还不是 INT 类型）
-- 注意：执行此步骤前确保所有数据都已转换为整数
ALTER TABLE club_members MODIFY COLUMN role INT NOT NULL DEFAULT 0;

