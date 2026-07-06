CREATE TABLE `sys_user` (
                            `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                            `username` VARCHAR(50) NOT NULL COMMENT '用户名',
                            `password` VARCHAR(100) NOT NULL COMMENT '密码',
                            `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
                            `email` VARCHAR(255) DEFAULT NULL COMMENT '邮箱',
                            `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态 0禁用 1启用',
                            `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标记 0未删除 1已删除',
                            `version` BIGINT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
                            `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `uk_username` (`username`),
                            UNIQUE KEY `uk_email` (`email`),
                            KEY `idx_status_deleted` (`status`, `is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
