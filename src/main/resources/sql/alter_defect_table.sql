
-- 为缺陷表添加版本追踪相关字段
-- 假设缺陷表名为 defect 或 bug，请根据实际表名调整

ALTER TABLE `defect` 
ADD COLUMN `found_version` VARCHAR(50) COMMENT '发现版本 - 在哪个版本中发现了这个缺陷',
ADD COLUMN `introduced_version` VARCHAR(50) COMMENT '引入版本 - 这个缺陷是在哪个版本中被引入的',
ADD COLUMN `is_legacy` TINYINT(1) DEFAULT 0 COMMENT '是否遗留缺陷 - 0:新引入缺陷 1:遗留缺陷（历史版本引入）',
ADD COLUMN `found_after_release` TINYINT(1) DEFAULT 0 COMMENT '发现时机 - 0:发布前发现 1:发布后发现';

-- 为字段添加索引以提升查询性能
CREATE INDEX idx_defect_found_version ON `defect` (`found_version`);
CREATE INDEX idx_defect_introduced_version ON `defect` (`introduced_version`);
CREATE INDEX idx_defect_is_legacy ON `defect` (`is_legacy`);
CREATE INDEX idx_defect_found_after_release ON `defect` (`found_after_release`);

-- 组合索引用于常见的组合查询
CREATE INDEX idx_defect_version_analysis ON `defect` (`found_version`, `is_legacy`, `found_after_release`);

-- 为已存在的数据设置默认值（可选，根据实际需要调整）
-- UPDATE `defect` SET 
--   `found_version` = '未知版本',
--   `introduced_version` = '未知版本',
--   `is_legacy` = 0,
--   `found_after_release` = 0
-- WHERE `found_version` IS NULL;
