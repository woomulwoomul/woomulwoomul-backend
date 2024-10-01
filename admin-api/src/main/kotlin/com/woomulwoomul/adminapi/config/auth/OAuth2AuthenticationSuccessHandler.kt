package com.woomulwoomul.adminapi.config.auth

import com.fasterxml.jackson.databind.ObjectMapper
import com.woomulwoomul.core.common.constant.SuccessCode
import com.woomulwoomul.core.config.auth.JwtProvider
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.http.Consts
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

        val cookies = jwtProvider.createTokenCookies(userId)

        response?.apply {
            status = SuccessCode.OAUTH2_LOGIN.httpStatus.value()
            characterEncoding = Consts.UTF_8.name()
            cookies.forEach { addCookie(it) }
        }

        redirectStrategy.sendRedirect(request, response, "/home")
    }
}