# Hospital Energy Management System

**注意：这是一个学生课程设计演示性项目。**

## 项目简介
该项目是一个医院智慧能源管理系统的后端框架演示性项目，基于Spring Boot构建。系统旨在实现对医院能源的实时监控、数据采集、能耗分析等功能，帮助医院有效管理能源使用。

## 功能模块
1.  **认证管理 (`/api/auth`)**
    *   用户登录 (`POST /api/auth/login`)
    *   用户注册 (`POST /api/auth/register`)

2.  **用户管理 (`/api/users`)**
    *   创建、获取、更新、删除用户
    *   角色及权限管理 (通过Spring Security及用户角色关联实现)

3.  **房间管理 (`/api/rooms`)**
    *   创建、获取、更新、删除房间信息
    *   根据房间号、楼层、部门查询房间

4.  **设备模板管理 (`/api/device-templates`)**
    *   创建、获取、更新、删除设备模板
    *   根据制造商、类别、制造商+型号查询设备模板

5.  **设备管理 (`/api/devices`)**
    *   创建、获取、更新、删除设备实例
    *   根据房间ID、模板ID、状态查询设备
    *   设备信息录入与状态监控

6.  **能源数据管理 (`/api/energy-data`)**
    *   创建、获取、更新、删除能源数据记录
    *   根据设备ID、能源类型、时间范围查询能源数据
    *   支持能源数据采集与存储

7.  **能源监控与分析 (基于能源数据API)**
    *   支持历史数据查询
    *   为前端提供数据以实现实时监控仪表板、能耗统计与趋势分析

8.  **报表生成 (概念性，可基于API数据在前端实现)**
    *   为前端提供数据以生成自定义报表及导出

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
  - Swagger UI（用于API文档测试和查看）
  - 详细API说明参见 `doc/API说明.md`

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
│   │   │               │   ├── AppConfig.java
│   │   │               │   ├── SecurityConfig.java
│   │   │               │   └── SwaggerConfig.java
│   │   │               ├── controller
│   │   │               │   ├── AuthController.java
│   │   │               │   ├── DeviceController.java
│   │   │               │   ├── DeviceTemplateController.java
│   │   │               │   ├── EnergyDataController.java
│   │   │               │   ├── RoomController.java
│   │   │               │   └── UserController.java
│   │   │               ├── model
│   │   │               │   ├── Device.java
│   │   │               │   ├── DeviceTemplate.java
│   │   │               │   ├── EnergyData.java
│   │   │               │   ├── Role.java
│   │   │               │   ├── Room.java
│   │   │               │   └── User.java
│   │   │               ├── repository
│   │   │               │   ├── DeviceRepository.java
│   │   │               │   ├── DeviceTemplateRepository.java
│   │   │               │   ├── EnergyDataRepository.java
│   │   │               │   ├── RoomRepository.java
│   │   │               │   └── UserRepository.java
│   │   │               ├── service
│   │   │               │   ├── AuthService.java
│   │   │               │   ├── DeviceService.java
│   │   │               │   ├── DeviceTemplateService.java
│   │   │               │   ├── EnergyDataService.java
│   │   │               │   ├── RoomService.java
│   │   │               │   └── UserService.java
│   │   │               └── dto
│   │   │                   ├── DeviceDto.java
│   │   │                   ├── DeviceTemplateDto.java
│   │   │                   ├── EnergyDataDto.java
│   │   │                   ├── LoginRequest.java
│   │   │                   ├── RegistrationRequest.java
│   │   │                   ├── RoomDto.java
│   │   │                   └── UserDto.java
│   │   └── resources
│   │       ├── application.properties
│   │       ├── mybatis
│   │       │   └── mappers
│   │       │       ├── UserMapper.xml
│   │       │       ├── EnergyDataMapper.xml
│   │       │       └── <!-- 其他Mapper XML文件 (如果存在) -->
│   │       └── static
│   └── test
│       └── java
│           └── com
│               └── hospital
│                   └── energymgmt
│                       └── EnergyManagementApplicationTests.java
├── pom.xml
├── README.md
└── doc
    ├── API说明.md
    └── 表结构说明性文档.md
```

## 使用说明
1. 克隆项目到本地。
2. 配置数据库连接信息在 `src/main/resources/application.properties` 文件中。
3. 使用Maven构建项目：`mvn clean install`。
4. 运行应用程序：`mvn spring-boot:run`。
5. 访问API文档：通过Swagger UI (通常是 `http://localhost:8080/swagger-ui/index.html`) 或查阅 `doc/API说明.md`。
