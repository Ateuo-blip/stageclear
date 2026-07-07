package io.stageclear.customer.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.stageclear.common.entity.CustomerSession;
import io.stageclear.common.mapper.CustomerSessionMapper;
import io.stageclear.common.service.CustomerSessionService;
import org.springframework.stereotype.Service;

/**
 * CustomerSession 业务实现
 */
@Service
public class CustomerSessionServiceImpl extends ServiceImpl<CustomerSessionMapper, CustomerSession> implements CustomerSessionService {

}
