USE xiantao;

UPDATE category SET name = '数码产品' WHERE id = 1;
UPDATE category SET name = '图书教材' WHERE id = 2;
UPDATE category SET name = '生活用品' WHERE id = 3;
UPDATE category SET name = '服装鞋帽' WHERE id = 4;
UPDATE category SET name = '运动户外' WHERE id = 5;
UPDATE category SET name = '其他' WHERE id = 6;

UPDATE product SET title = 'iPhone 14 Pro 256G 深空黑', description = '自用iPhone 14 Pro，成色99新，无划痕无磕碰，电池健康度92%，配件齐全，支持验机' WHERE id = 1;
UPDATE product SET title = 'MacBook Pro M2 14寸 16G 512G', description = '公司年会奖品，全新未拆封，支持官方验证' WHERE id = 2;
UPDATE product SET title = 'AirPods Pro 2代', description = '使用半年，功能完好，有原装盒和充电线' WHERE id = 3;
UPDATE product SET title = '高等数学同济第七版上下册', description = '考研必备
有少量笔记
不影响阅读' WHERE id = 4;
UPDATE product SET title = '英语四级词汇书+真题', description = '四级备考资料
包含词汇书和近5年真题' WHERE id = 5;
UPDATE product SET title = '小米落地扇 直流变频款', description = '使用一年
风力柔和静音
支持APP控制' WHERE id = 6;
UPDATE product SET title = '宜家懒人沙发', description = '毕业转让
八成新
可拆洗' WHERE id = 7;
UPDATE product SET title = '耐克Air Max 270 运动鞋 42码', description = '穿过几次
鞋底有轻微磨损
整体很新' WHERE id = 8;
UPDATE product SET title = '优衣库羽绒服 男款L码', description = '去年冬天买的
只穿过几次
保暖效果好' WHERE id = 9;
UPDATE product SET title = '迪卡侬瑜伽垫+瑜伽球', description = '全新未使用
搬家带不走' WHERE id = 10;

UPDATE sys_user SET nickname = '小明同学' WHERE id = 1;
UPDATE sys_user SET nickname = '小红同学' WHERE id = 2;
UPDATE sys_user SET nickname = '小刚同学' WHERE id = 3;
