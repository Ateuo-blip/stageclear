package io.stageclear.customer.service.impl;

import io.stageclear.common.entity.CustomerSession;
import io.stageclear.common.service.CustomerSessionService;
import io.stageclear.customer.service.SessionNoGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class SessionNoGeneratorImpl implements SessionNoGenerator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;
    private static final int SEQ_LENGTH = 6;
    private static final String PREFIX = "S";

    private final CustomerSessionService customerSessionService;

    @Override
    public String nextSessionNo() {
        String datePart = LocalDate.now().format(DATE_FORMATTER);
        String sessionNoPrefix = PREFIX + datePart;

        CustomerSession latestSession = customerSessionService.lambdaQuery()
                .select(CustomerSession::getSessionNo)
                .likeRight(CustomerSession::getSessionNo, sessionNoPrefix)
                .orderByDesc(CustomerSession::getSessionNo)
                .last("limit 1")
                .one();

        long nextSeq = 1L;
        if (latestSession != null && latestSession.getSessionNo() != null) {
            String latestSessionNo = latestSession.getSessionNo();
            String seqPart = latestSessionNo.substring(sessionNoPrefix.length());
            nextSeq = Long.parseLong(seqPart) + 1L;
        }

        return sessionNoPrefix + String.format("%0" + SEQ_LENGTH + "d", nextSeq);
    }
}
