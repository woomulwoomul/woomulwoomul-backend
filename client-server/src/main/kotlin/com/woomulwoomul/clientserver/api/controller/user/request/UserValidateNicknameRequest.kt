package com.woomulwoomul.clientserver.api.controller.user.request

import com.woomulwoomul.core.common.constant.RegexConstants.Companion.LOWERCASE_ENG_KOR_NUMBER_UNDERSCORE
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class UserValidateNicknameRequest(
    @field:Size(min = 2, max = 10, message = "닉네임은 2자 ~ 10자 이내로 입력해 주세요.")
    @field:Pattern(regexp = LOWERCASE_ENG_KOR_NUMBER_UNDERSCORE, message = "닉네임은 한글/영어/숫자/언더바(_)만 사용할 수 있어요.")
    var nickname: String,
)
