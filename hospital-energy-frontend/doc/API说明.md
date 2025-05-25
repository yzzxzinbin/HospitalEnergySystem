# Hospital Energy Management System API Documentation

## 1. Authentication API (`/api/auth`)

### 1.1. 用户登录
*   **POST** `/api/auth/login`
    *   **说明:** 用户使用用户名和密码进行登录。
    *   **请求体:** `LoginRequestDto` (包含 `username` 和 `password`)
    *   **成功响应 (200 OK):** 返回 `LoginResponseDto` 对象 (包含 `token` 和 `username`)。
    *   **失败响应 (401 Unauthorized / 403 Forbidden):** 
        *   "Invalid username or password." (凭证无效)
        *   "User account is disabled." (用户被禁用)
        *   "Login failed: An unexpected error occurred." (其他登录错误)

### 1.2. 用户注册
*   **POST** `/api/auth/register`
    *   **说明:** 注册新用户。
    *   **请求体:** `RegistrationRequestDto` (包含 `username`, `password`, `email`)
    *   **成功响应 (201 CREATED):** 返回成功消息字符串，例如: "User registered successfully. Please login."
    *   **失败响应 (400 Bad Request):** "Registration failed: \[错误信息]"

## 2. User Management API (`/api/users`)

### 2.1. 创建用户
*   **POST** `/api/users`
    *   **说明:** 创建一个新用户 (通常由管理员执行)。
    *   **请求体:** `UserDto` (包含 `username`, `password`, `email`, 可选的 `role` 字符串，例如 "ADMIN,USER")
    *   **成功响应 (200 OK):** 返回创建的 `UserDto` 对象 (包含 `id`, `username`, `email`, `role`)。

### 2.2. 根据ID获取用户
*   **GET** `/api/users/{id}`
    *   **说明:** 根据用户ID获取用户信息。
    *   **路径参数:** `id` (用户ID)
    *   **成功响应 (200 OK):** 返回 `UserDto` 对象 (包含 `id`, `username`, `email`, `role`)。
    *   **失败响应 (404 Not Found):** 如果用户不存在。

### 2.3. 获取所有用户
*   **GET** `/api/users`
    *   **说明:** 获取所有用户列表。支持分页和排序。
    *   **请求参数 (Query Parameters):**
        *   `page` (可选, 默认为 0): 页码 (0-indexed)。
        *   `size` (可选, 默认为 10): 每页记录数。
        *   `sort` (可选, 默认为 "id,asc"): 排序字段和方向。格式: `fieldName,(asc|desc)`。例如: `username,desc`。
    *   **成功响应 (200 OK):** 返回 `PageResponseDto<UserDto>` 对象，其中 `UserDto` 包含 `id`, `username`, `email`, `role`。

### 2.4. 更新用户
*   **PUT** `/api/users/{id}`
    *   **说明:** 更新指定ID的用户信息。
    *   **路径参数:** `id` (用户ID)
    *   **请求体:** `UserDto` (包含需要更新的字段，例如 `username`, `email`, `password`, `role`)
    *   **成功响应 (200 OK):** 返回更新后的 `UserDto` 对象 (包含 `id`, `username`, `email`, `role`)。
    *   **失败响应 (404 Not Found):** 如果用户不存在。

### 2.5. 删除用户
*   **DELETE** `/api/users/{id}`
    *   **说明:** 删除指定ID的用户。
    *   **路径参数:** `id` (用户ID)
    *   **成功响应 (204 No Content):** 无返回内容。
    *   **失败响应 (404 Not Found):** 如果用户不存在。

## 3. Room Management API (`/api/rooms`)

### 3.1. 创建房间
*   **POST** `/api/rooms`
    *   **说明:** 创建一个新的房间信息。
    *   **请求体:** `RoomDto`
    *   **成功响应 (201 CREATED):** 返回创建的 `RoomDto` 对象。
    *   **失败响应 (400 Bad Request):** 如果请求数据无效 (例如房间号已存在)。

