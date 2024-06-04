package com.woomulwoomul.woomulwoomulbackend.api.service.user

import com.woomulwoomul.woomulwoomulbackend.common.constant.ExceptionCode.SERVER_ERROR
import com.woomulwoomul.woomulwoomulbackend.common.response.CustomException
import com.woomulwoomul.woomulwoomulbackend.config.auth.OAuth2Provider
import com.woomulwoomul.woomulwoomulbackend.domain.user.ProviderType
import com.woomulwoomul.woomulwoomulbackend.domain.user.Role
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils

@Service
class CustomOAuth2UserService : DefaultOAuth2UserService() {

    override fun loadUser(userRequest: OAuth2UserRequest?): OAuth2User {
        if (userRequest == null || !StringUtils.hasText(userRequest.clientRegistration.providerDetails.userInfoEndpoint.uri))
            throw CustomException(SERVER_ERROR)

        val userNameAttributeName = userRequest.clientRegistration.providerDetails.userInfoEndpoint.userNameAttributeName

        if (!StringUtils.hasText(userNameAttributeName))
            throw CustomException(SERVER_ERROR)

//        val oAuth2User = super.loadUser(userRequest)
        println("registrationId=".plus(userRequest.clientRegistration.registrationId))

        val attributes = OAuth2Provider.of(ProviderType.of(userRequest.clientRegistration.registrationId), userRequest.additionalParameters)
        val authorities: Set<GrantedAuthority> = LinkedHashSet()
        authorities.plus(SimpleGrantedAuthority(Role.USER.name))

        println("===CustomOAuth2UserService===")
        println("attributes")
        for ((key, value) in attributes)
            println("key=".plus(key).plus(", value=").plus(value))
        println("userNameAttributeName=".plus(userNameAttributeName))
        println("authorities=".plus(authorities))
        println("===============================")
        return DefaultOAuth2User(authorities, attributes, userNameAttributeName)
    }


}