package com.woomulwoomul.adminapi.config.auth

import com.woomulwoomul.core.common.constant.ExceptionCode.*
import com.woomulwoomul.core.common.response.CustomException
import com.woomulwoomul.core.config.auth.OAuth2Provider
import com.woomulwoomul.core.domain.user.ProviderType
import com.woomulwoomul.core.domain.user.Role
import com.woomulwoomul.core.domain.user.UserProviderRepository
import com.woomulwoomul.core.domain.user.UserRoleRepository
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CustomOAuth2UserService(
    private val userProviderRepository: UserProviderRepository,
    private val userRoleRepository: UserRoleRepository,
) : DefaultOAuth2UserService() {

    private val userIdConst = "userId"
    private val providerIdConst = "providerId"

    override fun loadUser(userRequest: OAuth2UserRequest?): OAuth2User {
        userRequest?.let {
            if (!it.clientRegistration.providerDetails.userInfoEndpoint.uri.isNullOrBlank().not())
                throw CustomException(OAUTH_UNAUTHENTICATED)
        } ?: throw CustomException(OAUTH_UNAUTHENTICATED)

        val provider = ProviderType.of(userRequest.clientRegistration.registrationId)
        val userNameAttributeName = userRequest.clientRegistration.providerDetails.userInfoEndpoint.userNameAttributeName
        val oAuth2User = super.loadUser(userRequest)
        val attributes = OAuth2Provider.of(provider, userNameAttributeName, oAuth2User.attributes)

        val userProvider = userProviderRepository.findInnerFetchJoinUser(attributes[providerIdConst]!!)
            ?: throw CustomException(USER_NOT_FOUND)
        val userRoles = userRoleRepository.findAllFetchUser(userProvider.user.id!!)

        if (userRoles.none { it.role == Role.ADMIN }) throw CustomException(REQUEST_FORBIDDEN)

        attributes[userIdConst] = userProvider.user.id.toString()

        val authorities: List<GrantedAuthority> = Role.getSimpleGrantedAuthorities(userRoles)
        return DefaultOAuth2User(authorities, attributes.toMap(), userIdConst)
    }
}