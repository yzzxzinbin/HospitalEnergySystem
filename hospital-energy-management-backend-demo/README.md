# Hospital Energy Management System

## 项目简介
该项目是一个医院智慧能源管理系统的后端框架演示性项目，基于Spring Boot构建。系统旨在实现对医院能源的实时监控、数据采集、能耗分析等功能，帮助医院有效管理能源使用。

## 功能模块
1. **用户管理**
   - 用户注册与登录
   - 角色管理（管理员、普通用户）
   - 权限管理

2. **能源数据采集**
   - 实时数据采集（如电力、水、气等）
   - 数据存储与备份

3. **能源监控**
   - 实时监控仪表板
   - 历史数据查询
   - 报警与通知

4. **能耗分析**
   - 能耗统计（日、周、月、年）
   - 能耗趋势分析
   - 能耗对比（部门、设备）

5. **设备管理**
   - 设备信息录入
   - 设备状态监控
   - 设备维护记录

6. **报表生成**
   - 自定义报表
   - 导出报表（Excel、PDF）

## 技术栈
- **前端**: Vue.js 2
  - 框架：Vue CLI
  - 状态管理：Vuex
  - 路由：Vue Router
  - UI库：Element UI

- **后端**: Spring Boot
  - 持久层：MyBatis
  - 数据库：MySQL
  - 安全：Spring Security
  - 缓存：Redis（可选）

- **数据库**
  - MySQL（用于存储业务数据）
  - Redis（可选，用于缓存频繁访问的数据）

- **接口**
  - RESTful API
  - Swagger UI（用于API文档）

## 项目结构
```
hospital-energy-management-backend-demo
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── hospital
│   │   │           └── energymgmt
│   │   │               ├── EnergyManagementApplication.java
│   │   │               ├── config
│   │   │               │   ├── SecurityConfig.java
│   │   │               │   └── SwaggerConfig.java
│   │   │               ├── controller
│   │   │               │   ├── AuthController.java
│   │   │               │   ├── UserController.java
│   │   │               │   └── EnergyDataController.java
│   │   │               ├── model
│   │   │               │   ├── User.java
│   │   │               │   ├── Role.java
│   │   │               │   └── EnergyData.java
│   │   │               ├── repository
│   │   │               │   ├── UserRepository.java
│   │   │               │   └── EnergyDataRepository.java
│   │   │               ├── service
│   │   │               │   ├── AuthService.java
│   │   │               │   ├── UserService.java
│   │   │               │   └── EnergyDataService.java
│   │   │               └── dto
│   │   │                   ├── LoginRequest.java
│   │   │                   ├── UserDto.java
│   │   │                   └── EnergyDataDto.java
│   │   └── resources
│   │       ├── application.properties
│   │       ├── mybatis
│   │       │   └── mappers
│   │       │       ├── UserMapper.xml
│   │       │       └── EnergyDataMapper.xml
│   │       └── static
│   └── test
│       └── java
│           └── com
│               └── hospital
│                   └── energymgmt
│                       └── EnergyManagementApplicationTests.java
├── pom.xml
└── README.md
```

## 使用说明
1. 克隆项目到本地。
2. 配置数据库连接信息在 `src/main/resources/application.properties` 文件中。
3. 使用Maven构建项目：`mvn clean install`。
4. 运行应用程序：`mvn spring-boot:run`。
5. 访问API文档：通过Swagger UI查看接口文档。

## 贡献
欢迎任何形式的贡献！请提交问题或拉取请求。