package com.woomulwoomul.woomulwoomulbackend.common.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum ExceptionCode {

    /**
     * 400 Bad request
     */
    // @NotBlank, @NotNull

    // @Size

    // @Pattern, @Email - format

    // @Pattern - type

    // @Min, @Max

    /**
     * 401 Unauthorized
     */
    TOKEN_UNAUTHENTICATED(UNAUTHORIZED, "승인되지 않은 요청입니다. 로그인 후에 다시 시도 해주세요."),

    /**
     * 403 Forbidden
     */
    TOKEN_UNAUTHORIZED(FORBIDDEN, "권한이 없는 요청입니다. 로그인 후에 다시 시도 해주세요."),

    /**
     * 404 Not found
     */


    /**
     * 405 Method not allowed
     */
    METHOD_DISABLED(METHOD_NOT_ALLOWED, "불가능한 요청 방법 입니다."),

    /**
     * 409 Conflict
     */

    /**
     * 413 Payload too large
     */
    FILE_SIZE_EXCEED(PAYLOAD_TOO_LARGE, "파일 사이즈는 8MB 이하만 가능합니다."),

    /**
     * 415 Unsupported media type
     */
    FILE_TYPE_UNSUPPORTED(UNSUPPORTED_MEDIA_TYPE, "파일은 '.jpeg', '.jpg', 또는 '.png'만 가능합니다."),

    /**
     * 500 Internal server error
     */
    SERVER_ERROR(INTERNAL_SERVER_ERROR, "서버 에러가 발생했습니다."),

    /**
     * 503 Service unavailable
     */
    ONGOING_INSPECTION(SERVICE_UNAVAILABLE, "현재 서비스 오류로 인해 서비스 이용이 어렵습니다. 잠시 후에 시도해 주세요."),
    ;

    private final HttpStatus status;
    private final String message;

    ExceptionCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
