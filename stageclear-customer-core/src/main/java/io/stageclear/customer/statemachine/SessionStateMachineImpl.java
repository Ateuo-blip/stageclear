package io.stageclear.customer.statemachine;

import io.stageclear.common.enums.SessionStatus;
import io.stageclear.common.exception.BusinessException;
import io.stageclear.common.exception.ErrorCode;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class SessionStateMachineImpl implements SessionStateMachine {

    private static final Map<SessionStatus, Set<SessionStatus>> RULES = Map.of(
            SessionStatus.WAITING, Set.of(SessionStatus.IN_PROGRESS, SessionStatus.ENDED),
            SessionStatus.IN_PROGRESS, Set.of(SessionStatus.TRANSFERRING, SessionStatus.ENDED),
            SessionStatus.TRANSFERRING, Set.of(SessionStatus.IN_PROGRESS, SessionStatus.ENDED),
            SessionStatus.ENDED, Set.of()
    );

    @Override
    public boolean canTransit(SessionStatus from, SessionStatus to) {
        if (from == null || to == null) {
            return false;
        }
        return RULES.getOrDefault(from, Set.of()).contains(to);
    }

    @Override
    public void checkTransit(SessionStatus from, SessionStatus to) {
        if (!canTransit(from, to)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }
    }
}