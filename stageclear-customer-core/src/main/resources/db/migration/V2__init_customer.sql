-- =====================================================
-- V2__init_customer.sql
-- BeeSupport 客服系统 9 张业务表
-- 2026-07-07
-- =====================================================

-- -----------------------------------------------------
-- 1. customer_session  会话
-- -----------------------------------------------------
CREATE TABLE customer_session (
    id            BIGINT        NOT NULL AUTO_INCREMENT          COMMENT '主键',
    session_no    VARCHAR(20)   NOT NULL                         COMMENT '会话流水号 S+yyyyMMdd+6位seq',
    user_id       BIGINT        NOT NULL                         COMMENT '发起用户 id（sys_user.id）',
    agent_id      BIGINT        NULL                             COMMENT '坐席 id，分配前为空',
    status        VARCHAR(20)   NOT NULL DEFAULT 'WAITING'       COMMENT 'WAITING/IN_PROGRESS/ENDED/TRANSFERRING',
    channel       VARCHAR(20)   NOT NULL DEFAULT 'WEB'           COMMENT 'WEB/H5/WECHAT/APP',
    source        VARCHAR(255)  NULL                             COMMENT '用户来源 URL',
    priority      TINYINT       NOT NULL DEFAULT 0               COMMENT '0普通 / 1VIP / 2紧急',
    started_at    DATETIME      NULL                             COMMENT '分配坐席时间',
    ended_at      DATETIME      NULL                             COMMENT '结束时间',
    end_reason    VARCHAR(50)   NULL                             COMMENT 'USER_LEAVE / AGENT_CLOSE / TRANSFER / TIMEOUT',
    rating        TINYINT       NULL                             COMMENT '用户评价 1-5',
    is_deleted    TINYINT(1)    NOT NULL DEFAULT 0               COMMENT '软删除',
    created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by    BIGINT        NULL                             COMMENT '创建人 id',
    updated_by    BIGINT        NULL                             COMMENT '更新人 id',
    PRIMARY KEY (id),
    UNIQUE KEY uk_session_no (session_no),
    KEY idx_user_status (user_id, status),
    KEY idx_agent_status (agent_id, status),
    KEY idx_started (started_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客服会话';

-- -----------------------------------------------------
-- 2. customer_agent  坐席
-- -----------------------------------------------------
CREATE TABLE customer_agent (
    id             BIGINT        NOT NULL AUTO_INCREMENT          COMMENT '主键',
    agent_no       VARCHAR(20)   NOT NULL                         COMMENT '工号 A+5位自增短号',
    user_id        BIGINT        NOT NULL                         COMMENT '关联 sys_user.id',
    real_name      VARCHAR(50)   NOT NULL                         COMMENT '真实姓名',
    nick_name      VARCHAR(50)   NULL                             COMMENT '花名',
    status         VARCHAR(20)   NOT NULL DEFAULT 'OFFLINE'       COMMENT 'ONLINE/BUSY/OFFLINE/AWAY',
    max_sessions   INT           NOT NULL DEFAULT 5               COMMENT '最大并发会话数',
    current_load   INT           NOT NULL DEFAULT 0               COMMENT '当前会话数',
    level          VARCHAR(20)   NOT NULL DEFAULT 'JUNIOR'        COMMENT 'JUNIOR/SENIOR/EXPERT',
    team           VARCHAR(50)   NULL                             COMMENT '所属组',
    avatar         VARCHAR(255)  NULL                             COMMENT '头像 URL',
    is_deleted     TINYINT(1)    NOT NULL DEFAULT 0               COMMENT '软删除',
    created_at     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by     BIGINT        NULL,
    updated_by     BIGINT        NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_agent_no (agent_no),
    UNIQUE KEY uk_user_id (user_id),
    KEY idx_status (status),
    KEY idx_team (team)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客服坐席';

-- -----------------------------------------------------
-- 3. customer_message  消息
-- -----------------------------------------------------
CREATE TABLE customer_message (
    id            BIGINT        NOT NULL AUTO_INCREMENT          COMMENT '主键',
    session_id    BIGINT        NOT NULL                         COMMENT '所属会话 id',
    sender_type   VARCHAR(20)   NOT NULL                         COMMENT 'USER/AGENT/AI/SYSTEM',
    sender_id     BIGINT        NULL                             COMMENT '发送者 id',
    content_type  VARCHAR(20)   NOT NULL DEFAULT 'TEXT'           COMMENT 'TEXT/IMAGE/FILE/SYSTEM',
    content       TEXT          NOT NULL                         COMMENT '消息内容',
    send_time     DATETIME(3)   NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '发送时间（毫秒精度）',
    read_flag     TINYINT(1)    NOT NULL DEFAULT 0               COMMENT '是否已读',
    is_deleted    TINYINT(1)    NOT NULL DEFAULT 0               COMMENT '软删除',
    created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by    BIGINT        NULL,
    updated_by    BIGINT        NULL,
    PRIMARY KEY (id),
    KEY idx_session_time (session_id, send_time),
    KEY idx_sender (sender_type, sender_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会话消息';

-- -----------------------------------------------------
-- 4. customer_ticket  工单
-- -----------------------------------------------------
CREATE TABLE customer_ticket (
    id            BIGINT        NOT NULL AUTO_INCREMENT          COMMENT '主键',
    ticket_no     VARCHAR(20)   NOT NULL                         COMMENT '工单号 T+yyyyMMdd+6位seq',
    session_id    BIGINT        NULL                             COMMENT '来源会话 id',
    user_id       BIGINT        NOT NULL                         COMMENT '工单所属用户 id',
    assignee_id   BIGINT        NULL                             COMMENT '处理人坐席 id',
    priority      TINYINT       NOT NULL DEFAULT 0               COMMENT '0低/1中/2高/3紧急',
    status        VARCHAR(20)   NOT NULL DEFAULT 'OPEN'           COMMENT 'OPEN/PROCESSING/PENDING/RESOLVED/CLOSED',
    title         VARCHAR(255)  NOT NULL                         COMMENT '工单标题',
    description   TEXT          NULL                             COMMENT '详细描述',
    category      VARCHAR(50)   NULL                             COMMENT '售后/咨询/投诉/建议',
    resolved_at   DATETIME      NULL                             COMMENT '解决时间',
    closed_at     DATETIME      NULL                             COMMENT '关闭时间',
    is_deleted    TINYINT(1)    NOT NULL DEFAULT 0               COMMENT '软删除',
    created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by    BIGINT        NULL,
    updated_by    BIGINT        NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_ticket_no (ticket_no),
    KEY idx_status_priority (status, priority),
    KEY idx_assignee (assignee_id),
    KEY idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客服工单';

-- -----------------------------------------------------
-- 5. kb_article  知识库文章
-- -----------------------------------------------------
CREATE TABLE kb_article (
    id              BIGINT        NOT NULL AUTO_INCREMENT          COMMENT '主键',
    title           VARCHAR(255)  NOT NULL                         COMMENT '标题',
    content         MEDIUMTEXT    NOT NULL                         COMMENT '正文（支持 Markdown/HTML）',
    category        VARCHAR(50)   NOT NULL                         COMMENT '一级分类',
    sub_category    VARCHAR(50)   NULL                             COMMENT '二级分类',
    keywords        VARCHAR(255)  NULL                             COMMENT '搜索关键词，逗号分隔',
    status          VARCHAR(20)   NOT NULL DEFAULT 'DRAFT'          COMMENT 'DRAFT/PUBLISHED/ARCHIVED',
    view_count      INT           NOT NULL DEFAULT 0               COMMENT '浏览数',
    helpful_count   INT           NOT NULL DEFAULT 0               COMMENT '觉得有用次数',
    unhelpful_count INT           NOT NULL DEFAULT 0               COMMENT '觉得没用次数',
    is_deleted      TINYINT(1)    NOT NULL DEFAULT 0               COMMENT '软删除',
    created_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by      BIGINT        NULL,
    updated_by      BIGINT        NULL,
    PRIMARY KEY (id),
    KEY idx_category (category),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识库文章';

-- -----------------------------------------------------
-- 6. sys_role  角色
-- -----------------------------------------------------
CREATE TABLE sys_role (
    id            BIGINT        NOT NULL AUTO_INCREMENT          COMMENT '主键',
    role_code     VARCHAR(50)   NOT NULL                         COMMENT '角色编码 SUPER_ADMIN/ADMIN/SUPERVISOR/AGENT/SENIOR_AGENT/USER/VIP_USER',
    role_name     VARCHAR(50)   NOT NULL                         COMMENT '角色名',
    description   VARCHAR(255)  NULL                             COMMENT '描述',
    sort_order    INT           NOT NULL DEFAULT 0               COMMENT '排序',
    enabled       TINYINT(1)    NOT NULL DEFAULT 1               COMMENT '启用',
    is_deleted    TINYINT(1)    NOT NULL DEFAULT 0               COMMENT '软删除',
    created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by    BIGINT        NULL,
    updated_by    BIGINT        NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统角色';

-- -----------------------------------------------------
-- 7. sys_permission  权限
-- -----------------------------------------------------
CREATE TABLE sys_permission (
    id            BIGINT        NOT NULL AUTO_INCREMENT          COMMENT '主键',
    perm_code     VARCHAR(100)  NOT NULL                         COMMENT '权限编码 resource:action',
    perm_name     VARCHAR(50)   NOT NULL                         COMMENT '权限名',
    resource      VARCHAR(50)   NOT NULL                         COMMENT '资源 session/agent/ticket/kb/user/role',
    action        VARCHAR(50)   NOT NULL                         COMMENT '操作 read/write/delete/transfer/approve',
    description   VARCHAR(255)  NULL                             COMMENT '描述',
    is_deleted    TINYINT(1)    NOT NULL DEFAULT 0               COMMENT '软删除',
    created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by    BIGINT        NULL,
    updated_by    BIGINT        NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_perm_code (perm_code),
    KEY idx_resource_action (resource, action)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统权限';

-- -----------------------------------------------------
-- 8. sys_user_role  用户-角色关联
-- -----------------------------------------------------
CREATE TABLE sys_user_role (
    id            BIGINT        NOT NULL AUTO_INCREMENT          COMMENT '主键',
    user_id       BIGINT        NOT NULL                         COMMENT '用户 id',
    user_type     VARCHAR(20)   NOT NULL DEFAULT 'USER'           COMMENT 'USER/AGENT 区分关联对象',
    role_id       BIGINT        NOT NULL                         COMMENT '角色 id',
    is_deleted    TINYINT(1)    NOT NULL DEFAULT 0               COMMENT '软删除',
    created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by    BIGINT        NULL,
    updated_by    BIGINT        NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_type_role (user_id, user_type, role_id),
    KEY idx_role (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联';

-- -----------------------------------------------------
-- 9. sys_role_permission  角色-权限关联
-- -----------------------------------------------------
CREATE TABLE sys_role_permission (
    id            BIGINT        NOT NULL AUTO_INCREMENT          COMMENT '主键',
    role_id       BIGINT        NOT NULL                         COMMENT '角色 id',
    permission_id BIGINT        NOT NULL                         COMMENT '权限 id',
    is_deleted    TINYINT(1)    NOT NULL DEFAULT 0               COMMENT '软删除',
    created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by    BIGINT        NULL,
    updated_by    BIGINT        NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_perm (role_id, permission_id),
    KEY idx_perm (permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联';

-- =====================================================
-- 种子数据
-- =====================================================

-- 角色
INSERT INTO sys_role (role_code, role_name, description, sort_order) VALUES
    ('SUPER_ADMIN',   '超级管理员', '系统全部权限', 100),
    ('ADMIN',         '管理员',     '管理后台权限', 90),
    ('SUPERVISOR',    '主管',       '客服管理',     80),
    ('SENIOR_AGENT',  '高级坐席',   '资深客服',     70),
    ('AGENT',         '坐席',       '普通客服',     60),
    ('VIP_USER',      'VIP 用户',   '优先服务',     50),
    ('USER',          '普通用户',   '默认',         10);

-- 权限
INSERT INTO sys_permission (perm_code, perm_name, resource, action) VALUES
    ('session:read',    '查看会话',   'session', 'read'),
    ('session:reply',   '回复消息',   'session', 'write'),
    ('session:transfer','转接会话',   'session', 'transfer'),
    ('session:close',   '关闭会话',   'session', 'delete'),
    ('agent:manage',    '坐席管理',   'agent',   'write'),
    ('ticket:read',     '查看工单',   'ticket',  'read'),
    ('ticket:assign',   '分配工单',   'ticket',  'write'),
    ('ticket:resolve',  '处理工单',   'ticket',  'approve'),
    ('kb:read',         '查看知识库', 'kb',      'read'),
    ('kb:edit',         '编辑知识库', 'kb',      'write'),
    ('kb:publish',      '发布文章',   'kb',      'approve'),
    ('user:manage',     '用户管理',   'user',    'write'),
    ('role:manage',     '角色管理',   'role',    'write');
