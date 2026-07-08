package io.stageclear.customer.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.stageclear.common.entity.SysUser;
import io.stageclear.common.mapper.SysUserMapper;
import io.stageclear.common.service.SysUserService;
import org.springframework.stereotype.Service;

/**
 * SysUser 业务实现
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
}
