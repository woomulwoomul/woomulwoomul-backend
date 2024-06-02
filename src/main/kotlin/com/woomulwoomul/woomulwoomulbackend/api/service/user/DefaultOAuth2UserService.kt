package com.woomulwoomul.woomulwoomulbackend.api.service.user

import com.woomulwoomul.woomulwoomulbackend.common.constant.ExceptionCode.SERVER_ERROR
import com.woomulwoomul.woomulwoomulbackend.common.response.CustomException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

@Service
class DefaultOAuth2UserService : OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    override fun loadUser(userRequest: OAuth2UserRequest?): OAuth2User {
        if (userRequest == null || !StringUtils.hasText(userRequest.clientRegistration.providerDetails.userInfoEndpoint.uri))
            throw CustomException(SERVER_ERROR)

        val userNameAttributeName = userRequest.clientRegistration.providerDetails.userInfoEndpoint.userNameAttributeName

        if (!StringUtils.hasText(userNameAttributeName))
            throw CustomException(SERVER_ERROR)

        println("===============================")
        println("===============================")
        println("===============================")
        print("userNameAttributeName=")
        println(userNameAttributeName)
        print("userRequest=")
        println(userRequest)
        println("===============================")
        println("===============================")
        println("===============================")

        val userAttributes: Map<String, JvmType.Object> = HashMap()
        val authorities: Set<GrantedAuthority> = LinkedHashSet()

        return DefaultOAuth2User(authorities, userAttributes, userNameAttributeName)
    }


}