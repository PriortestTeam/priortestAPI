
-- 为relation表添加创建时间和创建用户字段
ALTER TABLE `relation` 
ADD COLUMN `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
ADD COLUMN `create_user_id` BIGINT COMMENT '创建用户ID';

-- 为已存在的数据设置默认值
UPDATE `relation` SET `create_time` = NOW() WHERE `create_time` IS NULL;
