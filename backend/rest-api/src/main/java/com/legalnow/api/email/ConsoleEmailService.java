package com.legalnow.api.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class ConsoleEmailService implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(ConsoleEmailService.class);

    @Override
    public void sendConfirmationEmail(String to, String token) {
        log.info("[EMAIL] To: {} | Confirm token: {}", to, token);
    }
}
