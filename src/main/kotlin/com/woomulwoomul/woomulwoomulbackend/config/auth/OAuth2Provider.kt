package com.woomulwoomul.woomulwoomulbackend.config.auth

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.convertValue
import com.woomulwoomul.woomulwoomulbackend.domain.user.ProviderType
import com.woomulwoomul.woomulwoomulbackend.domain.user.ProviderType.KAKAO

class OAuth2Provider {

    companion object {

        private val objectMapper = ObjectMapper().apply {
            registerModule(KotlinModule.Builder().build())
            propertyNamingStrategy = PropertyNamingStrategies.SNAKE_CASE
        }


        fun of(providerType: ProviderType, attributes: Map<String, Any>): Map<String, String> {
            return when (providerType) {
                KAKAO -> ofKakao(attributes)
            }
        }

        private fun ofKakao(attributes: Map<String, Any>): Map<String, String> {
            val properties: Properties = objectMapper.convertValue(attributes["properties"], Properties::class.java)
            val kakaoAccount: KakaoAccount = objectMapper.convertValue(attributes["kakao_account"], KakaoAccount::class.java)

            val customAttributes: MutableMap<String, String> = mutableMapOf()
            customAttributes["id"] = attributes["id"].toString()
            customAttributes["email"] = kakaoAccount.email
            customAttributes["imageUrl"] = properties.profileImage
            return customAttributes
        }
    }
}

data class Properties(
    val nickname: String,
    val profileImage: String,
    val thumbnailImage: String,
)

data class KakaoAccount(
    val email: String,
)