### 3.2. 获取所有房间
*   **GET** `/api/rooms`
    *   **说明:** 获取所有房间信息列表。支持分页和排序。
    *   **请求参数 (Query Parameters):**
        *   `page` (可选, 默认为 0): 页码 (0-indexed)。
        *   `size` (可选, 默认为 10): 每页记录数。
        *   `sort` (可选, 默认为 "id,asc"): 排序字段和方向。格式: `fieldName,(asc|desc)`。例如: `roomNumber,desc`。
    *   **成功响应 (200 OK):** 返回 `PageResponseDto<RoomDto>` 对象，包含 `records` (房间列表), `total` (总记录数), `currentPage`, `pageSize`, `totalPages`。

### 3.3. 根据ID获取房间
*   **GET** `/api/rooms/{id}`
    *   **说明:** 根据ID获取指定的房间信息。
    *   **路径参数:** `id` (房间ID)
    *   **成功响应 (200 OK):** 返回 `RoomDto` 对象。
    *   **失败响应 (404 Not Found):** 如果房间不存在。

### 3.4. 根据房间号获取房间
*   **GET** `/api/rooms/by-room-number/{roomNumber}`
    *   **说明:** 根据房间号获取指定的房间信息。
    *   **路径参数:** `roomNumber` (房间号)
    *   **成功响应 (200 OK):** 返回 `RoomDto` 对象。
    *   **失败响应 (404 Not Found):** 如果房间不存在。

### 3.5. 根据楼层获取房间列表
*   **GET** `/api/rooms/by-floor/{floor}`
    *   **说明:** 获取指定楼层的所有房间信息。
    *   **路径参数:** `floor` (楼层号)
    *   **成功响应 (200 OK):** 返回 `RoomDto` 对象列表。

### 3.6. 根据部门获取房间列表
*   **GET** `/api/rooms/by-department/{department}`
    *   **说明:** 获取指定部门的所有房间信息。
    *   **路径参数:** `department` (部门名称)
    *   **成功响应 (200 OK):** 返回 `RoomDto` 对象列表。

### 3.7. 更新房间信息
*   **PUT** `/api/rooms/{id}`
    *   **说明:** 更新指定ID的房间信息。
    *   **路径参数:** `id` (房间ID)
    *   **请求体:** `RoomDto`
    *   **成功响应 (200 OK):** 返回更新后的 `RoomDto` 对象。
    *   **失败响应 (400 Bad Request):** 如果请求数据无效或房间不存在。

### 3.8. 删除房间
*   **DELETE** `/api/rooms/{id}`
    *   **说明:** 删除指定ID的房间。
    *   **路径参数:** `id` (房间ID)
    *   **成功响应 (204 No Content):** 无返回内容。
    *   **失败响应 (404 Not Found):** 如果房间不存在。

## 4. Device Template Management API (`/api/device-templates`)

### 4.1. 创建设备模板
*   **POST** `/api/device-templates`
    *   **说明:** 创建一个新的设备模板。
    *   **请求体:** `DeviceTemplateDto`
    *   **成功响应 (201 CREATED):** 返回创建的 `DeviceTemplateDto` 对象。
    *   **失败响应 (400 Bad Request):** 如果请求数据无效 (例如制造商和型号已存在)。

### 4.2. 获取所有设备模板
*   **GET** `/api/device-templates`
    *   **说明:** 获取所有设备模板列表。支持分页和排序。
    *   **请求参数 (Query Parameters):**
        *   `page` (可选, 默认为 0): 页码 (0-indexed)。
        *   `size` (可选, 默认为 10): 每页记录数。
        *   `sort` (可选, 默认为 "id,asc"): 排序字段和方向。格式: `fieldName,(asc|desc)`。例如: `manufacturer,asc`。
    *   **成功响应 (200 OK):** 返回 `PageResponseDto<DeviceTemplateDto>` 对象，包含 `records` (设备模板列表), `total` (总记录数), `currentPage`, `pageSize`, `totalPages`。

### 4.3. 根据ID获取设备模板
*   **GET** `/api/device-templates/{id}`
    *   **说明:** 根据ID获取指定的设备模板。
    *   **路径参数:** `id` (设备模板ID)
    *   **成功响应 (200 OK):** 返回 `DeviceTemplateDto` 对象。
    *   **失败响应 (404 Not Found):** 如果设备模板不存在。

