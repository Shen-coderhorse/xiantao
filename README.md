# 🛒 闲淘 XianTao — 校园二手交易平台

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.3-6db33f?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Vue 3](https://img.shields.io/badge/Vue-3.4.21-42b883?logo=vuedotjs&logoColor=white)](https://vuejs.org/)
[![Java](https://img.shields.io/badge/Java-21-ed8b00?logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479a1?logo=mysql&logoColor=white)](https://www.mysql.com/)
[![uni-app](https://img.shields.io/badge/uni--app-Vue3-4fc08d?logo=uniapp&logoColor=white)](https://uniapp.dcloud.net.cn/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

> 前后端分离架构，三端并行（网页端 + 管理后台 + 微信小程序），担保交易保障资金安全

---

## ✨ 核心特性

- 🛡️ **担保交易** — 买家付款 → 平台托管 → 卖家发货 → 确认收货 → 资金解冻，全流程资金安全保障
- 📊 **信用体系** — 基于交易行为自动计算信用分（300-850），5 级信用等级可视化展示
- 🚚 **物流追踪** — 支持主流物流公司，模拟物流轨迹实时可视化展示
- 📱 **三端并行** — 网页端（Vue 3）、管理端（Vue 3 + ECharts）、小程序端（uni-app）
- 🔐 **JWT 认证** — Token 有效期 24 小时，BCrypt 密码加密，Spring Validation 参数校验
- 📈 **数据看板** — 管理端集成 ECharts 可视化统计，实时掌握平台运营数据

---

## 🚀 快速开始

### 环境要求

| 环境 | 最低版本 | 说明 |
|------|---------|------|
| JDK | 21+ | 后端运行环境 |
| Node.js | 18+ | 前端构建工具 |
| Maven | 3.8+ | Java 项目管理 |
| MySQL | 8.0+ | 关系型数据库 |

### 1️⃣ 数据库初始化

```bash
mysql -u root -p
source e:/xiantao/xiantao-server/sql/init.sql
```

### 2️⃣ 启动后端

```bash
cd xiantao-server
mvn spring-boot:run
```

> 后端默认运行在 `http://localhost:8080`

### 3️⃣ 启动前端

**网页端：**
```bash
cd xiantao-web && npm install && npm run dev
# → http://localhost:5173
```

**管理后台：**
```bash
cd xiantao-admin && npm install && npm run dev
# → http://localhost:5174
```

**微信小程序端：**
```bash
cd xiantao-miniprogram && npm install && npm run dev:h5
```

### 🔑 测试账号

| 账号 | 密码 | 角色 | 余额 |
|------|------|------|------|
| test001 | 123456 | 普通用户 | ¥10,000 |
| test002 | 123456 | 普通用户 | ¥8,000 |
| test003 | 123456 | 普通用户 | ¥5,000 |
| admin | 123456 | 管理员 | ¥99,999 |

---

## 🏗️ 系统架构

### 分层架构图

```mermaid
graph TB
    subgraph Client["🖥️ 客户端层"]
        A1["📱 网页端<br/>Vue 3 + Element Plus<br/>Port: 5173"]
        A2["🛡️ 管理后台<br/>Vue 3 + ECharts<br/>Port: 5174"]
        A3["📲 微信小程序<br/>uni-app"]
    end

    subgraph Gateway["🔗 API 网关层"]
        B1["RESTful API<br/>CORS 跨域处理"]
        B2["JWT 身份认证<br/>Token 有效期 24h"]
        B3["Spring Validation<br/>参数校验"]
    end

    subgraph Business["⚙️ 业务逻辑层"]
        C1["👤 用户服务"]
        C2["📱 商品服务"]
        C3["🛒 订单服务"]
        C4["💰 担保交易服务"]
        C5["⭐ 评价服务"]
        C6["📊 信用服务"]
        C7["🚚 物流服务"]
        C8["📍 地址服务"]
    end

    subgraph Data["🗄️ 数据访问层"]
        D1["MyBatis-Plus 3.5.5<br/>ORM 框架"]
        D2["UserMapper"]
        D3["ProductMapper"]
        D4["OrderMapper"]
        D5["TransactionMapper"]
    end

    subgraph Storage["💾 持久化层"]
        E1["MySQL 8.0<br/>xiantao 数据库"]
    end

    Client --> Gateway
    Gateway --> Business
    Business --> Data
    Data --> Storage
```

### 技术栈全景

```mermaid
flowchart TB
    subgraph Frontend[" 前端技术栈"]
        F1["Vue 3.4"] ~~~ F2["Vite 5.x"] ~~~ F3["Element Plus"] ~~~ F4["Pinia"] ~~~ F5["Axios"] ~~~ F6["ECharts 5.4"] ~~~ F7["uni-app"]
    end

    subgraph Backend[" 后端技术栈"]
        B1["Spring Boot 3.2"] ~~~ B2["Java 21"] ~~~ B3["MyBatis-Plus"] ~~~ B4["jjwt 0.12"] ~~~ B5["BCrypt"] ~~~ B6["Lombok"]
    end

    subgraph Storage["💾 存储"]
        S1["MySQL 8.0"]
    end

    Frontend ==>|HTTP/REST| Backend
    Backend ==>|JDBC| Storage
```

---

## 🔄 核心业务流程

### 担保交易流程图

```mermaid
sequenceDiagram
    autonumber
    participant Buyer as 🛒 买家
    participant Platform as 🏦 平台托管
    participant Seller as 📦 卖家
    participant System as 💾 数据库

    Buyer->>Seller: 浏览商品并下单
    Seller->>Platform: 确认订单
    Buyer->>Platform: 💰 付款 (资金进入托管)
    Platform->>System: 记录交易流水 (type=付款)
    Platform->>Seller: 📬 通知发货
    Seller->>Platform: 🚚 填写物流信息
    Platform->>Buyer: 📬 通知买家收货
    Buyer->>Platform: ✅ 确认收货
    Platform->>System: 记录交易流水 (type=解冻)
    Platform->>Seller: 💰 资金转入卖家账户
    Platform->>System: 更新订单状态 (已完成)
    Buyer->>Platform: ⭐ 评价卖家
    Seller->>Platform: ⭐ 评价买家
```

### 资金流向图

```mermaid
flowchart TD
    Start([买家余额]) --> Pay{买家付款}
    Pay -->|扣除余额| Escrow[平台托管账户<br/>资金锁定]
    Escrow --> Ship{卖家发货}
    Ship -->|物流状态更新| Transit[物流运输中]
    Transit --> Confirm{买家确认收货}
    Confirm -->|成功| Unfreeze[资金解冻]
    Confirm -->|取消| Refund[退款给买家]
    Unfreeze --> Seller[卖家收到货款<br/>余额增加]
    Refund --> Buyer([买家余额返还])
    Seller --> End([交易完成])
```

### 订单状态流转图

```mermaid
stateDiagram-v2
    [*] --> 待付款: 创建订单
    待付款 --> 已取消: 取消订单
    待付款 --> 已付款: 买家付款
    已付款 --> 已取消: 申请退款
    已付款 --> 已完成: 确认收货
    已完成 --> 评价: 买卖双方互评
    已取消 --> [*]
    评价 --> [*]

    note right of 待付款: 等待买家支付
    note right of 已付款: 资金托管中<br/>卖家可发货
    note right of 已完成: 资金已解冻<br/>交易成功
    note right of 已取消: 资金已退还<br/>交易关闭
```

---

## 📊 信用体系

### 信用分计算模型

```mermaid
flowchart LR
    Start[基础分 500] --> Actions{交易行为}

    Actions -->|成功交易 +10| Good
    Actions -->|取消交易 -5| Bad
    Actions -->|好评 +5| Good2
    Actions -->|中评 0| Neutral
    Actions -->|差评 -10| Bad2
    Actions -->|违规 -20| Bad3

    Good --> Calc[信用分计算<br/>300-850]
    Good2 --> Calc
    Bad --> Calc
    Neutral --> Calc
    Bad2 --> Calc
    Bad3 --> Calc

    Calc --> Level[信用等级]

    Level --> L1["800-850 🟡 信用极好"]
    Level --> L2["700-799 🟢 信用优秀"]
    Level --> L3["600-699 🔵 信用良好"]
    Level --> L4["500-599 🟠 信用一般"]
    Level --> L5["300-499 🔴 信用较差"]
```

---

## 🗄️ 数据库设计

### ER 关系图

```mermaid
erDiagram
    USER ||--o{ PRODUCT : "发布"
    USER ||--o{ ORDERS_buy : "购买"
    USER ||--o{ ORDERS_sell : "卖出"
    USER ||--o{ ADDRESS : "拥有"
    USER ||--|| USER_CREDIT : "拥有信用"
    USER ||--o{ USER_RATING : "评价"
    CATEGORY ||--o{ PRODUCT : "分类"
    PRODUCT ||--|| ORDERS : "被购买"
    ORDERS ||--o{ TRANSACTION_RECORD : "交易流水"
    ORDERS ||--o{ LOGISTICS : "物流信息"
    ORDERS ||--|| ADDRESS : "收货地址"
    ORDERS ||--o{ USER_RATING : "关联评价"
    LOGISTICS ||--o{ LOGISTICS_TRACK : "轨迹记录"

    USER {
        bigint id PK
        string username UK
        string password
        string phone UK
        string nickname
        decimal balance
        tinyint status
        datetime create_time
    }

    CATEGORY {
        bigint id PK
        string name
        string icon
        int sort
    }

    PRODUCT {
        bigint id PK
        string title
        decimal price
        bigint category_id FK
        bigint seller_id FK
        tinyint status
        int view_count
    }

    ORDERS {
        bigint id PK
        string order_no UK
        bigint product_id FK
        bigint buyer_id FK
        bigint seller_id FK
        tinyint status
        datetime pay_time
    }

    ADDRESS {
        bigint id PK
        bigint user_id FK
        string receiver_name
        string detail_address
    }

    USER_CREDIT {
        bigint id PK
        bigint user_id FK
        int credit_score
        int total_transactions
    }

    USER_RATING {
        bigint id PK
        bigint order_id FK
        bigint reviewer_id FK
        tinyint rating
        string content
    }

    LOGISTICS {
        bigint id PK
        bigint order_id FK
        string tracking_no
        tinyint status
    }

    TRANSACTION_RECORD {
        bigint id PK
        bigint order_id FK
        decimal amount
        tinyint transaction_type
        tinyint status
    }
```

### 数据流向图 (DFD)

```mermaid
flowchart TB
    subgraph External["👥 外部实体"]
        BUYER["🛒 买家"]
        SELLER["📦 卖家"]
        ADMIN["🛡️ 管理员"]
    end

    subgraph Process["⚙️ 处理过程"]
        P1["P1 用户认证<br/>JWT + BCrypt"]
        P2["P2 商品管理<br/>发布/编辑/下架"]
        P3["P3 订单处理<br/>创建/支付/发货"]
        P4["P4 物流追踪<br/>轨迹查询"]
        P5["P5 信用评价<br/>评分计算"]
        P6["P6 担保交易<br/>资金托管"]
    end

    subgraph DataStore["💾 数据存储"]
        D1[("D1 用户表<br/>sys_user")]
        D2[("D2 商品表<br/>product")]
        D3[("D3 订单表<br/>orders")]
        D4[("D4 物流表<br/>logistics")]
        D5[("D5 信用表<br/>user_credit")]
        D6[("D6 交易流水<br/>transaction_record")]
    end

    BUYER --> P1
    SELLER --> P1
    ADMIN --> P1

    SELLER --> P2
    BUYER --> P3
    BUYER --> P5

    P1 --> D1
    P2 --> D2
    P3 --> D3
    P4 --> D4
    P5 --> D5
    P6 --> D6

    P3 --> P6
    P3 --> P4
    P3 --> P5
```

---

## 📦 功能模块

### 模块总览

```mermaid
mindmap
  root((闲淘平台))
    用户模块
      注册登录
      个人信息
      信用体系
    商品模块
      发布商品
      浏览搜索
      编辑删除
    订单模块
      创建订单
      支付管理
      发货确认
    担保交易
      资金托管
      解冻退款
      交易流水
    评价系统
      买卖互评
      信用评分
    物流追踪
      物流下单
      轨迹查询
    管理后台
      用户管理
      商品审核
      数据统计
```

| 模块 | 功能 | Controller | Service | 状态 |
|------|------|-----------|---------|------|
| 👤 用户 | 注册、登录、信息管理 | `AuthController`<br/>`UserController` | `UserService` | ✅ |
| 📱 商品 | 发布、浏览、搜索、编辑 | `ProductController` | `ProductService` | ✅ |
| 📂 分类 | 分类管理、筛选 | `CategoryController` | `CategoryService` | ✅ |
| 🛒 订单 | 创建、支付、发货、确认 | `OrderController` | `OrderService` | ✅ |
| 💰 担保交易 | 资金托管、解冻、退款 | `TransactionRecordController` | `TransactionRecordService` | ✅ |
| ⭐ 评价 | 买卖互评、信用评分 | `UserRatingController` | `UserRatingService` | ✅ |
| 📍 地址 | 收货地址管理 | `AddressController` | `AddressService` | ✅ |
| 🚚 物流 | 物流下单、轨迹查询 | `LogisticsController` | `LogisticsService` | ✅ |
| 🛡️ 管理 | 用户/商品/订单/数据统计 | `Admin*Controller` (4个) | 各Service | ✅ |

---

## 📁 项目结构

```
xiantao/
├── xiantao-server/                  # 后端服务 (Spring Boot 3.2.3)
│   ├── src/main/java/com/xiantao/
│   │   ├── common/                  # 公共组件：异常、响应、全局处理
│   │   ├── config/                  # 配置：跨域、JWT 拦截器、Web 配置
│   │   ├── controller/              # 控制层 (16 个 Controller)
│   │   ├── service/                 # 业务层 (10 个 Service)
│   │   │   └── impl/                # 服务实现类
│   │   ├── mapper/                  # 数据访问层 (10 个 Mapper)
│   │   ├── entity/                  # 数据库实体类 (10 个)
│   │   ├── dto/                     # 请求参数对象 (10 个)
│   │   ├── vo/                      # 响应视图对象 (11 个)
│   │   ├── utils/                   # 工具类 (JWT、密码生成)
│   │   └── XiantaoApplication.java  # 启动入口
│   ├── src/main/resources/
│   │   └── application.yml          # 应用配置
│   ├── sql/                         # 数据库初始化脚本
│   └── pom.xml                      # Maven 依赖配置
│
├── xiantao-web/                     # 网页端 (Vue 3 + Element Plus)
│   ├── src/
│   │   ├── api/                     # API 请求封装 (9 个模块)
│   │   ├── router/                  # 路由配置
│   │   ├── stores/                  # Pinia 状态管理
│   │   ├── views/                   # 页面组件 (14 个页面)
│   │   └── utils/                   # Axios 拦截器
│   └── vite.config.js
│
├── xiantao-admin/                   # 管理后台 (Vue 3 + ECharts)
│   ├── src/
│   │   ├── router/                  # 路由配置
│   │   ├── utils/                   # 工具类
│   │   └── views/                   # 管理页面 (9 个页面)
│   └── vite.config.js
│
├── xiantao-miniprogram/             # 微信小程序端 (uni-app)
│   ├── api/                         # API 封装
│   ├── pages/                       # 页面
│   └── src/                         # uni-app 源码
│
├── README.md                        # 技术文档 (本文件)
├── 二手交易平台-产品设计书.md        # 产品设计文档
├── 二手交易平台-实施计划书.md        # 项目实施计划
└── 闲淘平台-API接口文档.md           # API 接口文档
```

---

## 🔌 API 接口概览

### 接口全景图

```mermaid
graph LR
    subgraph Public["🌐 公开接口"]
        P1["POST /api/auth/login<br/>用户登录"]
        P2["POST /api/auth/register<br/>用户注册"]
        P3["GET /api/category/list<br/>分类列表"]
        P4["GET /api/product/list<br/>商品列表"]
        P5["GET /api/product/{id}<br/>商品详情"]
    end

    subgraph Auth["🔐 需要认证"]
        A1["GET/PUT /api/user/*<br/>用户管理"]
        A2["CRUD /api/product/*<br/>商品操作"]
        A3["CRUD /api/order/*<br/>订单操作"]
        A4["CRUD /api/address/*<br/>地址管理"]
        A5["CRUD /api/transaction/*<br/>担保交易"]
        A6["CRUD /api/rating/*<br/>评价管理"]
        A7["CRUD /api/logistics/*<br/>物流管理"]
    end

    subgraph Admin["🛡️ 需要管理员权限"]
        M1["CRUD /api/admin/user/*<br/>用户管理"]
        M2["CRUD /api/admin/product/*<br/>商品审核"]
        M3["CRUD /api/admin/order/*<br/>订单管理"]
        M4["GET /api/admin/category/*<br/>分类管理"]
    end

    Public -.->|无需 Token| Gateway
    Auth -.->|Bearer Token| Gateway
    Admin -.->|Bearer Token + admin角色| Gateway
```

### 接口列表

| 模块 | 基础路径 | 接口数 | 认证 |
|------|---------|--------|------|
| 认证模块 | `/api/auth` | 2 | 公开 |
| 用户模块 | `/api/user` | 3 | JWT |
| 分类模块 | `/api/category` | 1 | 公开 |
| 商品模块 | `/api/product` | 8 | JWT |
| 订单模块 | `/api/order` | 7 | JWT |
| 地址模块 | `/api/address` | 6 | JWT |
| 担保交易 | `/api/transaction` | 5 | JWT |
| 评价模块 | `/api/rating` | 4 | JWT |
| 物流模块 | `/api/logistics` | 4 | JWT |
| 管理模块 | `/api/admin` | 13 | JWT (admin) |

### 统一响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

### 核心接口示例

**用户登录：**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "test001", "password": "123456"}'
```

**发布商品：**
```bash
curl -X POST http://localhost:8080/api/product \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"title": "iPhone 14 Pro", "price": 5999.00, "categoryId": 1}'
```

---

## 🛡️ 安全机制

### 安全架构图

```mermaid
flowchart TB
    subgraph Auth["认证层"]
        A1["BCrypt 密码加密<br/>不可逆哈希"]
        A2["JWT Token<br/>24h 有效期"]
        A3["Bearer Token<br/>请求头传递"]
    end

    subgraph Filter["过滤层"]
        F1["JwtInterceptor<br/>身份验证"]
        F2["CorsConfig<br/>跨域控制"]
        F3["Spring Validation<br/>参数校验"]
    end

    subgraph Protect["防护层"]
        P1["角色权限控制<br/>admin 角色隔离"]
        P2["文件上传限制<br/>单文件 10MB"]
        P3["敏感信息过滤<br/>密码/TOKEN 不入日志"]
    end

    Request([HTTP 请求]) --> F1
    F1 --> A2
    A2 --> F3
    F3 --> P1
    P1 --> Server[业务逻辑]
    A1 --> P3
    F2 --> Server
```

| 机制 | 实现方式 | 说明 |
|------|---------|------|
| 密码加密 | BCrypt | 不可逆哈希，存储安全 |
| 身份认证 | JWT Token | 24 小时有效期 |
| 角色控制 | 自定义拦截器 | admin 角色隔离 |
| 参数校验 | Spring Validation | DTO 层级校验 |
| 跨域处理 | CorsConfig | 允许前端跨域访问 |
| 文件上传 | Multipart 限制 | 单文件 10MB，总请求 50MB |

---

## 🚢 部署指南

### 一键启动 (Docker Compose)

```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: 123456
      MYSQL_DATABASE: xiantao
    volumes:
      - ./sql/init.sql:/docker-entrypoint-initdb.d/init.sql
    ports:
      - "3306:3306"

  backend:
    build: ./xiantao-server
    depends_on:
      - mysql
    ports:
      - "8080:8080"

  frontend:
    image: node:18-alpine
    working_dir: /app
    volumes:
      - ./xiantao-web:/app
    command: npm run dev
    ports:
      - "5173:5173"
```

```bash
docker-compose up -d
```

---

## 📚 文档资源

| 文档 | 说明 |
|------|------|
| [产品设计书](二手交易平台-产品设计书.md) | 完整产品需求与设计文档 |
| [实施计划书](二手交易平台-实施计划书.md) | 项目实施计划与里程碑 |
| [API 接口文档](闲淘平台-API接口文档.md) | 详细 RESTful API 文档 |
| [数据库脚本](xiantao-server/sql/init.sql) | 数据库初始化 SQL |

---

## ❓ 常见问题

**Q: 启动后端报错 "Cannot connect to database"**
> 检查 MySQL 是否启动，`application.yml` 配置是否正确，数据库 `xiantao` 是否已创建

**Q: 前端请求跨域报错**
> 确认后端已启动，项目已配置 `CorsConfig.java`

**Q: Token 过期如何处理**
> 前端响应拦截器捕获 401，清除本地 Token 并跳转登录页

---

<p align="center">
  <strong>如果这个项目对您有帮助，请给我们一个 ⭐</strong>
</p>
