package com.woomulwoomul.woomulwoomulbackend.config.auth

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.woomulwoomul.woomulwoomulbackend.domain.user.*
import com.woomulwoomul.woomulwoomulbackend.domain.user.ProviderType.KAKAO

class OAuth2Provider {

    companion object {

        private val objectMapper = ObjectMapper().apply {
            registerModule(KotlinModule.Builder().build())
            propertyNamingStrategy = PropertyNamingStrategies.SNAKE_CASE
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }

        fun of(providerType: ProviderType, userNameAttributeName: String, attributes: Map<String, Any>):
                MutableMap<String, String> {
            return when (providerType) {
                KAKAO -> ofKakao(userNameAttributeName, attributes)
            }
        }

        fun toUserEntity(username: String, email: String, imageUrl: String): UserEntity {
            return UserEntity(username = username, email = email, imageUrl = imageUrl)
        }

        fun toUserProviderEntity(userEntity: UserEntity, provider: ProviderType, providerId: String): UserProviderEntity {
            return UserProviderEntity(userEntity = userEntity, provider = provider, providerId = providerId)
        }

        fun toUserRoleEntity(userEntity: UserEntity): UserRoleEntity {
            return UserRoleEntity(userEntity = userEntity, role = Role.USER)
        }

        private fun ofKakao(userNameAttributeName: String, attributes: Map<String, Any>): MutableMap<String, String> {
            val properties: Properties = objectMapper.convertValue(attributes["properties"], Properties::class.java)
            val kakaoAccount: KakaoAccount = objectMapper.convertValue(attributes["kakao_account"], KakaoAccount::class.java)

            val customAttributes: MutableMap<String, String> = mutableMapOf()
            customAttributes["providerId"] = attributes[userNameAttributeName].toString()
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