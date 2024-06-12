package com.woomulwoomul.woomulwoomulbackend.config.auth

import com.fasterxml.jackson.databind.ObjectMapper
import com.woomulwoomul.woomulwoomulbackend.common.constant.ExceptionCode.OAUTH_UNAUTHENTICATED
import com.woomulwoomul.woomulwoomulbackend.common.response.ExceptionResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.http.Consts.UTF_8
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import org.springframework.stereotype.Component

@Component
class OAuth2AuthenticationFailureHandler(
    private val objectMapper: ObjectMapper,
) : SimpleUrlAuthenticationFailureHandler() {

    override fun onAuthenticationFailure(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        exception: AuthenticationException?
    ) {
        response!!.status = OAUTH_UNAUTHENTICATED.httpStatus.value()
        response.contentType = APPLICATION_JSON_VALUE
        response.characterEncoding = UTF_8.name()

        val responseBody: String = objectMapper.writeValueAsString(ExceptionResponse.toResponseEntity(OAUTH_UNAUTHENTICATED))
        val outputStream = response.outputStream
        outputStream.write(responseBody.toByteArray())
        outputStream.flush()
    }
}