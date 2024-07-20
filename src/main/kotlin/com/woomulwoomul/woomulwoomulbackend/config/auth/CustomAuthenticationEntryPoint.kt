package com.woomulwoomul.woomulwoomulbackend.config.auth

import com.fasterxml.jackson.databind.ObjectMapper
import com.woomulwoomul.woomulwoomulbackend.common.constant.ExceptionCode.*
import com.woomulwoomul.woomulwoomulbackend.common.response.CustomException
import com.woomulwoomul.woomulwoomulbackend.common.response.ExceptionResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.io.IOException

@Component
class CustomAuthenticationEntryPoint(
    private val objectMapper: ObjectMapper,
) : AuthenticationEntryPoint {

    override fun commence(request: HttpServletRequest?,
                          response: HttpServletResponse?,
                          authException: AuthenticationException?) {
        response?.apply {
            status = HttpStatus.UNAUTHORIZED.value()
            contentType = APPLICATION_JSON_VALUE
            try {
                val responseBody = objectMapper.writeValueAsString(ExceptionResponse(TOKEN_UNAUTHENTICATED))
                outputStream.write(responseBody.toByteArray())
            } catch (e: IOException) {
                throw CustomException(SERVER_ERROR, e.cause)
            }
            outputStream.flush()
        }
    }
}