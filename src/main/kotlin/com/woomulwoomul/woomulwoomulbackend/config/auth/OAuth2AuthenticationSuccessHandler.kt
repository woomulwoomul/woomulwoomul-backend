package com.woomulwoomul.woomulwoomulbackend.config.auth

import com.fasterxml.jackson.databind.ObjectMapper
import com.woomulwoomul.woomulwoomulbackend.api.service.user.resposne.UserLoginResponse
import com.woomulwoomul.woomulwoomulbackend.common.constant.CustomHttpHeaders.Companion.REFRESH_TOKEN
import com.woomulwoomul.woomulwoomulbackend.common.constant.ExceptionCode.USER_NOT_FOUND
import com.woomulwoomul.woomulwoomulbackend.common.constant.SuccessCode.OAUTH2_LOGIN
import com.woomulwoomul.woomulwoomulbackend.common.response.CustomException
import com.woomulwoomul.woomulwoomulbackend.common.response.DefaultSingleResponse
import com.woomulwoomul.woomulwoomulbackend.domain.user.UserRepository
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.http.Consts.UTF_8
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class OAuth2AuthenticationSuccessHandler(
    private val jwtProvider: JwtProvider,
    private val userRepository: UserRepository,
    private val objectMapper: ObjectMapper,
) : SimpleUrlAuthenticationSuccessHandler() {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authentication: Authentication?
    ) {
        val userId = authentication!!.name.toLong()
        val user = userRepository.find(userId) ?: throw CustomException(USER_NOT_FOUND)

        val headers = jwtProvider.createToken(userId)
        val accessToken = trimJwtToken(headers.getValue(AUTHORIZATION).toString())
        val refreshToken = trimJwtToken(headers.getValue(REFRESH_TOKEN).toString())

        val body = DefaultSingleResponse(
            code = OAUTH2_LOGIN.name,
            message = OAUTH2_LOGIN.message,
            data = UserLoginResponse(user)
        )

        response!!.status = OAUTH2_LOGIN.httpStatus.value()
        response.contentType = APPLICATION_JSON_VALUE
        response.characterEncoding = UTF_8.name()
        response.setHeader(AUTHORIZATION, accessToken)
        response.setHeader(REFRESH_TOKEN, refreshToken)
        response.writer.write(objectMapper.writeValueAsString(body))
    }

    private fun trimJwtToken(token: String): String {
        return token.trim('[', ']')
    }
}