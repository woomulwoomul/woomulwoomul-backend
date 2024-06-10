package com.woomulwoomul.woomulwoomulbackend.config.auth

import com.fasterxml.jackson.databind.JsonNode
import com.woomulwoomul.woomulwoomulbackend.domain.user.ProviderType
import com.woomulwoomul.woomulwoomulbackend.domain.user.ProviderType.KAKAO

class OAuth2Provider {

    companion object {

        fun of(providerType: ProviderType, node: JsonNode): Map<String, Any> {
            return when (providerType) {
                KAKAO -> ofKakao(node)
            }
        }

        private fun ofKakao(node: JsonNode): Map<String, String> {
            val attributes: MutableMap<String, String> = mutableMapOf()
            attributes["id"] = node.path("id").toString()
            attributes["email"] = node.path("kakao_account.email").toString()
            attributes["gender"] = node.path("kakao_account.gender").toString()
            attributes["imageUrl"] = node.path("kakao_account.profile.profile_image_url").toString()
            attributes["thumbnailImageUrl"] = node.path("kakao_account.profile.thumbnail_image_url").toString()
            return attributes
        }
    }
}