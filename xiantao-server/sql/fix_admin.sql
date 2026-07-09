-- ==========================================
-- 此文件已合并到 init.sql，保留仅作历史记录
-- 请使用 init.sql 进行数据库初始化
-- ==========================================
USE xiantao;

DELETE FROM sys_user WHERE username = 'admin';

INSERT INTO sys_user (username, password, phone, nickname, status, role, create_time, update_time) 
VALUES ('admin', '$2a$10$d6pLd7MlnkhpzlE7sujIGugveA9ifbkZqh4n/Za0N4mTIQNtOL9UK', '13800000000', '管理员', 1, 'admin', NOW(), NOW());
USE xiantao;

DELETE FROM sys_user WHERE username = 'admin';

INSERT INTO sys_user (username, password, phone, nickname, status, role, create_time, update_time) 
VALUES ('admin', '$2a$10$d6pLd7MlnkhpzlE7sujIGugveA9ifbkZqh4n/Za0N4mTIQNtOL9UK', '13800000000', '管理员', 1, 'admin', NOW(), NOW());
