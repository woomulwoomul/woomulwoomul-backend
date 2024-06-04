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
            map["id"] = attributes["id"] as? String?: ""
            map["email"] = attributes["kakao_account.email"] as? String ?: ""
            map["gender"] = attributes["kakao_account.gender"] as? String ?: ""
            map["imageUrl"] = attributes["kakao_account.profile.profile_image_url"] as? String ?: ""
            map["thumbnailImageUrl"] = attributes["kakao_account.profile.thumbnail_image_url"] as? String ?: ""
            return map
        }
    }
}