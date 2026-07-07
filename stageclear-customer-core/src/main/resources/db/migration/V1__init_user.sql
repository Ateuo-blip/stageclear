CREATE TABLE `sys_user` (
                            `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                            `username` VARCHAR(50) NOT NULL COMMENT '用户名',
                            `password` VARCHAR(100) NOT NULL COMMENT '密码（bcrypt 哈希，固定 60 字符）',
                            `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
                            `email` VARCHAR(255) DEFAULT NULL COMMENT '邮箱',
                            `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态 0禁用 1启用',
                            `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标记 0未删除 1已删除',
                            `version` BIGINT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
                            `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            `created_by` BIGINT DEFAULT NULL COMMENT '创建人 id',
                            `updated_by` BIGINT DEFAULT NULL COMMENT '更新人 id',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `uk_username` (`username`, `is_deleted`),
                            UNIQUE KEY `uk_email` (`email`, `is_deleted`),
                            KEY `idx_status_deleted` (`status`, `is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';