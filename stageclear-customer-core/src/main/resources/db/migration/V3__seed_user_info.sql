-- =====================================================
-- Stage 3 Auth 测试账号
-- 密码统一为：123456
-- BCrypt: $2b$10$Sizy0.kXcQY/lVraj4WHs.Q9ox95j/67KvQRTAy/YLyKXu2gvO66W
-- =====================================================

INSERT INTO sys_user (
    username,
    password,
    nickname,
    email,
    status,
    is_deleted,
    version,
    created_at,
    updated_at
) VALUES
      (
          'demo_user',
          '$2b$10$Sizy0.kXcQY/lVraj4WHs.Q9ox95j/67KvQRTAy/YLyKXu2gvO66W',
          '测试用户',
          'demo_user@stageclear.local',
          1,
          0,
          0,
          NOW(),
          NOW()
      ),
      (
          'agent_zhang',
          '$2b$10$Sizy0.kXcQY/lVraj4WHs.Q9ox95j/67KvQRTAy/YLyKXu2gvO66W',
          '坐席小张',
          'agent_zhang@stageclear.local',
          1,
          0,
          0,
          NOW(),
          NOW()
      );

INSERT INTO customer_agent (
    agent_no,
    user_id,
    real_name,
    nick_name,
    status,
    max_sessions,
    current_load,
    level,
    team,
    avatar,
    is_deleted,
    created_at,
    updated_at
) VALUES (
             'A00001',
             (SELECT id FROM sys_user WHERE username = 'agent_zhang' AND is_deleted = 0),
             '张三',
             '小张',
             'OFFLINE',
             5,
             0,
             'JUNIOR',
             '默认客服组',
             NULL,
             0,
             NOW(),
             NOW()
         );