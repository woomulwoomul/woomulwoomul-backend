package com.woomulwoomul.woomulwoomulbackend.exception;

import com.woomulwoomul.woomulwoomulbackend.common.response.ExceptionCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final ExceptionCode exceptionCode;
    private final Throwable throwable;

    public CustomException(ExceptionCode exceptionCode) {
        this.exceptionCode = exceptionCode;
        this.throwable = null;
    }

    public CustomException(ExceptionCode exceptionCode, Throwable throwable) {
        this.exceptionCode = exceptionCode;
        this.throwable = throwable;
    }
}
