package io.stageclear.customer.vo;

import io.stageclear.common.entity.CustomerAgent;
import io.stageclear.common.enums.AgentStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 坐席响应 VO
 */
@Data
@NoArgsConstructor
public class AgentVO {

    private Long id;
    /**
     * 工号，格式 A + 5位自增短号
     */
    private String agentNo;
    /**
     * 关联 sys_user.id，坐席也要登录系统
     */
    private Long userId;
    /**
     * 真实姓名（身份证）
     */
    private String realName;
    /**
     * 花名/昵称，UI 显示用
     */
    private String nickName;
    /**
     * 枚举：ONLINE(在线空闲) / BUSY(已满) / OFFLINE(离线) / AWAY(暂时离开)
     */
    private AgentStatus status;
    /**
     * 最大并发会话数（个人上限）
     */
    private Integer maxSessions;
    /**
     * 当前正在服务的会话数
     */
    private Integer currentLoad;
    /**
     * JUNIOR / SENIOR / EXPERT（影响分配策略权重）
     */
    private String level;
    /**
     * 所属组，用于排班和报表
     */
    private String team;
    /**
     * 头像 URL
     */
    private String avatar;
    private LocalDateTime createdAt;

    public static AgentVO from(CustomerAgent a) {
        if (a == null) return null;
        AgentVO v = new AgentVO();
        v.setId(a.getId());
        v.setAgentNo(a.getAgentNo());
        v.setUserId(a.getUserId());
        v.setRealName(a.getRealName());
        v.setNickName(a.getNickName());
        v.setStatus(AgentStatus.of(a.getStatus()));
        v.setMaxSessions(a.getMaxSessions());
        v.setCurrentLoad(a.getCurrentLoad());
        v.setLevel(a.getLevel());
        v.setTeam(a.getTeam());
        v.setAvatar(a.getAvatar());
        v.setCreatedAt(a.getCreatedAt());
        return v;
    }
}
