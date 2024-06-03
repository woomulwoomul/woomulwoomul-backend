package com.woomulwoomul.woomulwoomulbackend.api.service.user

import com.woomulwoomul.woomulwoomulbackend.common.constant.ExceptionCode.SERVER_ERROR
import com.woomulwoomul.woomulwoomulbackend.common.response.CustomException
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

        val oAuth2User = super.loadUser(userRequest)
        println("oAuth2User: $oAuth2User")

        println("Client Registration: ${userRequest.clientRegistration}")
        println("Access Token: ${userRequest.accessToken.tokenValue}")
        println("Access Token Type: ${userRequest.accessToken.tokenType}")
        println("Access Token Scopes: ${userRequest.accessToken.scopes}")
        println("Access Token Expiration: ${userRequest.accessToken.expiresAt}")
        println("Additional Parameters: ${userRequest.additionalParameters}")

//        val extractedAttributes = OAuth2Provider.of(ProviderType.of(userNameAttributeName), attributes)
        val userAttributes: Map<String, Any> = HashMap()
        val authorities: Set<GrantedAuthority> = LinkedHashSet()
        authorities.plus(SimpleGrantedAuthority(Role.USER.name))

        return DefaultOAuth2User(authorities, userAttributes, userNameAttributeName)
    }


}