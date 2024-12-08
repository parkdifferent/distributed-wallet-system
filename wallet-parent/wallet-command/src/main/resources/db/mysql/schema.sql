CREATE TABLE IF NOT EXISTS `t_account` (
    `id` varchar(64) NOT NULL COMMENT '账户ID',
    `asset_type` varchar(16) NOT NULL COMMENT '资产类型',
    `status` varchar(16) NOT NULL COMMENT '账户状态',
    `balance` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '余额',
    `min_balance` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '最小余额',
    `max_balance` decimal(20,8) NOT NULL DEFAULT '999999999.99999999' COMMENT '最大余额',
    `currency` varchar(32) NOT NULL COMMENT '货币类型',
    `owner_id` varchar(64) NOT NULL COMMENT '所有者ID',
    `create_time` datetime NOT NULL COMMENT '创建时间',
    `update_time` datetime NOT NULL COMMENT '更新时间',
    `version` int NOT NULL DEFAULT '1' COMMENT '版本号',
    `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    KEY `idx_owner_id` (`owner_id`),
    KEY `idx_asset_type` (`asset_type`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账户表';
