# 闲淘二手交易平台 - API接口文档

## 基础信息

| 项目 | 说明 |
|------|------|
| 基础URL | http://localhost:8080/api |
| 认证方式 | JWT Token (Bearer) |
| 请求格式 | application/json |
| 响应格式 | application/json |

## 统一响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

---

## 1. 认证模块 (/api/auth)

### 1.1 用户注册
- **URL**: `POST /api/auth/register`
- **描述**: 用户注册新账号
- **请求体**:
```json
{
  "username": "test001",
  "password": "123456",
  "phone": "13800138001"
}
```
- **响应**:
```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "id": 1,
    "username": "test001",
    "nickname": "test001",
    "token": "eyJhbGci..."
  }
}
```

### 1.2 用户登录
- **URL**: `POST /api/auth/login`
- **描述**: 用户登录获取Token
- **请求体**:
```json
{
  "username": "test001",
  "password": "123456"
}
```
- **响应**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "id": 1,
    "username": "test001",
    "nickname": "小明同学",
    "balance": 10000.00,
    "role": "user",
    "token": "eyJhbGci..."
  }
}
```

---

## 2. 用户模块 (/api/user)

### 2.1 获取用户信息
- **URL**: `GET /api/user/info`
- **描述**: 获取当前登录用户信息
- **认证**: 需要
- **响应**:
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "username": "test001",
    "nickname": "小明同学",
    "phone": "13800138001",
    "balance": 10000.00,
    "role": "user"
  }
}
```

### 2.2 修改用户信息
- **URL**: `PUT /api/user/info`
- **描述**: 修改当前用户信息
- **认证**: 需要
- **请求体**:
```json
{
  "nickname": "新昵称",
  "phone": "13900139000"
}
```

---

## 3. 分类模块 (/api/category)

### 3.1 获取分类列表
- **URL**: `GET /api/category/list`
- **描述**: 获取所有商品分类
- **响应**:
```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "name": "数码产品",
      "icon": "📱",
      "sort": 1,
      "status": 1
    }
  ]
}
```

---

## 4. 商品模块 (/api/product)

### 4.1 获取商品列表
- **URL**: `GET /api/product/list`
- **参数**:
  - `pageNum`: 页码 (默认1)
  - `pageSize`: 每页数量 (默认10)
  - `categoryId`: 分类ID (可选)
  - `keyword`: 搜索关键词 (可选)
  - `minPrice`: 最低价格 (可选)
  - `maxPrice`: 最高价格 (可选)
  - `sortBy`: 排序字段 (price_asc, price_desc, time_asc)
- **响应**:
```json
{
  "code": 200,
  "data": {
    "list": [...],
    "total": 100,
    "pageNum": 1,
    "pageSize": 10
  }
}
```

### 4.2 商品搜索
- **URL**: `GET /api/product/search`
- **参数**: 同商品列表
- **说明**: 多条件搜索接口，与列表接口参数相同

### 4.3 获取附近商品
- **URL**: `GET /api/product/nearby`
- **参数**:
  - `latitude`: 纬度
  - `longitude`: 经度
  - `distance`: 距离范围(km)

