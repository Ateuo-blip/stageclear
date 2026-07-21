package io.stageclear.customer.strategy;

import io.stageclear.common.exception.BusinessException;
import io.stageclear.common.exception.ErrorCode;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class AgentAssignStrategyFactory {
    private final Map<String, AgentAssignStrategy> strategyMap;

    public AgentAssignStrategyFactory(List<AgentAssignStrategy> strategies){
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(AgentAssignStrategy::strategyCode,//strategy -> strategy.strategyCode()
                        Function.identity()//strategy -> strategy
                ));//Function.identity()表示流里本身的元素
    }

    public AgentAssignStrategy getStrategy(String strategyCode) {
        AgentAssignStrategy strategy = strategyMap.get(strategyCode);
        if (strategy == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }
        return strategy;
    }
}
