package com.woomulwoomul.core.common.constant

import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.OK

enum class SuccessCode(
    val httpStatus: HttpStatus,
    val message: String,
) {

    /**
     * Notification Controller
     */
    // 200 Ok
    NOTIFICATIONS_FOUND(OK, "알림 전체 조회를 완료했습니다."),
    NOTIFICATION_READ(OK, "알림 읽음 처리가 완료됐습니다."),

    /**
     * Follow Controller
     */
    // 200 Ok
    FOLLOWING_FOUND(OK, "팔로잉 전체 조회를 완료했습니다."),
    FOLLOW_DELETED(OK, "팔로우가 삭제 됐습니다."),

    /**
     * Answer Controller
     */
    // 201 Created
    ANSWER_CREATED(CREATED, "답변 작성을 완료했습니다."),

    // 200 Ok
    FOUND_USER_ANSWERS(OK, "회원 답변 전체 조회를 완료했습니다."),
    FOUND_USER_ANSWER(OK, "회원 답변 조회를 완료했습니다."),
    ANSWER_IMAGE_UPLOADED(OK, "답변 이미지를 업로드했습니다."),
    ANSWER_UPDATED(OK, "답변 업데이트를 완료했습니다."),
    ANSWER_DELETED(OK, "답변 삭제를 완료했습니다."),

    /**
     * Question Controller
     */
    // 201 Created
    USER_QUESTION_CREATED(CREATED, "회원 질문 생성을 완료했습니다."),

    // 200 Ok
    DEFAULT_QUESTION_FOUND(OK, "기본 질문 조회를 완료했습니다."),
    ALL_CATEGORIES_FOUND(OK, "전체 카테고리 조회를 완료했습니다."),

    /**
     * User Controller
     */
    // 200 Ok
    USER_PROFILE_FOUND(OK, "회원 프로필 조회를 완료했습니다."),
    USER_PROFILE_UPDATED(OK, "회원 프로필 업데이트를 완료했습니다."),
    USER_IMAGE_UPLOADED(OK, "회원 이미지를 업로드했습니다."),
    NICKNAME_AVAILABLE(OK, "사용 가능한 닉네임입니다."),

    /**
     * Auth Controller
     */
    // 200 Ok
    OAUTH2_LOGIN(OK, "SNS 로그인을 했습니다."),
    TOKEN_REFRESHED(OK, "토큰을 재발급 했습니다."),

    /**
     * Develop Controller
     */
    // 200 Ok
    SERVER_OK(OK, "헬스 체크를 했습니다."),
    DB_RESET(OK, "데이터베이스를 초기화하고 테스트 데이터를 주입했습니다."),
    TESTER_TOKEN_GENERATED(OK, "테스트 계정 토큰을 생성했습니다.")
}