### 4.4 获取商品详情
- **URL**: `GET /api/product/{id}`
- **响应**:
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "title": "iPhone 14 Pro",
    "description": "99新",
    "price": 5999.00,
    "originalPrice": 8999.00,
    "categoryId": 1,
    "sellerId": 1,
    "sellerName": "小明同学",
    "images": "http://...",
    "status": 1,
    "viewCount": 128,
    "createTime": "2024-..."
  }
}
```

### 4.5 发布商品
- **URL**: `POST /api/product`
- **认证**: 需要
- **请求体**:
```json
{
  "title": "商品标题",
  "description": "商品描述",
  "price": 99.99,
  "originalPrice": 199.99,
  "categoryId": 1,
  "images": "url1,url2"
}
```

### 4.6 修改商品
- **URL**: `PUT /api/product/{id}`
- **认证**: 需要 (只能修改自己的商品)

### 4.7 删除商品
- **URL**: `DELETE /api/product/{id}`
- **认证**: 需要

### 4.8 获取我的商品
- **URL**: `GET /api/product/my`
- **认证**: 需要
- **参数**: pageNum, pageSize

---

## 5. 订单模块 (/api/order)

### 5.1 创建订单
- **URL**: `POST /api/order`
- **认证**: 需要
- **请求体**:
```json
{
  "productId": 1,
  "addressId": 1
}
```
- **响应**:
```json
{
  "code": 200,
  "message": "下单成功",
  "data": {
    "id": 1,
    "orderNo": "202403160001001",
    "productId": 1,
    "productTitle": "iPhone 14 Pro",
    "productPrice": 5999.00,
    "sellerId": 1,
    "buyerId": 2,
    "addressId": 1,
    "status": 0,
    "statusText": "待付款",
    "createTime": "..."
  }
}
```

### 5.2 获取订单列表
- **URL**: `GET /api/order/list`
- **认证**: 需要
- **参数**:
  - `pageNum`: 页码
  - `pageSize`: 每页数量
  - `status`: 订单状态 (0待付款 1待发货 2已完成 3已取消)
  - `type`: 1=买入 2=卖出

### 5.3 获取订单详情
- **URL**: `GET /api/order/{id}`
- **认证**: 需要

### 5.4 支付订单
- **URL**: `PUT /api/order/{id}/pay`
- **认证**: 需要 (买家操作)
- **说明**: 更新订单状态为已付款

### 5.5 确认收货
- **URL**: `PUT /api/order/{id}/complete` 或 `PUT /api/order/{id}/receive`
- **认证**: 需要 (买家操作)

### 5.6 取消订单
- **URL**: `PUT /api/order/{id}/cancel`
- **认证**: 需要

### 5.7 发货
- **URL**: `PUT /api/order/{id}/ship`
- **认证**: 需要 (卖家操作)
- **请求体**:
```json
{
  "companyCode": "SF",
  "companyName": "顺丰速运",
  "trackingNo": "SF1234567890"
}
```

---

## 6. 地址模块 (/api/address)

### 6.1 获取地址列表
- **URL**: `GET /api/address/list`
- **认证**: 需要

### 6.2 新增地址
- **URL**: `POST /api/address`
- **认证**: 需要
- **请求体**:
```json
{
  "receiverName": "小明",
  "receiverPhone": "13800138001",
  "province": "北京市",
  "city": "北京市",
  "district": "海淀区",
  "detailAddress": "中关村大街1号",
  "latitude": 39.986789,
  "longitude": 116.305276,
  "isDefault": 1
}
```

### 6.3 修改地址
- **URL**: `PUT /api/address/{id}`
- **认证**: 需要

### 6.4 删除地址
- **URL**: `DELETE /api/address/{id}`
- **认证**: 需要

### 6.5 设置默认地址
- **URL**: `PUT /api/address/{id}/default`
- **认证**: 需要

### 6.6 获取默认地址
- **URL**: `GET /api/address/default`
- **认证**: 需要

---

## 7. 担保交易模块 (/api/transaction)

### 7.1 创建支付
- **URL**: `POST /api/transaction/pay/{orderId}`
- **认证**: 需要 (买家操作)
- **说明**: 买家付款，资金进入平台托管账户

### 7.2 解冻资金
- **URL**: `POST /api/transaction/release/{orderId}`
- **认证**: 需要 (买家操作)
- **说明**: 买家确认收货，资金转入卖家账户

### 7.3 退款
- **URL**: `POST /api/transaction/refund/{orderId}`
- **认证**: 需要
- **说明**: 订单取消，资金退还买家

### 7.4 获取我的交易流水
- **URL**: `GET /api/transaction/my`
- **认证**: 需要
- **响应**:
```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "orderId": 1,
      "orderNo": "202403160001001",
      "transactionType": 1,
      "transactionTypeText": "付款",
      "amount": 5999.00,
      "fromUserId": 2,
      "fromUserName": "小红同学",
      "toUserId": 0,
      "toUserName": "平台托管",
      "status": 1,
      "statusText": "成功",
      "remark": "买家付款，资金进入平台托管",
      "createTime": "..."
    }
  ]
}
```

### 7.5 获取订单交易流水
- **URL**: `GET /api/transaction/order/{orderId}`
- **认证**: 需要

---

## 8. 评价模块 (/api/rating)

### 8.1 创建评价
- **URL**: `POST /api/rating`
- **认证**: 需要
- **请求体**:
```json
{
  "orderId": 1,
  "revieweeId": 1,
  "rating": 3,
  "content": "商品很好，和描述一致！"
}
```
- **说明**: rating: 1=差评, 2=中评, 3=好评

### 8.2 获取我的评价
- **URL**: `GET /api/rating/my`
- **认证**: 需要

### 8.3 获取用户收到的评价
- **URL**: `GET /api/rating/user/{userId}`

### 8.4 获取用户信用信息
- **URL**: `GET /api/rating/credit`
- **认证**: 需要
- **响应**:
```json
{
  "code": 200,
  "data": {
    "userId": 1,
    "creditScore": 510,
    "creditLevel": "信用良好",
    "creditLevelColor": "#409EFF",
    "totalTransactions": 2,
    "completedTransactions": 1,
    "goodRatingCount": 1,
    "mediumRatingCount": 0,
    "badRatingCount": 0,
    "violationCount": 0
  }
}
```

---

## 9. 物流模块 (/api/logistics)

### 9.1 获取物流信息
- **URL**: `GET /api/logistics/{orderId}`
- **认证**: 需要
- **响应**:
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "orderId": 1,
    "companyCode": "SF",
    "companyName": "顺丰速运",
    "trackingNo": "SF1234567890",
    "status": 4,
    "statusText": "已签收",
    "currentLocation": "上海市浦东新区",
    "latitude": 31.235929,
    "longitude": 121.501310,
    "shipTime": "...",
    "receiveTime": "...",
    "tracks": [
      {
        "location": "上海市浦东新区",
        "description": "您的包裹已签收",
        "trackTime": "..."
      }
    ]
  }
}
```

