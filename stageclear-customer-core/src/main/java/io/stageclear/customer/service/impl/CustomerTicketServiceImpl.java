package io.stageclear.customer.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.stageclear.common.entity.CustomerTicket;
import io.stageclear.common.mapper.CustomerTicketMapper;
import io.stageclear.common.service.CustomerTicketService;
import org.springframework.stereotype.Service;

/**
 * CustomerTicket 业务实现
 */
@Service
public class CustomerTicketServiceImpl extends ServiceImpl<CustomerTicketMapper, CustomerTicket> implements CustomerTicketService {

}
