package com.woomulwoomul.woomulwoomulbackend.api.service.user.request

import jakarta.validation.constraints.Size

data class UserProfileUpdateServiceRequest(
    @field:Size(min = 2, max = 30, message = "회원 닉네임는 2~30자만 가능합니다.")
    var userNickname: String,
    @field:Size(min = 1, max = 500, message = "회원 이미지 URL은 1~500자만 가능합니다.")
    var userImageUrl: String,
    @field:Size(max = 60, message = "회원 소개글은 0~60자만 가능합니다.")
    var userIntroduction: String,
)
