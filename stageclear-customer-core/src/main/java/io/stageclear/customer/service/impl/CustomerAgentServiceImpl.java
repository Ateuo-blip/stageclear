package io.stageclear.customer.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.stageclear.common.entity.CustomerAgent;
import io.stageclear.common.mapper.CustomerAgentMapper;
import io.stageclear.common.service.CustomerAgentService;
import org.springframework.stereotype.Service;

/**
 * CustomerAgent 业务实现
 */
@Service
public class CustomerAgentServiceImpl extends ServiceImpl<CustomerAgentMapper, CustomerAgent> implements CustomerAgentService {

}
