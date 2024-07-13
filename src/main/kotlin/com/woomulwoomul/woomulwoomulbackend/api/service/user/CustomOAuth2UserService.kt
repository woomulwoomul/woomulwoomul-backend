package com.woomulwoomul.woomulwoomulbackend.api.service.user

import com.woomulwoomul.woomulwoomulbackend.common.constant.ExceptionCode.OAUTH_UNAUTHENTICATED
import com.woomulwoomul.woomulwoomulbackend.common.constant.ExceptionCode.NICKNAME_GENERATE_FAIL
import com.woomulwoomul.woomulwoomulbackend.common.response.CustomException
import com.woomulwoomul.woomulwoomulbackend.common.utils.UserUtils
import com.woomulwoomul.woomulwoomulbackend.config.auth.OAuth2Provider
import com.woomulwoomul.woomulwoomulbackend.domain.user.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.StringUtils

@Service
@Transactional(readOnly = true)
class CustomOAuth2UserService(
    private val userRepository: UserRepository,
    private val userProviderRepository: UserProviderRepository,
    private val userRoleRepository: UserRoleRepository,
) : DefaultOAuth2UserService() {

    private val USER_ID_CONST = "userId"
    private val PROVIDER_ID_CONST = "providerId"

    @Transactional
    override fun loadUser(userRequest: OAuth2UserRequest?): OAuth2User {
        if (userRequest == null || !StringUtils.hasText(userRequest.clientRegistration.providerDetails.userInfoEndpoint.uri))
            throw CustomException(OAUTH_UNAUTHENTICATED)

        val provider = ProviderType.of(userRequest.clientRegistration.registrationId)
        val userNameAttributeName = userRequest.clientRegistration.providerDetails.userInfoEndpoint.userNameAttributeName
        val oAuth2User = super.loadUser(userRequest)
        val attributes = OAuth2Provider.of(provider, userNameAttributeName, oAuth2User.attributes)

        var userProvider = userProviderRepository.findInnerFetchJoinUser(attributes[PROVIDER_ID_CONST]!!)
        val userRoles = if (userProvider == null) {
            attributes["nickname"] = createRandomNickname(attributes["email"]!!)
            val user = userRepository.save(OAuth2Provider.toUserEntity(
                attributes["nickname"]!!,
                attributes["email"]!!,
                attributes["imageUrl"]!!
            ))

            userProvider = userProviderRepository.save(OAuth2Provider.toUserProviderEntity(
                user = user,
                provider = provider,
                providerId = attributes[PROVIDER_ID_CONST]!!
            ))

            listOf(userRoleRepository.save(OAuth2Provider.toUserRoleEntity(user)))
        } else {
            userRoleRepository.findAllFetchUser(userProvider.user.id!!)
        }

        attributes[USER_ID_CONST] = userProvider.user.id.toString()

        val authorities: List<GrantedAuthority> = Role.getSimpleGrantedAuthorities(userRoles)

        return DefaultOAuth2User(authorities, attributes.toMap(), USER_ID_CONST)
    }

    private fun createRandomNickname(email: String): String {
        val nickname = UserUtils.getNicknameFromEmail(email)
        print("XXXXXXXXXXXXX")
        println(nickname)
        if (!userRepository.existsByNickname(nickname)) return nickname

        repeat(100) {
            val tmpNickname = UserUtils.generateRandomNickname(nickname)
            print("XXXXXXXXXXXXX")
            println(tmpNickname)
            if (!userRepository.existsByNickname(tmpNickname)) return tmpNickname
        }
        throw CustomException(NICKNAME_GENERATE_FAIL)
    }
}