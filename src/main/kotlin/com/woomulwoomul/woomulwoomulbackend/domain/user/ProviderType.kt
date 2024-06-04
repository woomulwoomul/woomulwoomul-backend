package com.woomulwoomul.woomulwoomulbackend.domain.user

import com.woomulwoomul.woomulwoomulbackend.common.constant.ExceptionCode.PROVIDER_TYPE_INVALID
import com.woomulwoomul.woomulwoomulbackend.common.response.CustomException
import jodd.util.StringUtil

enum class ProviderType {
    KAKAO
    ;

    companion object {
        fun of(providerType: String): ProviderType {
            val type = providerType.uppercase()
            if (StringUtil.equals(type, KAKAO.name))
                return KAKAO
            else
                throw CustomException(PROVIDER_TYPE_INVALID)
        }
    }
}