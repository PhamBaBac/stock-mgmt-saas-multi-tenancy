package com.bacpham.saas.exceptions;

public class InvalidRequestException extends BusinessException {
    public InvalidRequestException(final String message) {
        super(message);
    }
}