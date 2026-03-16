# 闲淘二手交易平台

## 项目简介

闲淘是一个基于 Spring Boot + Vue 3 的二手交易平台，提供用户注册登录、商品发布浏览、订单管理等核心功能。系统采用前后端分离架构，支持图片上传、商品搜索、分类筛选等功能，同时提供管理员后台进行商品管理。

## 主要功能

- **用户模块**: 注册、登录、个人信息管理、JWT Token认证
- **商品模块**: 商品发布、编辑、删除、浏览、搜索、分类筛选
- **订单模块**: 创建订单、支付、确认收货、取消订单
- **管理模块**: 管理员商品管理（增删改查、上下架）
- **文件上传**: 支持本地图片上传，最多5张，单张不超过10MB

## 技术栈

### 后端技术
| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 21 | JDK版本 |
| Spring Boot | 3.2.3 | 基础框架 |
| MyBatis Plus | 3.5.5 | ORM框架 |
| MySQL | 8.0 | 数据库 |
| JWT | 0.12.3 | Token认证 |
| Lombok | - | 简化代码 |

### 前端技术
| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.4.21 | 前端框架 |
| Vue Router | 4.3.0 | 路由管理 |
| Pinia | 2.1.7 | 状态管理 |
| Element Plus | 2.5.6 | UI组件库 |
| Axios | 1.6.7 | HTTP请求 |
| Vite | 5.x | 构建工具 |

## 快速开始

### 环境要求

- JDK 21+
- Node.js 18+
- MySQL 8.0+
- Maven 3.8+

### 安装步骤

#### 1. 创建数据库

```sql
CREATE DATABASE xiantao CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### 2. 初始化数据库

```bash
mysql -uroot -p123456 --default-character-set=utf8mb4 xiantao < e:\xiantao\xiantao-server\sql\init.sql
```

#### 3. 创建上传目录

```bash
mkdir E:\xiantao\uploads
```

#### 4. 启动后端服务

```bash
cd e:\xiantao\xiantao-server
mvn spring-boot:run
```

#### 5. 安装前端依赖并启动

```bash
cd e:\xiantao\xiantao-web
npm install
npm run dev
```

#### 6. 访问应用

- 前端地址: http://localhost:5173
- 后端地址: http://localhost:8080

## 项目启动命令

### 后端启动

```bash
cd e:\xiantao\xiantao-server
mvn spring-boot:run
```

### 前端启动

```bash
cd e:\xiantao\xiantao-web
npm run dev
```

## 目录结构

```
e:\xiantao\
├── xiantao-server/                    # 后端项目
│   ├── src/main/java/com/xiantao/
│   │   ├── config/                    # 配置类
│   │   │   ├── CorsConfig.java        # 跨域配置
│   │   │   ├── JwtInterceptor.java    # JWT拦截器
│   │   │   ├── ResourceConfig.java    # 静态资源配置
│   │   │   └── WebConfig.java         # Web配置
│   │   ├── controller/                # 控制器
│   │   │   ├── AuthController.java    # 认证控制器
│   │   │   ├── CategoryController.java# 分类控制器
│   │   │   ├── ProductController.java # 商品控制器
│   │   │   ├── OrderController.java   # 订单控制器
│   │   │   ├── UploadController.java  # 上传控制器
│   │   │   └── AdminProductController.java # 管理员商品控制器
│   │   ├── service/                   # 服务层
│   │   ├── mapper/                    # MyBatis Mapper
│   │   ├── entity/                    # 实体类
│   │   ├── dto/                       # 数据传输对象
│   │   ├── vo/                        # 视图对象
│   │   ├── common/                    # 公共类
│   │   └── utils/                     # 工具类
│   ├── src/main/resources/
│   │   └── application.yml            # 配置文件
│   └── sql/                           # SQL脚本
│       └── init.sql                   # 初始化脚本
│
├── xiantao-web/                       # 前端项目
│   ├── src/
│   │   ├── api/                       # API接口
│   │   │   ├── user.js                # 用户接口
│   │   │   ├── product.js             # 商品接口
│   │   │   ├── category.js            # 分类接口
│   │   │   ├── order.js               # 订单接口
│   │   │   └── admin.js               # 管理员接口
│   │   ├── views/                     # 页面组件
│   │   │   ├── Home.vue               # 首页
│   │   │   ├── Login.vue              # 登录页
│   │   │   ├── Register.vue           # 注册页
│   │   │   ├── Publish.vue            # 发布商品页
│   │   │   ├── ProductDetail.vue      # 商品详情页
│   │   │   ├── MyProducts.vue         # 我的商品页
│   │   │   ├── Orders.vue             # 订单页
│   │   │   ├── UserCenter.vue         # 个人中心
│   │   │   ├── AdminProducts.vue      # 管理员商品管理
│   │   │   └── Layout.vue             # 布局组件
│   │   ├── stores/                    # Pinia状态管理
│   │   │   └── user.js                # 用户状态
│   │   ├── router/                    # 路由配置
│   │   │   └── index.js
│   │   ├── utils/                     # 工具函数
│   │   │   └── request.js             # Axios封装
│   │   └── assets/                    # 静态资源
│   ├── vite.config.js                 # Vite配置
│   └── package.json                   # 依赖配置
│
├── uploads/                           # 上传文件目录
├── 二手交易平台-产品设计书.md
├── 二手交易平台-实施计划书.md
└── README.md                          # 本文档
```

## 测试账号

| 账号 | 密码 | 角色 |
|------|------|------|
| admin | 123456 | 管理员 |
| test001 | 123456 | 普通用户 |
| test002 | 123456 | 普通用户 |
| test003 | 123456 | 普通用户 |

## API接口详解

### 认证接口

#### 用户注册

```
POST /api/auth/register
Content-Type: application/json

