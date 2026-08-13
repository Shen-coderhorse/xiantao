# 🛒 闲淘 XianTao — 架构与流程图集（Mermaid）

> 本文件仅收录项目的 Mermaid 图表，作为 [README.md](README.md) 的可视化配套。
> 项目说明、快速开始、测试账号、接口列表、部署步骤等文字内容以 README.md 为唯一真相源，本文件不重复维护。
> 图表可在 GitHub / VS Code / Trae IDE 直接渲染。

> 共 11 张图。

---
## 分层架构图

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

---

## 技术栈全景

```mermaid
flowchart TB
    subgraph Frontend[" 前端技术栈"]
        F1["Vue 3.4"] ~~~ F2["Vite 5.x"] ~~~ F3["Element Plus"] ~~~ F4["Pinia"] ~~~ F5["Axios"] ~~~ F6["ECharts 5.4"] ~~~ F7["uni-app"]
    end

    subgraph Backend[" 后端技术栈"]
        B1["Spring Boot 3.2"] ~~~ B2["Java 21"] ~~~ B3["MyBatis-Plus"] ~~~ B4["jjwt 0.12"] ~~~ B5["BCrypt"] ~~~ B6["Lombok"] ~~~ B7["Flyway 9.22"]
    end

    subgraph Storage["💾 存储"]
        S1["MySQL 8.0"]
    end

    Frontend ==>|HTTP/REST| Backend
    Backend ==>|JDBC| Storage
```

---

## 担保交易流程图

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

---

## 资金流向图

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

---

## 订单状态流转图

```mermaid
stateDiagram-v2
    [*] --> 待付款: 创建订单
    待付款 --> 已取消: 取消订单
    待付款 --> 待发货: 买家付款
    待发货 --> 已取消: 取消退款
    待发货 --> 已完成: 确认收货
    已完成 --> 评价: 买卖双方互评
    已取消 --> [*]
    评价 --> [*]

    note right of 待付款: 等待买家支付
    note right of 待发货: 资金托管中<br/>卖家可发货
    note right of 已完成: 资金已解冻<br/>交易成功
    note right of 已取消: 资金已退还<br/>交易关闭
```

---

## 信用分计算模型

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

## ER 关系图

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

---

## 数据流向图 (DFD)

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

## 模块总览

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

---

## 接口全景图

```mermaid
graph LR
    subgraph Public["🌐 公开接口"]
        P1["POST /api/auth/login<br/>用户登录"]
        P2["POST /api/auth/register<br/>用户注册"]
        P3["GET /api/category/list<br/>分类列表"]
        P4["GET /api/product/list<br/>商品列表"]
        P5["GET /api/product/search<br/>商品搜索"]
        P6["GET /api/product/{id}<br/>商品详情"]
        P7["POST /api/upload/images<br/>图片上传"]
    end

    subgraph Auth["🔐 需要认证"]
        A1["GET/PUT /api/user/*<br/>用户信息<br/>POST /api/user/recharge 充值"]
        A2["CRUD /api/product/*<br/>商品操作"]
        A3["CRUD /api/order/*<br/>订单操作"]
        A4["CRUD /api/address/*<br/>地址管理"]
        A5["CRUD /api/transaction/*<br/>担保交易"]
        A6["CRUD /api/rating/*<br/>评价管理"]
        A7["CRUD /api/logistics/*<br/>物流管理"]
    end

    subgraph Admin["🛡️ 需要管理员权限"]
        M1["GET /api/admin/dashboard/*<br/>数据统计"]
        M2["CRUD /api/admin/user/*<br/>用户管理"]
        M3["CRUD /api/admin/product/*<br/>商品审核"]
        M4["CRUD /api/admin/order/*<br/>订单管理"]
        M5["CRUD /api/admin/category/*<br/>分类管理"]
        M6["GET /api/admin/credit/*<br/>信用管理"]
        M7["GET /api/admin/transaction/*<br/>交易流水"]
    end

    Public -.->|无需 Token| Gateway
    Auth -.->|Bearer Token| Gateway
    Admin -.->|Bearer Token + admin角色| Gateway
```

---

## 安全架构图

```mermaid
flowchart TB
    subgraph Auth["认证层"]
        A1["BCrypt 密码加密<br/>不可逆哈希"]
        A2["JWT Token<br/>24h 有效期"]
        A3["Bearer Token<br/>请求头传递"]
    end

    subgraph Filter["过滤层"]
        F1["JwtInterceptor<br/>身份验证"]
        F2["AdminInterceptor<br/>管理员验证"]
        F3["CorsConfig<br/>跨域控制"]
        F4["Spring Validation<br/>参数校验"]
    end

    subgraph Protect["防护层"]
        P1["角色权限控制<br/>admin 角色隔离"]
        P2["文件上传校验<br/>类型白名单+5MB限制"]
        P3["敏感信息过滤<br/>密码/TOKEN 不入日志"]
    end

    Request([HTTP 请求]) --> F1
    F1 --> F2
    F2 --> A2
    A2 --> F4
    F4 --> P1
    P1 --> Server[业务逻辑]
    A1 --> P3
    F3 --> Server
```