### 9.2 获取物流轨迹
- **URL**: `GET /api/logistics/{orderId}/track`

### 9.3 获取物流地图位置
- **URL**: `GET /api/logistics/{orderId}/location`
- **响应**: 包含经纬度和当前位置

### 9.4 模拟物流更新
- **URL**: `POST /api/logistics/simulate/{orderId}`
- **认证**: 需要 (卖家操作)
- **说明**: 模拟物流状态更新，用于演示

---

## 10. 订单状态说明

| 状态值 | 状态名称 | 说明 |
|--------|----------|------|
| 0 | 待付款 | 买家已下单，等待支付 |
| 1 | 待发货 | 买家已付款，资金在平台托管，等待卖家发货 |
| 2 | 已完成 | 买家已确认收货，资金转入卖家 |
| 3 | 已取消 | 订单已取消，资金已退还（若已付款） |

## 11. 物流状态说明

| 状态值 | 状态名称 |
|--------|----------|
| 0 | 待发货 |
| 1 | 已发货 |
| 2 | 运输中 |
| 3 | 派送中 |
| 4 | 已签收 |

## 12. 信用等级说明

| 分数区间 | 等级 | 颜色 |
|----------|------|------|
| 800-850 | 信用极好 | #F5A623 (金色) |
| 700-799 | 信用优秀 | #67C23A (绿色) |
| 600-699 | 信用良好 | #409EFF (蓝色) |
| 500-599 | 信用一般 | #E6A23C (橙色) |
| 300-499 | 信用较差 | #F56C6C (红色) |

## 13. 物流公司编码

| 编码 | 公司名称 |
|------|----------|
| SF | 顺丰速运 |
| ZTO | 中通快递 |
| YTO | 圆通速递 |
| YUNDA | 韵达快递 |
| STO | 申通快递 |
| JT | 极兔速递 |

## 14. 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 权限不足 |
| 500 | 服务器内部错误 |

---

## 15. 管理端接口 (/api/admin)

> 所有管理端接口需要管理员权限（JWT Token 中 role=admin），请求头需携带 `Authorization: Bearer <token>`

### 15.0a 数据统计
- **URL**: `GET /api/admin/dashboard/stats`
- **认证**: 需要 (管理员)
- **响应**:
```json
{
  "code": 200,
  "data": {
    "userCount": 10,
    "productCount": 50,
    "orderCount": 30,
    "totalAmount": "12345.00",
    "orderStatusDist": {
      "待付款": 5,
      "待发货": 10,
      "已完成": 12,
      "已取消": 3
    }
  }
}
```

### 15.0b 交易趋势
- **URL**: `GET /api/admin/dashboard/trend`
- **认证**: 需要 (管理员)
- **响应**:
```json
{
  "code": 200,
  "data": {
    "dates": ["07-01", "07-02", "07-03", "07-04", "07-05", "07-06", "07-07"],
    "amounts": [1200.0, 1500.0, 800.0, 2000.0, 1800.0, 2500.0, 3000.0],
    "counts": [3, 5, 2, 6, 4, 7, 8]
  }
}
```

### 15.1 管理端用户列表
- **URL**: `GET /api/admin/user/list`
- **认证**: 需要 (管理员)
- **参数**:
  - `pageNum`: 页码 (默认1)
  - `pageSize`: 每页数量 (默认10)
  - `keyword`: 搜索关键词 (可选)
  - `status`: 用户状态 (可选, 0=禁用 1=正常)
