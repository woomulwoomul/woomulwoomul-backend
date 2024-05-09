package com.woomulwoomul.woomulwoomulbackend.common.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.OK;

@Getter
public enum SuccessCode {
    /**
     * Develop Controller
     */
    // 200 Ok
    SERVER_OK(OK, "헬스 체크를 했습니다."),
    ;

    private final HttpStatus status;
    private final String message;

    SuccessCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
