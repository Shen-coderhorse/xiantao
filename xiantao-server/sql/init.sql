-- ==========================================
-- 闲淘二手交易平台 - 数据库初始化脚本
-- 版本：v3.0 (统一版)
-- 用途：docker-compose 容器初始化（docker-entrypoint-initdb.d）与手动引导；含 CREATE DATABASE / USE
-- 应用运行时的版本化建表由 Flyway 负责： src/main/resources/db/migration/V1__init_schema.sql（与本文件同为 v1 schema，仅去除 CREATE DATABASE / USE）
-- 新增变更请同步新增 Flyway V2+ 迁移并视需更新本文件
-- ==========================================

CREATE DATABASE IF NOT EXISTS xiantao DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE xiantao;

-- ==========================================
-- 1. 基础表结构
-- ==========================================

-- 用户表
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码(BCrypt加密)',
    phone VARCHAR(20) UNIQUE COMMENT '手机号',
    nickname VARCHAR(50) COMMENT '昵称',
    avatar VARCHAR(255) COMMENT '头像',
    status TINYINT DEFAULT 1 COMMENT '状态：0禁用 1正常',
    role VARCHAR(20) DEFAULT 'user' COMMENT '角色：user普通用户 admin管理员',
    balance DECIMAL(10,2) DEFAULT 0.00 COMMENT '用户余额（模拟）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 分类表
