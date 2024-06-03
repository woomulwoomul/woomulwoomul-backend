package com.woomulwoomul.woomulwoomulbackend.config.auth

import com.fasterxml.jackson.databind.ObjectMapper
import com.woomulwoomul.woomulwoomulbackend.domain.user.ProviderType
import com.woomulwoomul.woomulwoomulbackend.domain.user.ProviderType.KAKAO

class OAuth2Provider(
    private val objectMapper: ObjectMapper,
) {

    companion object {
        fun of(providerType: ProviderType, attributes: Map<String, Any>): Map<String, Any> {
            return when (providerType) {
                KAKAO -> ofKakao(attributes)
            }
        }

        private fun ofKakao(attributes: Map<String, Any>): Map<String, String> {
            val map: MutableMap<String, String> = mutableMapOf()

            println("=================================")
            println("=================================")
            println("=================================")
            for ((key, value) in attributes)
                println("$key: $value")
            println("=================================")
            println("=================================")
            println("=================================")

            map["email"] = attributes["kakao_account.email"] as? String ?: ""
            map["gender"] = attributes["kakao_account.gender"] as? String ?: ""

            return map
        }
    }
}