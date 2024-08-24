package com.woomulwoomul.clientapi.controller.user.request

import com.woomulwoomul.clientapi.service.user.request.UserProfileUpdateServiceRequest
import jakarta.validation.constraints.NotBlank

data class UserProfileUpdateRequest(
    @field:NotBlank(message = "회원 닉네임은 필수 입력입니다.")
    var userNickname: String?,
    @field:NotBlank(message = "회원 이미지 URL은 필수 입력입니다.")
    var userImageUrl: String?,
    var userIntroduction: String?,
) {

    fun toServiceRequest(): UserProfileUpdateServiceRequest {
        return UserProfileUpdateServiceRequest(userNickname!!, userImageUrl!!, userIntroduction ?: "")
    }
}
