package io.stageclear.customer.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.stageclear.common.entity.KbArticle;
import io.stageclear.common.mapper.KbArticleMapper;
import io.stageclear.common.service.KbArticleService;
import org.springframework.stereotype.Service;

/**
 * KbArticle 业务实现
 */
@Service
public class KbArticleServiceImpl extends ServiceImpl<KbArticleMapper, KbArticle> implements KbArticleService {

}