DROP TABLE IF EXISTS category;
CREATE TABLE category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分类ID',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    icon VARCHAR(255) COMMENT '分类图标',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态：0禁用 1正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- 商品表
DROP TABLE IF EXISTS product;
CREATE TABLE product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '商品ID',
    title VARCHAR(100) NOT NULL COMMENT '商品标题',
    description TEXT COMMENT '商品描述',
    price DECIMAL(10,2) NOT NULL COMMENT '价格',
    original_price DECIMAL(10,2) COMMENT '原价',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    seller_id BIGINT NOT NULL COMMENT '卖家ID',
    images VARCHAR(1000) COMMENT '图片URLs(逗号分隔)',
    status TINYINT DEFAULT 1 COMMENT '状态：0下架 1在售 2已售',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_category_id (category_id),
    INDEX idx_seller_id (seller_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 订单表
DROP TABLE IF EXISTS orders;
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID',
    order_no VARCHAR(32) NOT NULL UNIQUE COMMENT '订单编号',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    product_title VARCHAR(100) NOT NULL COMMENT '商品标题快照',
    product_price DECIMAL(10,2) NOT NULL COMMENT '商品价格快照',
    seller_id BIGINT NOT NULL COMMENT '卖家ID',
    buyer_id BIGINT NOT NULL COMMENT '买家ID',
    address_id BIGINT COMMENT '收货地址ID',
    status TINYINT DEFAULT 0 COMMENT '状态：0待付款 1待发货 2已完成 3已取消',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    pay_time DATETIME COMMENT '支付时间',
    complete_time DATETIME COMMENT '完成时间',
    INDEX idx_buyer_id (buyer_id),
    INDEX idx_seller_id (seller_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ==========================================
-- 2. 扩展功能表
-- ==========================================

-- 收货地址表
DROP TABLE IF EXISTS address;
CREATE TABLE address (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '地址ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    receiver_name VARCHAR(50) NOT NULL COMMENT '收货人姓名',
    receiver_phone VARCHAR(20) NOT NULL COMMENT '收货人电话',
    province VARCHAR(50) NOT NULL COMMENT '省份',
    city VARCHAR(50) NOT NULL COMMENT '城市',
    district VARCHAR(50) NOT NULL COMMENT '区县',
    detail_address VARCHAR(200) NOT NULL COMMENT '详细地址',
    latitude DECIMAL(10,6) COMMENT '纬度',
    longitude DECIMAL(10,6) COMMENT '经度',
    is_default TINYINT DEFAULT 0 COMMENT '是否默认：0否 1是',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收货地址表';

-- 担保交易流水表
DROP TABLE IF EXISTS transaction_record;
CREATE TABLE transaction_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '交易流水ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    order_no VARCHAR(32) NOT NULL COMMENT '订单编号',
    transaction_type TINYINT NOT NULL COMMENT '交易类型：1付款 2托管 3解冻 4退款',
    amount DECIMAL(10,2) NOT NULL COMMENT '交易金额',
    from_user_id BIGINT NOT NULL COMMENT '转出用户ID',
    to_user_id BIGINT NOT NULL COMMENT '转入用户ID(平台托管账户为0)',
    status TINYINT DEFAULT 0 COMMENT '状态：0处理中 1成功 2失败',
    remark VARCHAR(200) COMMENT '备注说明',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_order_id (order_id),
    INDEX idx_order_no (order_no),
    INDEX idx_from_user_id (from_user_id),
    INDEX idx_to_user_id (to_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='担保交易流水表';

-- 用户评价表
DROP TABLE IF EXISTS user_rating;
CREATE TABLE user_rating (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '评价ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    reviewer_id BIGINT NOT NULL COMMENT '评价人ID',
    reviewee_id BIGINT NOT NULL COMMENT '被评价人ID',
    rating TINYINT NOT NULL COMMENT '评分：1差评 2中评 3好评',
    content VARCHAR(500) COMMENT '评价内容',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '评价时间',
    INDEX idx_order_id (order_id),
    INDEX idx_reviewer_id (reviewer_id),
    INDEX idx_reviewee_id (reviewee_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户评价表';

-- 用户信用表
DROP TABLE IF EXISTS user_credit;
CREATE TABLE user_credit (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '信用ID',
    user_id BIGINT NOT NULL UNIQUE COMMENT '用户ID',
    credit_score INT DEFAULT 500 COMMENT '信用分(300-850)',
    total_transactions INT DEFAULT 0 COMMENT '总交易次数',
    completed_transactions INT DEFAULT 0 COMMENT '成功交易次数',
    cancelled_transactions INT DEFAULT 0 COMMENT '取消交易次数',
    good_rating_count INT DEFAULT 0 COMMENT '好评数量',
    medium_rating_count INT DEFAULT 0 COMMENT '中评数量',
    bad_rating_count INT DEFAULT 0 COMMENT '差评数量',
    violation_count INT DEFAULT 0 COMMENT '违规次数',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_credit_score (credit_score)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信用表';

-- 物流信息表
DROP TABLE IF EXISTS logistics;
CREATE TABLE logistics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '物流ID',
    order_id BIGINT NOT NULL UNIQUE COMMENT '订单ID',
    company_code VARCHAR(50) NOT NULL COMMENT '物流公司编码',
    company_name VARCHAR(50) NOT NULL COMMENT '物流公司名称',
    tracking_no VARCHAR(50) NOT NULL COMMENT '物流单号',
    status TINYINT DEFAULT 0 COMMENT '状态：0待发货 1已发货 2运输中 3派送中 4已签收',
    current_location VARCHAR(100) COMMENT '当前位置',
    latitude DECIMAL(10,6) COMMENT '当前位置纬度',
    longitude DECIMAL(10,6) COMMENT '当前位置经度',
    ship_time DATETIME COMMENT '发货时间',
    receive_time DATETIME COMMENT '签收时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_order_id (order_id),
    INDEX idx_tracking_no (tracking_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流信息表';

-- 物流轨迹表
DROP TABLE IF EXISTS logistics_track;
CREATE TABLE logistics_track (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '轨迹ID',
    logistics_id BIGINT NOT NULL COMMENT '物流信息ID',
    location VARCHAR(100) COMMENT '位置信息',
    description VARCHAR(500) NOT NULL COMMENT '轨迹描述',
    track_time DATETIME NOT NULL COMMENT '轨迹时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_logistics_id (logistics_id),
    INDEX idx_track_time (track_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流轨迹表';

-- ==========================================
-- 3. 初始数据
-- ==========================================

-- 3.1 用户数据
-- 密码统一为123456（含 admin 用户，实测登录验证），使用BCrypt加密
-- 旧注释曾误标 admin 密码为 admin123，实际与测试账号一致为 123456
-- 实际使用时请通过注册接口创建用户（历史 fix_admin.sql 已合并入本文件并删除）
INSERT INTO sys_user (username, password, phone, nickname, status, role, balance) VALUES
('test001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '13800138001', '小明同学', 1, 'user', 10000.00),
('test002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '13800138002', '小红同学', 1, 'user', 8000.00),
('test003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '13800138003', '小刚同学', 1, 'user', 5000.00),
('admin', '$2a$10$d6pLd7MlnkhpzlE7sujIGugveA9ifbkZqh4n/Za0N4mTIQNtOL9UK', '13800138000', '管理员', 1, 'admin', 99999.00);

-- 3.2 分类数据
INSERT INTO category (name, icon, sort, status) VALUES
('数码产品', '📱', 1, 1),
('图书教材', '📚', 2, 1),
('生活用品', '🏠', 3, 1),
('服装鞋帽', '👔', 4, 1),
('运动户外', '⚽', 5, 1),
('其他', '📦', 6, 1);

-- 3.3 商品数据
INSERT INTO product (title, description, price, original_price, category_id, seller_id, status, view_count) VALUES
('iPhone 14 Pro 256G 深空黑', '自用iPhone 14 Pro，成色99新，无划痕无磕碰，电池健康度92%，配件齐全，支持验机', 5999.00, 8999.00, 1, 1, 1, 128),
('MacBook Pro M2 14寸 16G 512G', '公司年会奖品，全新未拆封，支持官方验证', 12999.00, 15999.00, 1, 1, 1, 256),
('AirPods Pro 2代', '使用半年，功能完好，有原装盒和充电线', 999.00, 1899.00, 1, 2, 1, 89),
('高等数学同济第七版上下册', '考研必备，有少量笔记，不影响阅读', 35.00, 89.00, 2, 2, 1, 45),
('英语四级词汇书+真题', '四级备考资料，包含词汇书和近5年真题', 25.00, 68.00, 2, 3, 1, 67),
('小米落地扇 直流变频款', '使用一年，风力柔和静音，支持APP控制', 129.00, 299.00, 3, 1, 1, 34),
('宜家懒人沙发', '毕业转让，八成新，可拆洗', 199.00, 499.00, 3, 2, 1, 56),
('耐克Air Max 270 运动鞋 42码', '穿过几次，鞋底有轻微磨损，整体很新', 399.00, 1099.00, 4, 3, 1, 78),
('优衣库羽绒服 男款L码', '去年冬天买的，只穿过几次，保暖效果好', 299.00, 599.00, 4, 1, 1, 43),
('迪卡侬瑜伽垫+瑜伽球', '全新未使用，搬家带不走', 89.00, 159.00, 5, 3, 1, 23);

-- 3.4 订单数据
INSERT INTO orders (order_no, product_id, product_title, product_price, seller_id, buyer_id, address_id, status, create_time, pay_time, complete_time) VALUES
('202403160001001', 1, 'iPhone 14 Pro 256G 深空黑', 5999.00, 1, 2, 3, 2, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
('202403160001002', 4, '高等数学同济第七版上下册', 35.00, 2, 3, 5, 1, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), NULL),
('202403160001003', 7, '宜家懒人沙发', 199.00, 2, 1, 1, 0, DATE_SUB(NOW(), INTERVAL 1 DAY), NULL, NULL);

-- 3.5 用户信用数据
INSERT INTO user_credit (user_id, credit_score, total_transactions, completed_transactions, cancelled_transactions, good_rating_count) VALUES
(1, 510, 2, 1, 0, 1),
(2, 515, 2, 1, 0, 2),
(3, 500, 1, 0, 0, 0),
(4, 800, 100, 100, 0, 100);

-- 3.6 收货地址数据
INSERT INTO address (user_id, receiver_name, receiver_phone, province, city, district, detail_address, latitude, longitude, is_default) VALUES
(1, '小明', '13800138001', '北京市', '北京市', '海淀区', '中关村大街1号 北京大学', 39.986789, 116.305276, 1),
(1, '小明妈妈', '13900139001', '北京市', '北京市', '朝阳区', '建国路88号 SOHO现代城', 39.910079, 116.470612, 0),
(2, '小红', '13800138002', '上海市', '上海市', '浦东新区', '陆家嘴环路1000号', 31.235929, 121.501310, 1),
(2, '小红爸爸', '13700137002', '上海市', '上海市', '徐汇区', '漕溪北路595号 上海电影广场', 31.188777, 121.436397, 0),
(3, '小刚', '13800138003', '广州市', '广州市', '天河区', '天河路208号 天河城', 23.133237, 113.323091, 1),
(3, '小刚室友', '13600136003', '广州市', '广州市', '海珠区', '新港西路135号 中山大学', 23.102570, 113.297169, 0),
(4, '管理员', '13800138000', '北京市', '北京市', '西城区', '西长安街1号', 39.908824, 116.397470, 1);

-- 3.7 评价数据
INSERT INTO user_rating (order_id, reviewer_id, reviewee_id, rating, content) VALUES
(1, 2, 1, 3, '商品很好，和描述一致，发货速度快！'),
(1, 1, 2, 3, '买家很爽快，沟通愉快！');

-- 3.8 物流数据
INSERT INTO logistics (order_id, company_code, company_name, tracking_no, status, current_location, latitude, longitude, ship_time) VALUES
(1, 'SF', '顺丰速运', 'SF1234567890', 4, '上海市浦东新区', 31.235929, 121.501310, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(2, 'ZTO', '中通快递', 'ZTO9876543210', 2, '运输中 - 广州市转运中心', 23.133237, 113.323091, DATE_SUB(NOW(), INTERVAL 1 DAY));

-- 3.9 物流轨迹数据
INSERT INTO logistics_track (logistics_id, location, description, track_time) VALUES
(1, '北京市', '卖家已发货，等待揽件', DATE_SUB(NOW(), INTERVAL 5 DAY)),
(1, '北京市海淀区', '快递员已取件，包裹正在发往上海转运中心', DATE_SUB(NOW(), INTERVAL 4 DAY)),
(1, '上海市', '包裹已到达上海转运中心', DATE_SUB(NOW(), INTERVAL 3 DAY)),
(1, '上海市浦东新区', '包裹已到达陆家嘴营业部，快递员正在派送', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(1, '上海市浦东新区', '您的包裹已签收', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(2, '上海市', '卖家已发货，等待揽件', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(2, '上海市浦东新区', '快递员已取件，包裹正在发往广州转运中心', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2, '广州市', '包裹已到达广州转运中心', NOW());

-- 3.10 交易流水数据
INSERT INTO transaction_record (order_id, order_no, transaction_type, amount, from_user_id, to_user_id, status, remark) VALUES
(1, '202403160001001', 1, 5999.00, 2, 0, 1, '买家付款，资金进入平台托管'),
(1, '202403160001001', 2, 5999.00, 2, 0, 1, '资金进入平台托管账户'),
(1, '202403160001001', 3, 5999.00, 0, 1, 1, '买家确认收货，资金解冻转入卖家账户'),
(2, '202403160001002', 1, 35.00, 3, 0, 1, '买家付款，资金进入平台托管');

-- ==========================================
-- 初始化完成
-- ==========================================
SELECT '闲淘数据库初始化完成！' AS message;
-- 创建数据库
CREATE DATABASE IF NOT EXISTS xiantao DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE xiantao;

-- ==========================================
-- 1. 基础表结构
-- ==========================================

-- 用户表
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    phone VARCHAR(20) UNIQUE COMMENT '手机号',
    nickname VARCHAR(50) COMMENT '昵称',
    avatar VARCHAR(255) COMMENT '头像',
    status TINYINT DEFAULT 1 COMMENT '状态：0禁用 1正常',
    balance DECIMAL(10,2) DEFAULT 0.00 COMMENT '用户余额（模拟）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 分类表
DROP TABLE IF EXISTS category;
CREATE TABLE category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分类ID',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    icon VARCHAR(255) COMMENT '分类图标',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态：0禁用 1正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- 商品表
DROP TABLE IF EXISTS product;
CREATE TABLE product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '商品ID',
    title VARCHAR(100) NOT NULL COMMENT '商品标题',
    description TEXT COMMENT '商品描述',
    price DECIMAL(10,2) NOT NULL COMMENT '价格',
    original_price DECIMAL(10,2) COMMENT '原价',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    seller_id BIGINT NOT NULL COMMENT '卖家ID',
    images VARCHAR(1000) COMMENT '图片URLs',
    status TINYINT DEFAULT 1 COMMENT '状态：0下架 1在售 2已售',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 订单表
DROP TABLE IF EXISTS orders;
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID',
    order_no VARCHAR(32) NOT NULL UNIQUE COMMENT '订单编号',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    product_title VARCHAR(100) NOT NULL COMMENT '商品标题快照',
    product_price DECIMAL(10,2) NOT NULL COMMENT '商品价格快照',
    seller_id BIGINT NOT NULL COMMENT '卖家ID',
    buyer_id BIGINT NOT NULL COMMENT '买家ID',
    address_id BIGINT COMMENT '收货地址ID',
    status TINYINT DEFAULT 0 COMMENT '状态：0待付款 1已付款 2已完成 3已取消',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    pay_time DATETIME COMMENT '支付时间',
    complete_time DATETIME COMMENT '完成时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ==========================================
-- 2. 新增功能表
-- ==========================================

-- 收货地址表
DROP TABLE IF EXISTS address;
CREATE TABLE address (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '地址ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    receiver_name VARCHAR(50) NOT NULL COMMENT '收货人姓名',
    receiver_phone VARCHAR(20) NOT NULL COMMENT '收货人电话',
    province VARCHAR(50) NOT NULL COMMENT '省份',
    city VARCHAR(50) NOT NULL COMMENT '城市',
    district VARCHAR(50) NOT NULL COMMENT '区县',
    detail_address VARCHAR(200) NOT NULL COMMENT '详细地址',
    latitude DECIMAL(10,6) COMMENT '纬度（地图坐标）',
    longitude DECIMAL(10,6) COMMENT '经度（地图坐标）',
    is_default TINYINT DEFAULT 0 COMMENT '是否默认：0否 1是',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收货地址表';

-- 担保交易流水表
DROP TABLE IF EXISTS transaction_record;
CREATE TABLE transaction_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '交易流水ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    order_no VARCHAR(32) NOT NULL COMMENT '订单编号',
    transaction_type TINYINT NOT NULL COMMENT '交易类型：1付款 2托管 3解冻 4退款',
    amount DECIMAL(10,2) NOT NULL COMMENT '交易金额',
    from_user_id BIGINT NOT NULL COMMENT '转出用户ID',
    to_user_id BIGINT NOT NULL COMMENT '转入用户ID（平台托管账户为0）',
    status TINYINT DEFAULT 0 COMMENT '状态：0处理中 1成功 2失败',
    remark VARCHAR(200) COMMENT '备注说明',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_order_id (order_id),
    INDEX idx_order_no (order_no),
    INDEX idx_from_user_id (from_user_id),
    INDEX idx_to_user_id (to_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='担保交易流水表';

-- 用户评价表
DROP TABLE IF EXISTS user_rating;
CREATE TABLE user_rating (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '评价ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    reviewer_id BIGINT NOT NULL COMMENT '评价人ID',
    reviewee_id BIGINT NOT NULL COMMENT '被评价人ID',
    rating TINYINT NOT NULL COMMENT '评分：1差评 2中评 3好评',
    content VARCHAR(500) COMMENT '评价内容',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '评价时间',
    INDEX idx_order_id (order_id),
    INDEX idx_reviewer_id (reviewer_id),
    INDEX idx_reviewee_id (reviewee_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户评价表';

-- 用户信用表
DROP TABLE IF EXISTS user_credit;
CREATE TABLE user_credit (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '信用ID',
    user_id BIGINT NOT NULL UNIQUE COMMENT '用户ID',
    credit_score INT DEFAULT 500 COMMENT '信用分（300-850）',
    total_transactions INT DEFAULT 0 COMMENT '总交易次数',
    completed_transactions INT DEFAULT 0 COMMENT '成功交易次数',
    cancelled_transactions INT DEFAULT 0 COMMENT '取消交易次数',
    good_rating_count INT DEFAULT 0 COMMENT '好评数量',
    medium_rating_count INT DEFAULT 0 COMMENT '中评数量',
    bad_rating_count INT DEFAULT 0 COMMENT '差评数量',
    violation_count INT DEFAULT 0 COMMENT '违规次数',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_credit_score (credit_score)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信用表';

-- 物流信息表
DROP TABLE IF EXISTS logistics;
CREATE TABLE logistics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '物流ID',
    order_id BIGINT NOT NULL UNIQUE COMMENT '订单ID',
    company_code VARCHAR(50) NOT NULL COMMENT '物流公司编码',
    company_name VARCHAR(50) NOT NULL COMMENT '物流公司名称',
    tracking_no VARCHAR(50) NOT NULL COMMENT '物流单号',
    status TINYINT DEFAULT 0 COMMENT '状态：0待发货 1已发货 2运输中 3派送中 4已签收',
    current_location VARCHAR(100) COMMENT '当前位置',
    latitude DECIMAL(10,6) COMMENT '当前位置纬度',
    longitude DECIMAL(10,6) COMMENT '当前位置经度',
    ship_time DATETIME COMMENT '发货时间',
    receive_time DATETIME COMMENT '签收时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_order_id (order_id),
    INDEX idx_tracking_no (tracking_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流信息表';

-- 物流轨迹表
DROP TABLE IF EXISTS logistics_track;
CREATE TABLE logistics_track (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '轨迹ID',
    logistics_id BIGINT NOT NULL COMMENT '物流信息ID',
    location VARCHAR(100) COMMENT '位置信息',
    description VARCHAR(500) NOT NULL COMMENT '轨迹描述',
    track_time DATETIME NOT NULL COMMENT '轨迹时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_logistics_id (logistics_id),
    INDEX idx_track_time (track_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流轨迹表';

-- ==========================================
-- 3. 插入初始数据
-- ==========================================

-- 3.1 插入测试用户（密码都是123456，使用BCrypt加密）
INSERT INTO sys_user (username, password, phone, nickname, status, balance) VALUES
('test001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '13800138001', '小明同学', 1, 10000.00),
('test002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '13800138002', '小红同学', 1, 8000.00),
('test003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '13800138003', '小刚同学', 1, 5000.00),
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '13800138000', '管理员', 1, 99999.00);

-- 3.2 插入分类数据
INSERT INTO category (name, icon, sort, status) VALUES
('数码产品', '📱', 1, 1),
('图书教材', '📚', 2, 1),
('生活用品', '🏠', 3, 1),
('服装鞋帽', '👔', 4, 1),
('运动户外', '⚽', 5, 1),
('其他', '📦', 6, 1);

-- 3.3 插入测试商品数据
INSERT INTO product (title, description, price, original_price, category_id, seller_id, status, view_count) VALUES
('iPhone 14 Pro 256G 深空黑', '自用iPhone 14 Pro，成色99新，无划痕无磕碰，电池健康度92%，配件齐全，支持验机', 5999.00, 8999.00, 1, 1, 1, 128),
('MacBook Pro M2 14寸 16G 512G', '公司年会奖品，全新未拆封，支持官方验证', 12999.00, 15999.00, 1, 1, 1, 256),
('AirPods Pro 2代', '使用半年，功能完好，有原装盒和充电线', 999.00, 1899.00, 1, 2, 1, 89),
('高等数学同济第七版上下册', '考研必备，有少量笔记，不影响阅读', 35.00, 89.00, 2, 2, 1, 45),
('英语四级词汇书+真题', '四级备考资料，包含词汇书和近5年真题', 25.00, 68.00, 2, 3, 1, 67),
('小米落地扇 直流变频款', '使用一年，风力柔和静音，支持APP控制', 129.00, 299.00, 3, 1, 1, 34),
('宜家懒人沙发', '毕业转让，八成新，可拆洗', 199.00, 499.00, 3, 2, 1, 56),
('耐克Air Max 270 运动鞋 42码', '穿过几次，鞋底有轻微磨损，整体很新', 399.00, 1099.00, 4, 3, 1, 78),
('优衣库羽绒服 男款L码', '去年冬天买的，只穿过几次，保暖效果好', 299.00, 599.00, 4, 1, 1, 43),
('迪卡侬瑜伽垫+瑜伽球', '全新未使用，搬家带不走', 89.00, 159.00, 5, 3, 1, 23);

-- 3.4 插入测试订单数据
INSERT INTO orders (order_no, product_id, product_title, product_price, seller_id, buyer_id, address_id, status, create_time, pay_time, complete_time) VALUES
('202403160001001', 1, 'iPhone 14 Pro 256G 深空黑', 5999.00, 1, 2, 3, 2, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
('202403160001002', 4, '高等数学同济第七版上下册', 35.00, 2, 3, 5, 1, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), NULL),
('202403160001003', 7, '宜家懒人沙发', 199.00, 2, 1, 1, 0, DATE_SUB(NOW(), INTERVAL 1 DAY), NULL, NULL);

-- 3.5 为测试用户初始化信用数据（基础分500）
INSERT INTO user_credit (user_id, credit_score, total_transactions, completed_transactions, cancelled_transactions, good_rating_count) VALUES
(1, 510, 2, 1, 0, 1),
(2, 515, 2, 1, 0, 2),
(3, 500, 1, 0, 0, 0),
(4, 800, 100, 100, 0, 100);

-- 3.6 为测试用户插入测试地址数据
INSERT INTO address (user_id, receiver_name, receiver_phone, province, city, district, detail_address, latitude, longitude, is_default) VALUES
(1, '小明', '13800138001', '北京市', '北京市', '海淀区', '中关村大街1号 北京大学', 39.986789, 116.305276, 1),
(1, '小明妈妈', '13900139001', '北京市', '北京市', '朝阳区', '建国路88号 SOHO现代城', 39.910079, 116.470612, 0),
(2, '小红', '13800138002', '上海市', '上海市', '浦东新区', '陆家嘴环路1000号', 31.235929, 121.501310, 1),
(2, '小红爸爸', '13700137002', '上海市', '上海市', '徐汇区', '漕溪北路595号 上海电影广场', 31.188777, 121.436397, 0),
(3, '小刚', '13800138003', '广州市', '广州市', '天河区', '天河路208号 天河城', 23.133237, 113.323091, 1),
(3, '小刚室友', '13600136003', '广州市', '广州市', '海珠区', '新港西路135号 中山大学', 23.102570, 113.297169, 0),
(4, '管理员', '13800138000', '北京市', '北京市', '西城区', '西长安街1号', 39.908824, 116.397470, 1);

-- 3.7 插入测试评价数据
INSERT INTO user_rating (order_id, reviewer_id, reviewee_id, rating, content) VALUES
(1, 2, 1, 3, '商品很好，和描述一致，发货速度快！'),
(1, 1, 2, 3, '买家很爽快，沟通愉快！');

-- 3.8 插入测试物流数据
INSERT INTO logistics (order_id, company_code, company_name, tracking_no, status, current_location, latitude, longitude, ship_time) VALUES
(1, 'SF', '顺丰速运', 'SF1234567890', 4, '上海市浦东新区', 31.235929, 121.501310, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(2, 'ZTO', '中通快递', 'ZTO9876543210', 2, '运输中 - 广州市转运中心', 23.133237, 113.323091, DATE_SUB(NOW(), INTERVAL 1 DAY));

-- 3.9 插入测试物流轨迹数据
INSERT INTO logistics_track (logistics_id, location, description, track_time) VALUES
(1, '北京市', '小明同学已接单，准备配送', DATE_SUB(NOW(), INTERVAL 5 DAY)),
(1, '北京市海淀区', '快递员已取件，包裹正在发往上海转运中心', DATE_SUB(NOW(), INTERVAL 4 DAY)),
(1, '上海市', '包裹已到达上海转运中心', DATE_SUB(NOW(), INTERVAL 3 DAY)),
(1, '上海市浦东新区', '包裹已到达陆家嘴营业部，快递员正在派送', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(1, '上海市浦东新区', '您的包裹已签收，签收人：小红同学', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(2, '上海市', '小红同学已接单，准备配送', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(2, '上海市浦东新区', '快递员已取件，包裹正在发往广州转运中心', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2, '广州市', '包裹已到达广州转运中心', DATE_SUB(NOW(), INTERVAL 0 DAY));

-- 3.10 插入测试交易流水数据
INSERT INTO transaction_record (order_id, order_no, transaction_type, amount, from_user_id, to_user_id, status, remark) VALUES
(1, '202403160001001', 1, 5999.00, 2, 0, 1, '买家付款，资金进入平台托管'),
(1, '202403160001001', 2, 5999.00, 0, 2, 1, '买家确认收货，资金解冻转入卖家账户'),
(2, '202403160001002', 1, 35.00, 3, 0, 1, '买家付款，资金进入平台托管');

-- ==========================================
-- 初始化完成
-- ==========================================

SELECT '数据库初始化完成！' AS message;
