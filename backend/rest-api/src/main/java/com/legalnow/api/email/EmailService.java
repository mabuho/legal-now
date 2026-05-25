package com.legalnow.api.email;

public interface EmailService {
    void sendConfirmationEmail(String to, String token);
}
