package com.legalnow.api.janus;

public class JanusRoomCreationException extends RuntimeException {
    public JanusRoomCreationException(String message) {
        super(message);
    }

    public JanusRoomCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
