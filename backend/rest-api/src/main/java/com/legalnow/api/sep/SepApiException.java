package com.legalnow.api.sep;

public class SepApiException extends RuntimeException {

    public SepApiException(String message) {
        super(message);
    }

    public SepApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
