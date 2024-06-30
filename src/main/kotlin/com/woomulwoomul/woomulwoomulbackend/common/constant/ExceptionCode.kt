package com.woomulwoomul.woomulwoomulbackend.common.constant

import org.springframework.http.HttpStatus

enum class ExceptionCode(
    val httpStatus: HttpStatus,
    val message: String,
) {

    /**
     * 400 Bad Request
     */
    // @NotBlank, @NotNull
    USER_NICKNAME_FIELD_REQUIRED(HttpStatus.BAD_REQUEST, "회원 닉네임은 필수 입력입니다."),
    USER_IMAGE_URL_FIELD_REQUIRED(HttpStatus.BAD_REQUEST, "회원 이미지 URL은 필수 입력입니다."),
    QUESTION_TEXT_FIELD_REQUIRED(HttpStatus.BAD_REQUEST, "질문은 필수 입력입니다."),
    QUESTION_BACKGROUND_COLOR_FIELD_REQUIRED(HttpStatus.BAD_REQUEST, "질문 배경 색상은 필수 입력입니다."),
    CATEGORY_IDS_FIELD_REQUIRED(HttpStatus.BAD_REQUEST, "카테고리 ID는 필수 입력입니다."),

    // @Size, @ByteSize
    USER_NICKNAME_SIZE_INVALID(HttpStatus.BAD_REQUEST, "회원 닉네임는 2~30자만 가능합니다."),
    USER_IMAGE_URL_SIZE_INVALID(HttpStatus.BAD_REQUEST, "회원 이미지 URL은 0~500자만 가능합니다."),
    USER_INTRODUCTION_SIZE_INVALID(HttpStatus.BAD_REQUEST, "회원 소개글은 0~30자만 가능합니다."),
    QUESTION_TEXT_BYTE_SIZE_INVALID(HttpStatus.BAD_REQUEST, "질문 내용은 1~60 바이트만 가능합니다."),
    QUESTION_BACKGROUND_COLOR_SIZE_INVALID(HttpStatus.BAD_REQUEST, "질문 배경 색상은 6자만 가능합니다."),

    // @Pattern, @Email
    EMAIL_FORMAT_INVALID(HttpStatus.BAD_REQUEST, "올바른 이메일 형식을 입력해 주세요."),
    REPORT_TYPE_INVALID(HttpStatus.BAD_REQUEST, "신고 타입은 'MISSION', 'MISSION_MEMBER', 'FEED' 중 하나여야 됩니다."),

    // @Positive, @PositiveOrZero

    // @Min, @Max

    // Custom
    FILE_FIELD_REQUIRED(HttpStatus.BAD_REQUEST, "파일은 필수 입력입니다."),
    PROVIDER_TYPE_INVALID(HttpStatus.BAD_REQUEST, "SNS는 'kakao' 중 하나여야 됩니다."),

    /**
     * 401 Unauthorized
     */
    TOKEN_UNAUTHENTICATED(HttpStatus.UNAUTHORIZED, "승인되지 않은 요청입니다. 다시 로그인 해주세요."),
    OAUTH_UNAUTHENTICATED(HttpStatus.UNAUTHORIZED, "SNS 로그인에 실패했습니다. 다시 로그인 해주세요."),

    /**
     * 403 Forbidden
     */
    TOKEN_UNAUTHORIZED(HttpStatus.FORBIDDEN, "권한이 없는 요청입니다. 로그인 후에 다시 시도 해주세요."),
    REQUEST_FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없는 요청입니다."),

    /**
     * 404 Not Found
     */
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 카테고리입니다."),

    /**
     * 405 Method Not Allowed
     */
    METHOD_DISABLED(HttpStatus.METHOD_NOT_ALLOWED, "잘못된 요청 메소드입니다."),

    /**
     * 409 Conflict
     */
    EXISTING_USER(HttpStatus.CONFLICT, "해당 이메일로 이미 가입된 회원이 있습니다."),
    NICKNAME_GENERATE_FAIL(HttpStatus.CONFLICT, "닉네임 생성 중 오류가 발생했습니다. 잠시 후 다시 시도 해주세요."),

    /**
     * 413 Payload too large
     */
    FILE_SIZE_EXCEED(HttpStatus.PAYLOAD_TOO_LARGE, "파일 사이즈는 8MB 이하만 가능합니다."),

    /**
     * 415 Unsupported Media Type
     */
    IMAGE_TYPE_UNSUPPORTED(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "이미지는 '.jpeg', '.jpg', 또는 '.png'만 가능합니다."),

    /**
     * 423 Locked
     */
    REDIS_LOCK_WAIT_TIMEOUT(HttpStatus.LOCKED, "잠시 후에 다시 시도해주세요."),
    REDIS_LOCK_FORCE_LEASED(HttpStatus.LOCKED, "잠시 후에 다시 시도해주세요."),

    /**
     * 500 Internal Server Error
     */
    EMAIL_SEND_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 전송 중 서버 에러가 발생했습니다."),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러가 발생했습니다."),
    ;
}