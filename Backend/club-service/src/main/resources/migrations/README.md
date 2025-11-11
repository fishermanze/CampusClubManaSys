# 数据库迁移脚本说明

## 修复 club_members.role 字段问题

### 问题描述
当 `club_members` 表中的 `role` 字段包含字符串值（如 "VICE_PRESIDENT"、"PRESIDENT" 等）时，后端会抛出 `NumberFormatException` 错误，因为实体类期望 `role` 是整数类型。

### 解决方案

#### 方案 1：立即执行 SQL 脚本（推荐）

1. 连接到数据库
2. 执行 `fix_club_members_role.sql` 脚本：

```bash
mysql -u your_username -p your_database < fix_club_members_role.sql
```

或者直接在数据库客户端中执行脚本内容。

#### 方案 2：重启应用自动修复

应用启动时，`RoleColumnMigrator` 会自动运行并修复数据。重启 `club-service` 应用即可。

### 角色映射规则

- **0** = 普通成员（MEMBER, USER, STUDENT 等）
- **1** = 负责人（PRESIDENT, VICE_PRESIDENT, LEADER, CHAIRMAN 等）
- **2** = 管理员（ADMIN, MANAGER, CLUB_ADMIN 等）

### 注意事项

1. 执行脚本前，建议备份数据库
2. 脚本会修改 `role` 列的类型为 `INT`，确保所有数据都已转换为整数
3. 如果执行脚本后仍有问题，检查日志中的迁移信息

