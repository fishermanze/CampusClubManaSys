CCMS 项目部署说明
1. 环境依赖
JDK 17 及以上
MySQL 8.0 及以上（推荐）
Redis 6.0 及以上（用于 JWT 令牌存储）
2. 数据库配置步骤
2.1 安装并启动 MySQL
下载并安装 MySQL
启动 MySQL 服务：
Windows：通过 “服务” 面板启动 MySQL 服务，或命令行执行 net start mysql（需管理员权限）
Linux/Mac：命令行执行 sudo systemctl start mysql
2.2 创建项目数据库
登录 MySQL 客户端（命令行或可视化工具如 Navicat）：
bash
mysql -u root -p
输入 MySQL 管理员密码登录。
执行 SQL 创建数据库（名称需与配置一致）：
sql
-- 创建数据库并设置编码（支持中文）
CREATE DATABASE IF NOT EXISTS ccms_db 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;
2.3 修改项目数据库连接配置
找到项目配置文件：src/main/resources/application.properties
替换以下配置为你的 MySQL 实际信息：
properties
# 数据库连接地址（默认端口3306，数据库名为ccms_db）
spring.datasource.url=jdbc:mysql://localhost:3306/ccms_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true

# 替换为你的MySQL用户名（默认root）
spring.datasource.username=你的MySQL用户名

# 替换为你的MySQL密码
spring.datasource.password=你的MySQL密码
3. Redis 配置（可选，不配置会影响 JWT 功能）
下载并启动 Redis：官方下载地址
若 Redis 服务地址 / 端口非默认（默认 localhost:6379），修改配置：
properties
# Redis服务器地址（默认localhost）
spring.data.redis.host=你的Redis地址
# Redis端口（默认6379）
spring.data.redis.port=你的Redis端口
# 若Redis有密码，添加：
# spring.data.redis.password=你的Redis密码