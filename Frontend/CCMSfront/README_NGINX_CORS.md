# 使用Nginx解决CORS跨域问题

本文档提供了使用Nginx代理来解决前端与后端之间的CORS跨域问题的完整指南。

## 问题分析

当前系统遇到的CORS错误是由于浏览器的同源策略导致的。错误信息显示：
- 已拦截跨源请求：同源策略禁止读取位于 http://localhost:8000/api/api/auth/register 的远程资源
- 原因：CORS 头缺少 'Access-Control-Allow-Origin'
- 注意：URL中出现了重复的 `/api` 前缀

## 解决方案

我们采用以下两步解决这个问题：

### 1. 修复API路径配置

已完成以下修改：

- 在 `apiService.ts` 中移除了所有API路径中的 `/api` 前缀
- 在 `axiosInstance.ts` 中更新了刷新token的路径和认证路径列表
- 这些修改确保了不会出现重复的 `/api` 前缀

### 2. 使用Nginx作为代理服务器

已创建 `nginx.conf` 配置文件，提供了完整的代理和CORS头设置。

## 如何使用Nginx

### 前提条件

确保你的系统已安装Nginx。如果未安装，可以从官方网站下载并安装。

### 启动Nginx服务

按照以下步骤启动Nginx服务：

1. **Windows系统**：
   ```bash
   # 以管理员身份打开命令提示符
   cd C:\nginx  # 你的nginx安装目录
   nginx -c e:\trae_progs\CampusClubManaSys\frontend\nginx.conf  # 指定配置文件路径
   ```

2. **Linux/Mac系统**：
   ```bash
   sudo nginx -c e:/trae_progs/CampusClubManaSys/frontend/nginx.conf
   ```

### 重要说明

1. **确保以下服务正在运行**：
   - 前端Vite开发服务器（运行在端口3001）
   - 后端API服务（运行在端口8000）
   - Nginx服务（运行在端口8080）

2. **访问应用程序**：
   - 现在你应该通过 `http://localhost:8080` 访问应用程序，而不是直接访问3001端口
   - Nginx会自动代理请求到正确的服务并添加必要的CORS头

3. **验证CORS问题是否解决**：
   - 打开浏览器的开发者工具（F12）
   - 切换到Network标签页
   - 尝试执行登录或注册操作
   - 检查API请求是否成功，不再出现CORS错误

## 配置文件说明

`nginx.conf` 配置文件包含以下关键部分：

1. **events块**：Nginx的必要配置块，定义工作进程的连接数限制
   - `worker_connections 1024;`：每个工作进程的最大连接数

2. **http块**：Nginx的主要配置块，server指令必须放在http块内部

3. **server块**：定义虚拟主机配置
   - `listen 8080;`：Nginx监听端口
   - `server_name localhost;`：服务器名称

4. **静态文件代理**：将所有根路径请求代理到Vite开发服务器
   - **WebSocket支持配置**：
     - `proxy_http_version 1.1;`：使用HTTP 1.1协议
     - `proxy_set_header Upgrade $http_upgrade;`：升级连接为WebSocket
     - `proxy_set_header Connection "upgrade";`：设置连接头
     - `proxy_set_header Origin "http://localhost:3001";`：设置源地址

5. **API代理**：将所有 `/api/` 路径请求代理到后端服务

6. **CORS头设置**：添加了所有必要的CORS头信息

7. **预检请求处理**：正确处理OPTIONS请求

**重要提示**：
- Nginx配置文件必须包含events块，否则会报错 "no events section in configuration"
- server指令必须位于http块内部，否则会报错 "server directive is not allowed here"
- 配置文件必须遵循正确的层次结构：events块和http块是同级的顶级配置

- **WebSocket配置**：
  - 为了支持热模块替换(HMR)和实时更新，必须在Nginx配置中添加WebSocket支持
  - WebSocket配置必须在前端静态文件的location块中设置
  - 同时需要在Vite配置中设置正确的HMR选项

- **端口一致性**：
  - 确保Vite配置中的端口(3001)与Nginx配置中的代理端口一致
  - 所有访问必须通过Nginx的8080端口，而不是直接访问Vite服务器

## 停止Nginx服务

完成测试后，可以通过以下命令停止Nginx服务：

```bash
# Windows
nginx -s stop

# Linux/Mac
sudo nginx -s stop
```

## 替代方案

如果无法使用Nginx，还有以下替代方案：

1. 在后端代码中添加CORS支持
2. 修改Vite的代理配置，添加CORS头信息
3. 使用浏览器插件暂时禁用CORS（仅用于开发测试）

但使用Nginx是最可靠和生产环境友好的解决方案。