package com.kshrd.blog.exception;

import lombok.Getter;

/** Base type for domain/business rule failures that should map to a 4xx response. */
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
