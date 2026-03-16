-- 创建数据库
CREATE DATABASE IF NOT EXISTS xiantao DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE xiantao;

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
    status TINYINT DEFAULT 0 COMMENT '状态：0待付款 1已付款 2已完成 3已取消',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    pay_time DATETIME COMMENT '支付时间',
    complete_time DATETIME COMMENT '完成时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 插入测试用户（密码都是123456，使用BCrypt加密）
INSERT INTO sys_user (username, password, phone, nickname, status) VALUES
('test001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '13800138001', '小明同学', 1),
('test002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '13800138002', '小红同学', 1),
('test003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '13800138003', '小刚同学', 1);

-- 插入分类数据
INSERT INTO category (name, icon, sort, status) VALUES
('数码产品', '📱', 1, 1),
('图书教材', '📚', 2, 1),
('生活用品', '🏠', 3, 1),
('服装鞋帽', '👔', 4, 1),
('运动户外', '⚽', 5, 1),
('其他', '📦', 6, 1);

-- 插入测试商品数据
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

-- 插入测试订单数据
INSERT INTO orders (order_no, product_id, product_title, product_price, seller_id, buyer_id, status, create_time, pay_time, complete_time) VALUES
('202403160001001', 1, 'iPhone 14 Pro 256G 深空黑', 5999.00, 1, 2, 2, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
('202403160001002', 4, '高等数学同济第七版上下册', 35.00, 2, 3, 1, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), NULL),
('202403160001003', 7, '宜家懒人沙发', 199.00, 2, 1, 0, DATE_SUB(NOW(), INTERVAL 1 DAY), NULL, NULL);
