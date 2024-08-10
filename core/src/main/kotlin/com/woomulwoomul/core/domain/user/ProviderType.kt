package com.woomulwoomul.core.domain.user

import com.woomulwoomul.core.common.constant.ExceptionCode.PROVIDER_TYPE_INVALID
import com.woomulwoomul.core.common.response.CustomException

enum class ProviderType {
    KAKAO
    ;

    companion object {
        fun of(providerType: String): ProviderType {
            return when(providerType.uppercase()) {
                KAKAO.name -> KAKAO
                else -> throw CustomException(PROVIDER_TYPE_INVALID)
            }
        }
    }
}