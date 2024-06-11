package com.woomulwoomul.woomulwoomulbackend.common.constant

import org.springframework.http.HttpStatus

enum class SuccessCode(
    val httpStatus: HttpStatus,
    val message: String,
) {

    /**
     * UserEntity Controller
     */
    // 201 Created


    // 200 OK
    FOUND_MY_USER_INFO(HttpStatus.OK, "내 회원 정보를 조회했습니다."),
    USER_IMAGE_UPLOADED(HttpStatus.OK, "회원 이미지를 업로드했습니다."),

    /**
     * Auth Controller
     */
    // 201 Created


    // 200 OK


    /**
     * Develop Controller
     */
    // 200 OK
    SERVER_OK(HttpStatus.OK, "헬스 체크를 했습니다."),
}