- **响应**:
```json
{
  "code": 200,
  "data": {
    "records": [
      {
        "id": 1,
        "username": "xiaoming",
        "nickname": "小明同学",
        "phone": "13800138000",
        "balance": 1000.00,
        "status": 1,
        "role": "user",
        "createTime": "2024-..."
      }
    ],
    "total": 100,
    "current": 1,
    "size": 10
  }
}
```

### 15.2 禁用/启用用户
- **URL**: `PUT /api/admin/user/{id}/status`
- **认证**: 需要 (管理员)
- **参数**: `status` (0=禁用 1=正常)

### 15.3 修改用户角色
- **URL**: `PUT /api/admin/user/{id}/role`
- **认证**: 需要 (管理员)
- **参数**: `role` (user/admin)

---

### 15.4 管理端商品列表
- **URL**: `GET /api/admin/product/list`
- **认证**: 需要 (管理员)
- **参数**:
  - `pageNum`: 页码
  - `pageSize`: 每页数量
  - `keyword`: 搜索关键词
  - `categoryId`: 分类ID (可选)
  - `status`: 商品状态 (可选, 0=下架 1=在售 2=已售)
- **响应**:
```json
{
  "code": 200,
  "data": {
    "records": [...],
    "total": 100,
    "current": 1,
    "size": 10
  }
}
```

### 15.5 管理端更新商品状态
- **URL**: `PUT /api/admin/product/{id}/status`
- **认证**: 需要 (管理员)
- **参数**: `status` (0=下架 1=在售 2=已售)

### 15.5b 管理端编辑商品
- **URL**: `PUT /api/admin/product/{id}`
- **认证**: 需要 (管理员)
- **请求体**:
```json
{
  "title": "商品标题",
  "price": 99.99,
  "categoryId": 1,
  "description": "商品描述",
  "images": "url1,url2",
  "status": 1
}
```

### 15.5c 管理端删除商品
- **URL**: `DELETE /api/admin/product/{id}`
- **认证**: 需要 (管理员)

---

### 15.6 管理端订单列表
- **URL**: `GET /api/admin/order/list`
- **认证**: 需要 (管理员)
- **参数**:
  - `pageNum`: 页码
  - `pageSize`: 每页数量
  - `status`: 订单状态 (可选)
- **响应**: 返回分页的订单列表

### 15.7 管理端订单详情
- **URL**: `GET /api/admin/order/{id}`
- **认证**: 需要 (管理员)
- **响应**: 返回订单详细信息

### 15.7b 管理端发货
- **URL**: `PUT /api/admin/order/{id}/ship`
- **认证**: 需要 (管理员)
- **请求体**:
```json
{
  "companyCode": "SF",
  "companyName": "顺丰速运",
  "trackingNo": "SF1234567890"
}
```

### 15.7c 管理端取消订单
- **URL**: `PUT /api/admin/order/{id}/cancel`
- **认证**: 需要 (管理员)
- **说明**: 只能取消待付款或待发货订单

---

### 15.8 管理端分类列表
- **URL**: `GET /api/admin/category/list`
- **认证**: 需要 (管理员)

### 15.9 管理端新增分类
- **URL**: `POST /api/admin/category`
- **认证**: 需要 (管理员)
- **请求体**:
```json
{
  "name": "新品类",
  "icon": "",
  "sort": 7,
  "status": 1
}
```

### 15.10 管理端修改分类
- **URL**: `PUT /api/admin/category/{id}`
- **认证**: 需要 (管理员)

### 15.11 管理端删除分类
- **URL**: `DELETE /api/admin/category/{id}`
- **认证**: 需要 (管理员)

---

### 15.12 管理端信用列表
- **URL**: `GET /api/admin/credit/list`
- **认证**: 需要 (管理员)
- **参数**:
  - `pageNum`: 页码 (默认1)
  - `pageSize`: 每页数量 (默认10)
  - `creditLevel`: 信用等级筛选 (可选)
- **响应**: 返回分页的用户信用信息列表

---

### 15.13 管理端交易流水列表
- **URL**: `GET /api/admin/transaction/list`
- **认证**: 需要 (管理员)
- **参数**:
  - `type`: 交易类型 (可选, 1=付款 2=托管 3=解冻 4=退款)
  - `status`: 交易状态 (可选, 0=处理中 1=成功 2=失败)
  - `pageNum`: 页码 (默认1)
  - `pageSize`: 每页数量 (默认10)
- **响应**: 返回分页的交易记录列表
