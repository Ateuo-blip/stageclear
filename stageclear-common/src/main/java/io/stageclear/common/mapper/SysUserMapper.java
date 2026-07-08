package io.stageclear.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.stageclear.common.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * SysUser Mapper
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}
