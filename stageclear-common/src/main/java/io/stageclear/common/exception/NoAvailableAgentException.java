package io.stageclear.common.exception;

public class NoAvailableAgentException extends BusinessException {

    public NoAvailableAgentException() {
        super(400, "当前坐席繁忙，请稍后重试");
    }

    public NoAvailableAgentException(int code, String message) {
        super(code, message);
    }
}