### 4.4. 根据制造商获取设备模板列表
*   **GET** `/api/device-templates/by-manufacturer/{manufacturer}`
    *   **说明:** 获取指定制造商的所有设备模板。
    *   **路径参数:** `manufacturer` (制造商名称)
    *   **成功响应 (200 OK):** 返回 `DeviceTemplateDto` 对象列表。

### 4.5. 根据类别获取设备模板列表
*   **GET** `/api/device-templates/by-category/{category}`
    *   **说明:** 获取指定类别的所有设备模板。
    *   **路径参数:** `category` (设备类别)
    *   **成功响应 (200 OK):** 返回 `DeviceTemplateDto` 对象列表。

### 4.6. 根据制造商和型号获取设备模板
*   **GET** `/api/device-templates/by-manufacturer-model`
    *   **说明:** 根据制造商和型号标识获取指定的设备模板。
    *   **请求参数:** `manufacturer` (制造商名称), `modelIdentifier` (型号标识)
    *   **成功响应 (200 OK):** 返回 `DeviceTemplateDto` 对象。
    *   **失败响应 (404 Not Found):** 如果设备模板不存在。

### 4.7. 更新设备模板
*   **PUT** `/api/device-templates/{id}`
    *   **说明:** 更新指定ID的设备模板。
    *   **路径参数:** `id` (设备模板ID)
    *   **请求体:** `DeviceTemplateDto`
    *   **成功响应 (200 OK):** 返回更新后的 `DeviceTemplateDto` 对象。
    *   **失败响应 (400 Bad Request):** 如果请求数据无效或设备模板不存在。

### 4.8. 删除设备模板
*   **DELETE** `/api/device-templates/{id}`
    *   **说明:** 删除指定ID的设备模板。
    *   **路径参数:** `id` (设备模板ID)
    *   **成功响应 (204 No Content):** 无返回内容。
    *   **失败响应 (404 Not Found):** 如果设备模板不存在。

## 5. Device Management API (`/api/devices`)

### 5.1. 创建设备
*   **POST** `/api/devices`
    *   **说明:** 创建一个新的设备实例。
    *   **请求体:** `DeviceDto` (应包含 `roomId` 和/或 `deviceTemplateId`)
    *   **成功响应 (201 CREATED):** 返回创建的 `DeviceDto` 对象。
    *   **失败响应 (400 Bad Request):** 如果请求数据无效 (例如关联的房间或模板不存在)。

### 5.2. 获取所有设备
*   **GET** `/api/devices`
    *   **说明:** 获取所有设备实例列表。支持分页和排序。
    *   **请求参数 (Query Parameters):**
        *   `page` (可选, 默认为 0): 页码 (0-indexed)。
        *   `size` (可选, 默认为 10): 每页记录数。
        *   `sort` (可选, 默认为 "id,asc"): 排序字段和方向。格式: `fieldName,(asc|desc)`。例如: `name,desc`。
    *   **成功响应 (200 OK):** 返回 `PageResponseDto<DeviceDto>` 对象，包含 `records` (设备列表), `total` (总记录数), `currentPage`, `pageSize`, `totalPages`。

### 5.3. 根据ID获取设备
*   **GET** `/api/devices/{id}`
    *   **说明:** 根据ID获取指定的设备实例。
    *   **路径参数:** `id` (设备ID)
    *   **成功响应 (200 OK):** 返回 `DeviceDto` 对象。
    *   **失败响应 (404 Not Found):** 如果设备不存在。

### 5.4. 更新设备信息
*   **PUT** `/api/devices/{id}`
    *   **说明:** 更新指定ID的设备信息。
    *   **路径参数:** `id` (设备ID)
    *   **请求体:** `DeviceDto`
    *   **成功响应 (200 OK):** 返回更新后的 `DeviceDto` 对象。
    *   **失败响应 (400 Bad Request):** 如果请求数据无效或设备不存在。

### 5.5. 删除设备
*   **DELETE** `/api/devices/{id}`
    *   **说明:** 删除指定ID的设备。
    *   **路径参数:** `id` (设备ID)
    *   **成功响应 (204 No Content):** 无返回内容。
    *   **失败响应 (404 Not Found):** 如果设备不存在。

