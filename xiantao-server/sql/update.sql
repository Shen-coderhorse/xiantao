-- ==========================================
-- 此文件已合并到 init.sql，保留仅作历史记录
-- 请使用 init.sql 进行数据库初始化
-- ==========================================
USE xiantao;

-- 此升级脚本的所有表结构和数据已包含在 init.sql 中
-- 无需单独执行此文件
-- ==========================================
-- 二手交易平台数据库升级脚本
-- 版本：v2.0
-- 更新日期：2026-04-28
-- ==========================================

USE xiantao;

-- ==========================================
-- 1. 修改现有表结构
-- ==========================================

-- 1.1 用户表新增余额字段
ALTER TABLE sys_user ADD COLUMN balance DECIMAL(10,2) DEFAULT 0.00 COMMENT '用户余额（模拟）' AFTER status;

-- 1.2 订单表新增收货地址ID字段
ALTER TABLE orders ADD COLUMN address_id BIGINT COMMENT '收货地址ID' AFTER buyer_id;

-- ==========================================
-- 2. 新增表结构
-- ==========================================

-- 2.1 收货地址表
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

-- 2.2 担保交易流水表
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

-- 2.3 用户评价表
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

-- 2.4 用户信用表
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

-- 2.5 物流信息表
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

-- 2.6 物流轨迹表
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

-- 3.1 为测试用户初始化信用数据（基础分500）
INSERT INTO user_credit (user_id, credit_score, total_transactions, completed_transactions, cancelled_transactions) VALUES
(1, 500, 2, 1, 0),
(2, 500, 2, 1, 0),
(3, 500, 1, 0, 0);

-- 3.2 为测试用户插入测试地址数据
INSERT INTO address (user_id, receiver_name, receiver_phone, province, city, district, detail_address, latitude, longitude, is_default) VALUES
(1, '小明', '13800138001', '北京市', '北京市', '海淀区', '中关村大街1号 北京大学', 39.986789, 116.305276, 1),
(1, '小明妈妈', '13900139001', '北京市', '北京市', '朝阳区', '建国路88号 SOHO现代城', 39.910079, 116.470612, 0),
(2, '小红', '13800138002', '上海市', '上海市', '浦东新区', '陆家嘴环路1000号', 31.235929, 121.501310, 1),
(2, '小红爸爸', '13700137002', '上海市', '上海市', '徐汇区', '漕溪北路595号 上海电影广场', 31.188777, 121.436397, 0),
(3, '小刚', '13800138003', '广州市', '广州市', '天河区', '天河路208号 天河城', 23.133237, 113.323091, 1),
(3, '小刚室友', '13600136003', '广州市', '广州市', '海珠区', '新港西路135号 中山大学', 23.102570, 113.297169, 0);

-- 3.3 插入测试评价数据
INSERT INTO user_rating (order_id, reviewer_id, reviewee_id, rating, content) VALUES
(1, 2, 1, 3, '商品很好，和描述一致，发货速度快！'),
(1, 1, 2, 3, '买家很爽快，沟通愉快！');

-- ==========================================
-- 4. 更新测试订单数据（关联地址）
-- ==========================================

-- 为已完成的订单关联地址
UPDATE orders SET address_id = 3 WHERE order_no = '202403160001001';
UPDATE orders SET address_id = 5 WHERE order_no = '202403160001002';
UPDATE orders SET address_id = 1 WHERE order_no = '202403160001003';

-- ==========================================
-- 升级完成
-- ==========================================

SELECT '数据库升级完成！' AS message;
