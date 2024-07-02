package com.woomulwoomul.woomulwoomulbackend.common.constant

import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.OK

enum class SuccessCode(
    val httpStatus: HttpStatus,
    val message: String,
) {

    /**
     * Answer Controller
     */
    // 201 Created

    // 200 Ok
    FOUND_USER_ANSWERS(OK, "회원 답변 전체 조회를 완료했습니다."),
    FOUND_USER_ANSWER(OK, "회원 답변 조회를 완료했습니다."),

    /**
     * Question Controller
     */
    // 201 Created
    USER_QUESTION_CREATED(CREATED, "회원 질문 생성을 완료했습니다."),

    // 200 Ok
    DEFAULT_QUESTIONS_FOUND(OK, "기본 질문 조회를 완료했습니다."),
    ALL_CATEGORIES_FOUND(OK, "전체 카테고리 조회를 완료했습니다."),

    /**
     * User Controller
     */
    // 201 Created


    // 200 Ok
    USER_PROFILE_FOUND(OK, "회원 프로필 조회를 완료했습니다."),
    USER_PROFILE_UPDATED(OK, "회원 프로필 업데이트를 완료했습니다."),
    USER_IMAGE_UPLOADED(OK, "회원 이미지를 업로드했습니다."),

    /**
     * Auth Controller
     */
    // 200 Ok
    OAUTH2_LOGIN(OK, "SNS 로그인을 했습니다."),

    /**
     * Develop Controller
     */
    // 200 Ok
    SERVER_OK(OK, "헬스 체크를 했습니다."),
}