### 5.6. 根据房间ID获取设备列表
*   **GET** `/api/devices/by-room/{roomId}`
    *   **说明:** 获取指定房间内的所有设备。
    *   **路径参数:** `roomId` (房间ID)
    *   **成功响应 (200 OK):** 返回 `DeviceDto` 对象列表。
    *   **失败响应 (404 Not Found):** 如果房间不存在。

### 5.7. 根据设备模板ID获取设备列表
*   **GET** `/api/devices/by-template/{templateId}`
    *   **说明:** 获取使用指定设备模板的所有设备。
    *   **路径参数:** `templateId` (设备模板ID)
    *   **成功响应 (200 OK):** 返回 `DeviceDto` 对象列表。
    *   **失败响应 (404 Not Found):** 如果设备模板不存在。

### 5.8. 根据状态获取设备列表
*   **GET** `/api/devices/by-status/{status}`
    *   **说明:** 获取具有指定状态的所有设备。
    *   **路径参数:** `status` (设备状态，例如 "Operational", "Maintenance")
    *   **成功响应 (200 OK):** 返回 `DeviceDto` 对象列表。

## 6. Energy Data Management API (`/api/energy-data`)

### 6.1. 创建能源数据记录
*   **POST** `/api/energy-data`
    *   **说明:** 创建一条新的能源数据记录。
    *   **请求体:** `EnergyDataDto` (应包含 `deviceId` 如果数据与特定设备关联)
    *   **成功响应 (201 CREATED):** 返回创建的 `EnergyDataDto` 对象。
    *   **失败响应 (400 Bad Request):** 如果请求数据无效 (例如关联的设备不存在)。

### 6.2. 根据ID获取能源数据记录
*   **GET** `/api/energy-data/{id}`
    *   **说明:** 根据ID获取指定的能源数据记录。
    *   **路径参数:** `id` (能源数据ID)
    *   **成功响应 (200 OK):** 返回 `EnergyDataDto` 对象。
    *   **失败响应 (404 Not Found):** 如果记录不存在。

### 6.3. 获取所有能源数据记录
*   **GET** `/api/energy-data`
    *   **说明:** 获取所有能源数据记录列表。支持分页、排序和过滤。
    *   **请求参数 (Query Parameters):**
        *   `page` (可选, 默认为 0): 页码 (0-indexed)。
        *   `size` (可选, 默认为 10): 每页记录数。
        *   `sort` (可选, 默认为 "timestamp,desc"): 排序字段和方向。格式: `fieldName,(asc|desc)`。例如: `timestamp,desc` 或 `value,asc`。
        *   `type` (可选): 能源类型 (例如 "electricity", "water")，用于筛选。
        *   `deviceId` (可选): 设备ID，用于筛选特定设备的数据。
        *   `startDate` (可选, ISO DATE_TIME格式, 例如 "2023-01-01T00:00:00"): 开始时间，用于筛选指定时间范围的数据。
        *   `endDate` (可选, ISO DATE_TIME格式, 例如 "2023-01-31T23:59:59"): 结束时间，用于筛选指定时间范围的数据。
    *   **成功响应 (200 OK):** 返回 `PageResponseDto<EnergyDataDto>` 对象，包含 `records` (能源数据列表), `total` (总记录数), `currentPage`, `pageSize`, `totalPages`。

### 6.4. 更新能源数据记录
*   **PUT** `/api/energy-data/{id}`
    *   **说明:** 更新指定ID的能源数据记录。
    *   **路径参数:** `id` (能源数据ID)
    *   **请求体:** `EnergyDataDto`
    *   **成功响应 (200 OK):** 返回更新后的 `EnergyDataDto` 对象。
    *   **失败响应 (400 Bad Request):** 如果请求数据无效或记录不存在。

### 6.5. 删除能源数据记录
*   **DELETE** `/api/energy-data/{id}`
    *   **说明:** 删除指定ID的能源数据记录。
    *   **路径参数:** `id` (能源数据ID)
    *   **成功响应 (204 No Content):** 无返回内容。
    *   **失败响应 (404 Not Found):** 如果记录不存在。

