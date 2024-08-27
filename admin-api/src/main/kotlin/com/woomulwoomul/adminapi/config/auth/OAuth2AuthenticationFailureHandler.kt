package com.woomulwoomul.adminapi.config.auth

import com.fasterxml.jackson.databind.ObjectMapper
import com.woomulwoomul.core.common.constant.ExceptionCode
import com.woomulwoomul.core.common.response.ExceptionResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.http.Consts
import org.springframework.http.MediaType
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
        response!!.status = ExceptionCode.OAUTH_UNAUTHENTICATED.httpStatus.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = Consts.UTF_8.name()

        val responseBody: String = objectMapper.writeValueAsString(ExceptionResponse.toResponseEntity(ExceptionCode.OAUTH_UNAUTHENTICATED))
        val outputStream = response.outputStream
        outputStream.write(responseBody.toByteArray())
        outputStream.flush()
    }
}