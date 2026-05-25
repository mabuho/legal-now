package com.legalnow.api.auth.exception;

public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException() {
        super("Email confirmation token has expired");
    }
}
