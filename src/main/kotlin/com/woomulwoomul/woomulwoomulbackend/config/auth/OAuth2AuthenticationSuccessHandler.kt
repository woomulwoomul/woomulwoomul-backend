package com.woomulwoomul.woomulwoomulbackend.config.auth

import com.fasterxml.jackson.databind.ObjectMapper
import com.woomulwoomul.woomulwoomulbackend.common.constant.CustomHttpHeaders.Companion.REFRESH_TOKEN
import com.woomulwoomul.woomulwoomulbackend.common.constant.ExceptionCode.USER_NOT_FOUND
import com.woomulwoomul.woomulwoomulbackend.common.constant.SuccessCode.OAUTH2_LOGIN
import com.woomulwoomul.woomulwoomulbackend.common.response.CustomException
import com.woomulwoomul.woomulwoomulbackend.common.response.DefaultResponse
import com.woomulwoomul.woomulwoomulbackend.domain.user.UserRepository
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.http.Consts.UTF_8
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.util.UriComponentsBuilder

@Component
class OAuth2AuthenticationSuccessHandler(
    private val jwtProvider: JwtProvider,
    private val userRepository: UserRepository,
    private val objectMapper: ObjectMapper,
    @Value(value = "\${web.domain}")
    private val frontendDomain: String,
) : SimpleUrlAuthenticationSuccessHandler() {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authentication: Authentication?
    ) {
        val userId = authentication!!.name.toLong()
        val user = userRepository.findByUserId(userId) ?: throw CustomException(USER_NOT_FOUND)

        val headers = jwtProvider.createToken(userId)

        val queryParams = LinkedMultiValueMap<String, String>()
        queryParams["user-id"] = userId.toString()
        queryParams["access-token"] = trimJwtToken(headers.getValue(AUTHORIZATION).toString())
        queryParams["refresh-token"] = trimJwtToken(headers.getValue(REFRESH_TOKEN).toString())

        val body = DefaultResponse(code = OAUTH2_LOGIN.name, message = OAUTH2_LOGIN.message,)

        response!!.status = OAUTH2_LOGIN.httpStatus.value()
        response.contentType = APPLICATION_JSON_VALUE
        response.characterEncoding = UTF_8.name()
        response.writer.write(objectMapper.writeValueAsString(body))

        redirectStrategy.sendRedirect(request, response, UriComponentsBuilder.fromUriString(frontendDomain)
						.path("login")
            .queryParams(queryParams)
            .build()
            .toUriString())
    }

    private fun trimJwtToken(token: String): String {
        return token.trim('[', ']')
    }
}