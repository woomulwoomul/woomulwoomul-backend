package com.woomulwoomul.adminapi.config.auth

import com.fasterxml.jackson.databind.ObjectMapper
import com.woomulwoomul.core.common.constant.CustomHttpHeaders
import com.woomulwoomul.core.common.constant.SuccessCode
import com.woomulwoomul.core.common.response.DefaultResponse
import com.woomulwoomul.core.config.auth.JwtProvider
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.http.Consts
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.util.UriComponentsBuilder

@Component
class OAuth2AuthenticationSuccessHandler(
    private val jwtProvider: JwtProvider,
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

        val headers = jwtProvider.createToken(userId)

        val queryParams = LinkedMultiValueMap<String, String>()
        queryParams["user-id"] = userId.toString()
        queryParams["access-token"] = trimJwtToken(headers.getValue(HttpHeaders.AUTHORIZATION).toString())
        queryParams["refresh-token"] = trimJwtToken(headers.getValue(CustomHttpHeaders.REFRESH_TOKEN).toString())

        val body = DefaultResponse(code = SuccessCode.OAUTH2_LOGIN.name, message = SuccessCode.OAUTH2_LOGIN.message,)

        response!!.status = SuccessCode.OAUTH2_LOGIN.httpStatus.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = Consts.UTF_8.name()
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