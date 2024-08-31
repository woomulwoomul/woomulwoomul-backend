package com.woomulwoomul.adminapi.config.auth

import com.fasterxml.jackson.databind.ObjectMapper
import com.woomulwoomul.core.common.constant.CustomHttpHeaders.Companion.REFRESH_TOKEN
import com.woomulwoomul.core.common.constant.SuccessCode
import com.woomulwoomul.core.config.auth.JwtProvider
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.http.Consts
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class OAuth2AuthenticationSuccessHandler(
    private val jwtProvider: JwtProvider,
    private val objectMapper: ObjectMapper,
) : SimpleUrlAuthenticationSuccessHandler() {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authentication: Authentication?
    ) {
        val userId = authentication!!.name.toLong()

        val headers = jwtProvider.createToken(userId)

        response!!.status = SuccessCode.OAUTH2_LOGIN.httpStatus.value()
        response.characterEncoding = Consts.UTF_8.name()
        response.setHeader(AUTHORIZATION, trimJwtToken(headers.getValue(AUTHORIZATION).toString()))
        response.setHeader(REFRESH_TOKEN, trimJwtToken(headers.getValue(REFRESH_TOKEN).toString()))

        redirectStrategy.sendRedirect(request,
            response,
            "${request?.scheme}://${request?.serverName}:${request?.serverPort}/dashboard")
    }

    private fun trimJwtToken(token: String): String {
        return JwtProvider.TOKEN_PREFIX + token.trim('[', ']')
    }
}