### 6.6. 根据设备ID获取能源数据列表
*   **GET** `/api/energy-data/by-device/{deviceId}`
    *   **说明:** 获取指定设备的所有能源数据记录。
    *   **路径参数:** `deviceId` (设备ID)
    *   **成功响应 (200 OK):** 返回 `EnergyDataDto` 对象列表。
    *   **失败响应 (404 Not Found):** 如果设备不存在。

### 6.7. 根据能源类型获取能源数据列表
*   **GET** `/api/energy-data/by-type/{type}`
    *   **说明:** 获取指定能源类型的所有数据记录。
    *   **路径参数:** `type` (能源类型，例如 "electricity", "water")
    *   **成功响应 (200 OK):** 返回 `EnergyDataDto` 对象列表。

### 6.8. 根据时间范围获取能源数据列表
*   **GET** `/api/energy-data/by-timestamp`
    *   **说明:** 获取指定时间范围内的所有能源数据记录。
    *   **请求参数:**
        *   `start` (开始时间, ISO DATE_TIME格式, 例如 "2023-01-01T00:00:00")
        *   `end` (结束时间, ISO DATE_TIME格式, 例如 "2023-01-31T23:59:59")
    *   **成功响应 (200 OK):** 返回 `EnergyDataDto` 对象列表。

### 6.9. 根据设备ID和时间范围获取能源数据列表
*   **GET** `/api/energy-data/by-device-timestamp/{deviceId}`
    *   **说明:** 获取指定设备在指定时间范围内的能源数据记录。
    *   **路径参数:** `deviceId` (设备ID)
    *   **请求参数:**
        *   `start` (开始时间, ISO DATE_TIME格式)
        *   `end` (结束时间, ISO DATE_TIME格式)
    *   **成功响应 (200 OK):** 返回 `EnergyDataDto` 对象列表。
    *   **失败响应 (404 Not Found):** 如果设备不存在。

## 仪表盘 API

### 1. 获取仪表盘摘要数据

*   **URL:** `/api/dashboard/summary`
*   **Method:** `GET`
*   **Description:** 获取最新的仪表盘摘要信息，包括设备状态统计、实时能耗数据和最近7天的能耗历史。
*   **Success Response:**
    *   **Code:** 200 OK
    *   **Content:** `DashboardSummaryDto`
        ```json
        {
            "onlineDeviceCount": 150,
            "offlineDeviceCount": 10,
            "maintenanceDeviceCount": 5,
            "faultyDeviceCount": 2,
            "realtimeElectricityPower": 120.5,
            "realtimeElectricityConsumption": 2850.75,
            "realtimeWaterPower": 30.2,
            "realtimeWaterConsumption": 750.0,
            "realtimeGasPower": 15.8,
            "realtimeGasConsumption": 320.5,
            "last7DaysElectricityConsumption": [
                {"date": "2023-10-01", "consumption": 400.0},
                {"date": "2023-10-02", "consumption": 410.5}
                // ... more days
            ],
            "last7DaysWaterConsumption": [
                {"date": "2023-10-01", "consumption": 100.0},
                {"date": "2023-10-02", "consumption": 105.0}
                // ... more days
            ],
            "last7DaysGasConsumption": [
                {"date": "2023-10-01", "consumption": 50.0},
                {"date": "2023-10-02", "consumption": 52.5}
                // ... more days
            ],
            "lastUpdatedAt": "2023-10-07T10:30:00"
        }
        ```
*   **Error Response:**
    *   **Code:** 404 Not Found
    *   **Content:** If no summary data is available.

### 2. 手动刷新仪表盘摘要数据

*   **URL:** `/api/dashboard/refresh`
*   **Method:** `POST`
*   **Description:** 手动触发后台存储过程，以重新计算并更新仪表盘摘要数据。这通常由计划任务自动执行，但此端点可用于即时刷新。
*   **Success Response:**
    *   **Code:** 200 OK
*   **Error Response:**
    *   Potentially 500 Internal Server Error if the stored procedure call fails.
