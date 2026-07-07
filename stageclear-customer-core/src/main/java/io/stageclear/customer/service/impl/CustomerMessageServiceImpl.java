package io.stageclear.customer.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.stageclear.common.entity.CustomerMessage;
import io.stageclear.common.mapper.CustomerMessageMapper;
import io.stageclear.common.service.CustomerMessageService;
import org.springframework.stereotype.Service;

/**
 * CustomerMessage 业务实现
 */
@Service
public class CustomerMessageServiceImpl extends ServiceImpl<CustomerMessageMapper, CustomerMessage> implements CustomerMessageService {

}
