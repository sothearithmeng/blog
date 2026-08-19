package com.kshrd.blog.exception;

/** Thrown when a requested entity does not exist. Mapped to HTTP 404. */
public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String entity, Object id) {
        super(ErrorCode.RESOURCE_NOT_FOUND, entity + " not found with id: " + id);
    }
}
