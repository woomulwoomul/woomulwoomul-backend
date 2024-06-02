package com.woomulwoomul.woomulwoomulbackend.config.auth

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import org.springframework.stereotype.Component

@Component
class OAuth2AuthenticationFailureHandler : SimpleUrlAuthenticationFailureHandler() {

    override fun onAuthenticationFailure(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        exception: AuthenticationException?
    ) {
        println("===============================")
        println("===============================")
        println("===============================")
        println("OAuth2AuthenticationFailureHandler")
        println("request=".plus(request))
        println("response=".plus(response))
        println("===============================")
        println("===============================")
        println("===============================")
        super.onAuthenticationFailure(request, response, exception)
    }
}