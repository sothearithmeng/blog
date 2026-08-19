package com.kshrd.blog.exception;

/** Thrown when a request conflicts with the current state (e.g. a unique constraint). Mapped to HTTP 409. */
public class ConflictException extends BusinessException {

    public ConflictException(String message) {
        super(ErrorCode.CONFLICT, message);
    }
}
