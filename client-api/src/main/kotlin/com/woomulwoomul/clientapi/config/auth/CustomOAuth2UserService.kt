package com.woomulwoomul.clientapi.config.auth

import com.woomulwoomul.core.common.constant.ExceptionCode
import com.woomulwoomul.core.common.response.CustomException
import com.woomulwoomul.core.common.utils.UserUtils
import com.woomulwoomul.core.config.auth.OAuth2Provider
import com.woomulwoomul.core.domain.user.*
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
    private val userRepository: UserRepository,
    private val userProviderRepository: UserProviderRepository,
    private val userRoleRepository: UserRoleRepository,
) : DefaultOAuth2UserService() {

    private val userIdConst = "userId"
    private val providerIdConst = "providerId"

    @Transactional
    override fun loadUser(userRequest: OAuth2UserRequest?): OAuth2User {
        userRequest?.let {
            if (!it.clientRegistration.providerDetails.userInfoEndpoint.uri.isNullOrBlank().not())
                throw CustomException(ExceptionCode.OAUTH_UNAUTHENTICATED)
        } ?: throw CustomException(ExceptionCode.OAUTH_UNAUTHENTICATED)

        val provider = ProviderType.of(userRequest.clientRegistration.registrationId)
        val userNameAttributeName = userRequest.clientRegistration.providerDetails.userInfoEndpoint.userNameAttributeName
        val oAuth2User = super.loadUser(userRequest)
        val attributes = OAuth2Provider.of(provider, userNameAttributeName, oAuth2User.attributes)

        var userProvider = userProviderRepository.findInnerFetchJoinUser(attributes[providerIdConst]!!)
        val userRoles = if (userProvider == null) {
            attributes["nickname"] = createRandomNickname(attributes["email"]!!)
            val user = userRepository.save(
                OAuth2Provider.toUserEntity(
                attributes["nickname"]!!,
                attributes["email"]!!,
                attributes["imageUrl"]!!
            ))

            userProvider = userProviderRepository.save(
                OAuth2Provider.toUserProviderEntity(
                user = user,
                provider = provider,
                providerId = attributes[providerIdConst]!!
            ))

            listOf(userRoleRepository.save(OAuth2Provider.toUserRoleEntity(user)))
        } else {
            userRoleRepository.findAllFetchUser(userProvider.user.id!!)
        }

        attributes[userIdConst] = userProvider.user.id.toString()

        val authorities: List<GrantedAuthority> = Role.getSimpleGrantedAuthorities(userRoles)

        return DefaultOAuth2User(authorities, attributes.toMap(), userIdConst)
    }

    private fun createRandomNickname(email: String): String {
        val nickname = UserUtils.getNicknameFromEmail(email)
        if (!userRepository.existsByNickname(nickname)) return nickname

        repeat(100) {
            val tmpNickname = UserUtils.generateRandomNickname(nickname)
            if (!userRepository.existsByNickname(tmpNickname)) return tmpNickname
        }
        throw CustomException(ExceptionCode.NICKNAME_GENERATE_FAIL)
    }
}