package com.woomulwoomul.woomulwoomulbackend.config.auth

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class OAuth2AuthenticationSuccessHandler : SimpleUrlAuthenticationSuccessHandler() {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authentication: Authentication?
    ) {
        println("===============================")
        println("===============================")
        println("===============================")
        println("OAuth2AuthenticationSuccessHandler")
        println("request=".plus(request))
        println("response=".plus(response))
        println("===============================")
        println("===============================")
        println("===============================")
        super.onAuthenticationSuccess(request, response, authentication)
    }

}