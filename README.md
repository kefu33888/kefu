# 33 客服台 - 在线咨询服务平台

> 一个基于 Spring Boot + Vue.js 的全栈客服管理系统

## 功能特性

- 👤 管理员后台系统
- 🔐 基于 Token 的身份验证
- 👥 用户管理、订单管理
- 📊 系统监控、数据统计
- 🎯 权限管理系统
- 📱 响应式前端界面

## 快速开始

### 环境要求

- Java 17+
- Maven 3.9+
- Docker（可选）

### 本地部署

1. **克隆仓库**
   ```bash
   git clone https://github.com/zchx8888/kefu.git
   cd kefu
   ```

2. **构建项目**
   ```bash
   mvn clean package
   ```

3. **运行应用**
   ```bash
   mvn spring-boot:run
   ```

4. **访问应用**
   - 前端：http://localhost:8080/
   - 账号：`hxzc33`
   - 密码：`123456`

### Docker 部署

1. **构建镜像**
   ```bash
   docker build -t kefu:latest .
   ```

2. **运行容器**
   ```bash
   docker run -d -p 8080:8080 \
     -e SPRING_DATASOURCE_URL="jdbc:mysql://mysql:3306/kefu" \
     -e SPRING_DATASOURCE_USERNAME="root" \
     -e SPRING_DATASOURCE_PASSWORD="password" \
     kefu:latest
   ```

## 项目结构

```
kefu/
├── src/
│   ├── main/
│   │   ├── java/com/kefu/              # 后端源代码
│   │   │   ├── controller/             # API 控制器
│   │   │   ├── config/                 # 配置类
│   │   │   ├── filter/                 # 请求过滤器
│   │   │   ├── security/               # 安全认证
│   │   │   └── KefuApplication.java
│   │   └── resources/
│   │       ├── application.yml         # Spring Boot 配置
│   │       └── static/                 # 前端资源（Vue.js 编译产物）
│   └── test/                           # 测试代码
├── pom.xml                             # Maven 配置
├── Dockerfile                          # Docker 配置
└── README.md                           # 本文档
```

## 核心功能

### 身份验证
- Token 基认证
- 内置管理员账号
- Spring Security 集成

### 前端
- Vue.js 框架
- 响应式设计
- PWA 支持

### 后端
- Spring Boot 3.1.2
- Spring Security 6
- H2/MySQL 数据库支持

## API 端点

| 方法 | 端点 | 说明 |
|------|------|------|
| POST | `/api/auth/login` | 用户登录 |
| GET | `/api/auth/me` | 获取当前用户信息 |
| POST | `/api/auth/logout` | 用户登出 |

## 配置文件

### application.yml
主要配置项：
- 数据库连接
- Spring Security
- 日志级别

### application.properties
可选的属性文件配置

## 开发说明

### 添加新的 API 端点
1. 在 `src/main/java/com/kefu/controller/` 中创建控制器
2. 定义 REST 端点
3. 在 SecurityConfig 中配置权限

### 修改前端
前端代码已编译为 JS/CSS 产物存储在 `src/main/resources/static/`
若需修改前端源代码，需要单独的 Vue 项目进行编译

## 常见问题

### Q: 如何修改管理员密码？
A: 修改 AuthController.java 中的硬编码凭证，然后重新构建

### Q: 支持哪些数据库？
A: 默认使用 H2（内存数据库），可配置 MySQL 等关系型数据库

### Q: 如何部署到生产环境？
A: 使用 Docker 部署或直接运行 JAR 文件，推荐在部署平台（如云服务）自动识别 Dockerfile 进行部署

## 自动部署

本仓库包含 Dockerfile，支持在以下部署平台自动部署：
- Docker Hub
- GitHub Container Registry
- 各类云服务平台（阿里云、腾讯云等）

部署平台会自动识别 Dockerfile、pom.xml 和应用配置文件，实现一键部署。

## 许可证

MIT

## 联系方式

- Issues: https://github.com/zchx8888/kefu/issues