Request:
{
  "username": "testuser",
  "password": "123456",
  "phone": "13800138000"
}

Response:
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "testuser",
    "nickname": "testuser",
    "phone": "13800138000",
    "role": "user",
    "token": "eyJhbGciOiJIUzI1NiJ9..."
  }
}
```

#### 用户登录

```
POST /api/auth/login
Content-Type: application/json

Request:
{
  "username": "testuser",
  "password": "123456"
}

Response:
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "testuser",
    "nickname": "testuser",
    "role": "user",
    "token": "eyJhbGciOiJIUzI1NiJ9..."
  }
}
```

### 用户接口

#### 获取用户信息

```
GET /api/user/info
Authorization: Bearer <token>

Response:
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "testuser",
    "nickname": "testuser",
    "phone": "13800138000",
    "role": "user"
  }
}
```

#### 更新用户信息

```
PUT /api/user/info
Authorization: Bearer <token>
Content-Type: application/json

Request:
{
  "nickname": "新昵称",
  "phone": "13900139000"
}

Response:
{
  "code": 200,
  "message": "修改成功",
  "data": {...}
}
```

### 商品接口

#### 获取商品列表

```
GET /api/product/list?pageNum=1&pageSize=10&keyword=手机&categoryId=1

Response:
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "title": "iPhone 14 Pro",
        "price": 5999.00,
        "categoryId": 1,
        "categoryName": "数码产品",
        "imageList": ["/uploads/2024/03/16/xxx.jpg"],
        "sellerName": "小明",
        "viewCount": 100,
        "status": 1
      }
    ],
    "total": 100,
    "current": 1,
    "size": 10
  }
}
```

#### 获取商品详情

```
GET /api/product/1

Response:
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "title": "iPhone 14 Pro",
    "price": 5999.00,
    "originalPrice": 7999.00,
    "categoryId": 1,
    "categoryName": "数码产品",
    "description": "99新，无划痕",
    "imageList": ["/uploads/2024/03/16/xxx.jpg"],
    "sellerId": 1,
    "sellerName": "小明",
    "viewCount": 100,
    "status": 1
  }
}
```

#### 发布商品

```
POST /api/product
Authorization: Bearer <token>
Content-Type: application/json

Request:
{
  "title": "iPhone 14 Pro",
  "categoryId": 1,
  "price": 5999.00,
  "originalPrice": 7999.00,
  "description": "99新，无划痕",
  "images": "/uploads/2024/03/16/xxx.jpg"
}

Response:
{
  "code": 200,
  "message": "发布成功",
  "data": {...}
}
```

#### 更新商品

```
PUT /api/product/1
Authorization: Bearer <token>
Content-Type: application/json

Request:
{
  "title": "iPhone 14 Pro",
  "categoryId": 1,
  "price": 5999.00,
  "description": "降价出售"
}

Response:
{
  "code": 200,
  "message": "修改成功",
  "data": {...}
}
```

#### 删除商品

```
DELETE /api/product/1
Authorization: Bearer <token>

Response:
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

#### 获取我的商品

```
GET /api/product/my?pageNum=1&pageSize=10
Authorization: Bearer <token>

Response:
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [...],
    "total": 10
  }
}
```

### 分类接口

#### 获取分类列表

```
GET /api/category/list

Response:
{
  "code": 200,
  "message": "success",
  "data": [
    { "id": 1, "name": "数码产品" },
    { "id": 2, "name": "图书教材" },
    { "id": 3, "name": "生活用品" },
    { "id": 4, "name": "服装鞋帽" },
    { "id": 5, "name": "运动户外" },
    { "id": 6, "name": "其他" }
  ]
}
```

### 订单接口

#### 创建订单

```
POST /api/order
Authorization: Bearer <token>
Content-Type: application/json

Request:
{
  "productId": 1
}

Response:
{
  "code": 200,
  "message": "下单成功",
  "data": {
    "id": 1,
    "orderNo": "20240316123456789",
    "productId": 1,
    "productTitle": "iPhone 14 Pro",
    "productPrice": 5999.00,
    "status": 0,
    "statusText": "待付款"
  }
}
```

