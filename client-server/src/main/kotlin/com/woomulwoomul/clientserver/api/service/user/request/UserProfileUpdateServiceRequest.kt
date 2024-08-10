package com.woomulwoomul.clientserver.api.service.user.request

import jakarta.validation.constraints.Size

data class UserProfileUpdateServiceRequest(
    @field:Size(min = 2, max = 10, message = "닉네임은 2자 ~ 10자 이내로 입력해 주세요.")
    var userNickname: String,
    @field:Size(min = 1, max = 500, message = "회원 이미지 URL은 1자 ~500자 이내로 입력해 주세요.")
    var userImageUrl: String,
    @field:Size(max = 60, message = "회원 소개글은 0자 ~ 60자 이내로 입력해 주세요.")
    var userIntroduction: String,
)
