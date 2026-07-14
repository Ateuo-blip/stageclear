package io.stageclear.customer.statemachine;

import io.stageclear.common.enums.SessionStatus;

public interface SessionStateMachine {

    boolean canTransit(SessionStatus from, SessionStatus to);

    void checkTransit(SessionStatus from, SessionStatus to);
}