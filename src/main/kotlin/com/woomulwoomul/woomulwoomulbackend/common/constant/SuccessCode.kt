package com.woomulwoomul.woomulwoomulbackend.common.constant

import org.springframework.http.HttpStatus

enum class SuccessCode(
    val httpStatus: HttpStatus,
    val message: String,
) {

    /**
     * Question Controller
     */
    // 201 Created

    // 200 Ok
    DEFAULT_QUESTIONS_FOUND(HttpStatus.OK, "기본 질문 조회를 완료했습니다."),

    /**
     * User Controller
     */
    // 201 Created


    // 200 Ok
    FOUND_MY_USER_INFO(HttpStatus.OK, "내 회원 정보를 조회했습니다."),
    USER_IMAGE_UPLOADED(HttpStatus.OK, "회원 이미지를 업로드했습니다."),

    /**
     * Auth Controller
     */
    // 200 Ok
    OAUTH2_LOGIN(HttpStatus.OK, "SNS 로그인을 했습니다."),

    /**
     * Develop Controller
     */
    // 200 Ok
    SERVER_OK(HttpStatus.OK, "헬스 체크를 했습니다."),
}