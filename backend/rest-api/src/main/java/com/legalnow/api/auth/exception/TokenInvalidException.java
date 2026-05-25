package com.legalnow.api.auth.exception;

public class TokenInvalidException extends RuntimeException {
    public TokenInvalidException() {
        super("Email confirmation token is invalid");
    }
}
