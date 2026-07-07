package io.stageclear.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.stageclear.common.entity.CustomerMessage;
import org.apache.ibatis.annotations.Mapper;

/**
 * CustomerMessage Mapper
 */
@Mapper
public interface CustomerMessageMapper extends BaseMapper<CustomerMessage> {

}