#### 获取订单列表

```
GET /api/order/list?type=1&pageNum=1&pageSize=10
Authorization: Bearer <token>

参数说明:
- type: 0-全部, 1-我买的, 2-我卖的

Response:
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [...],
    "total": 10
  }
}
```

#### 支付订单

```
PUT /api/order/1/pay
Authorization: Bearer <token>

Response:
{
  "code": 200,
  "message": "支付成功",
  "data": null
}
```

#### 确认收货

```
PUT /api/order/1/complete
Authorization: Bearer <token>

Response:
{
  "code": 200,
  "message": "订单完成",
  "data": null
}
```

#### 取消订单

```
PUT /api/order/1/cancel
Authorization: Bearer <token>

Response:
{
  "code": 200,
  "message": "订单已取消",
  "data": null
}
```

### 文件上传接口

#### 上传图片

```
POST /api/upload/images
Authorization: Bearer <token>
Content-Type: multipart/form-data

Request:
files: <binary data>

Response:
{
  "code": 200,
  "message": "上传成功",
  "data": ["/uploads/2024/03/16/xxx.jpg"]
}
```

### 管理员接口

#### 获取商品列表（管理员）

```
GET /api/admin/product/list?pageNum=1&pageSize=10&categoryId=1&status=1&keyword=手机
Authorization: Bearer <token> (需要管理员权限)

Response:
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [...],
    "total": 100
  }
}
```

#### 新增商品（管理员）

```
POST /api/admin/product
Authorization: Bearer <token> (需要管理员权限)
Content-Type: application/json

Request:
{
  "title": "商品名称",
  "categoryId": 1,
  "price": 100.00,
  "status": 1
}

Response:
{
  "code": 200,
  "message": "创建成功",
  "data": {...}
}
```

#### 更新商品（管理员）

```
PUT /api/admin/product/1
Authorization: Bearer <token> (需要管理员权限)
Content-Type: application/json

Request:
{
  "title": "新商品名称",
  "price": 200.00
}

Response:
{
  "code": 200,
  "message": "修改成功",
  "data": {...}
}
```

#### 删除商品（管理员）

```
DELETE /api/admin/product/1
Authorization: Bearer <token> (需要管理员权限)

Response:
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

#### 更新商品状态（管理员）

```
PUT /api/admin/product/1/status?status=0
Authorization: Bearer <token> (需要管理员权限)

参数说明:
- status: 0-下架, 1-在售

Response:
{
  "code": 200,
  "message": "状态更新成功",
  "data": null
}
```

## 使用示例

### 前端调用示例

```javascript
// 登录
import { login } from '@/api/user'

const handleLogin = async () => {
  const res = await login({ username: 'test001', password: '123456' })
  localStorage.setItem('token', res.data.token)
  localStorage.setItem('user', JSON.stringify(res.data))
}

// 获取商品列表
import { getProductList } from '@/api/product'

const loadProducts = async () => {
  const res = await getProductList({ pageNum: 1, pageSize: 10, keyword: '手机' })
  console.log(res.data.records)
}

// 发布商品
import { createProduct } from '@/api/product'

const publish = async () => {
  await createProduct({
    title: 'iPhone 14 Pro',
    categoryId: 1,
    price: 5999,
    description: '99新'
  })
}

// 上传图片
const handleUpload = async (file) => {
  const formData = new FormData()
  formData.append('files', file)
  const res = await axios.post('/api/upload/images', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
  return res.data.data[0] // 返回图片URL
}
```

## 常见问题

### Q: 登录后提示"用户名或密码错误"？

A: 
1. 检查数据库中密码哈希值是否完整（BCrypt应为60字符）
2. 使用SQL文件方式重新设置密码，避免PowerShell转义问题

### Q: 图片上传后无法显示？

A: 
1. 检查 `application.yml` 中 `upload.path` 是否为绝对路径
2. 确认上传目录 `E:\xiantao\uploads` 存在且有写入权限
3. 检查前端 `el-upload` 组件的 `name` 属性是否为 `files`

### Q: 管理员无法登录？

A: 
1. 确认数据库中存在 `admin` 用户且 `role` 字段为 `admin`
2. 密码哈希值需使用正确的BCrypt格式

### Q: 发布商品后首页看不到？

A: 
1. 检查商品 `status` 字段是否为 1（在售）
2. 确认 `images` 字段存储的是服务器返回的真实URL，而非 `blob:` URL

### Q: 图片显示不完整？

A: 
使用 `object-fit: contain` 样式确保图片完整显示：
```css
.product-image img {
  object-fit: contain;
}
```

## 技术支持

如有问题，请查看：
- 后端日志：检查控制台输出
- 前端控制台：检查Network面板请求响应
- 数据库：检查数据是否正确存储
