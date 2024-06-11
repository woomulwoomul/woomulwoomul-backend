package com.woomulwoomul.woomulwoomulbackend.config.auth

import com.fasterxml.jackson.databind.ObjectMapper
import com.woomulwoomul.woomulwoomulbackend.api.service.user.resposne.UserLoginResponse
import com.woomulwoomul.woomulwoomulbackend.common.constant.CustomHttpHeaders.Companion.REFRESH_TOKEN
import com.woomulwoomul.woomulwoomulbackend.common.constant.SuccessCode.OAUTH2_LOGIN
import com.woomulwoomul.woomulwoomulbackend.common.response.DefaultSingleResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class OAuth2AuthenticationSuccessHandler(
    private val jwtProvider: JwtProvider,
    private val objectMapper: ObjectMapper = ObjectMapper()
) : SimpleUrlAuthenticationSuccessHandler() {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authentication: Authentication?
    ) {
        println("==============onAuthenticationSuccess==============")
        print("authentication.name")
        println(authentication!!.name)
        val headers = jwtProvider.createToken(authentication!!.name.toLong())
        val accessToken = headers.getValue(AUTHORIZATION).toString()
        println("accessToken=".plus(accessToken))
        val refreshToken = headers.getValue(REFRESH_TOKEN).toString()
        println("refreshToken=".plus(refreshToken))


        val body = DefaultSingleResponse(
            code = OAUTH2_LOGIN.name,
            message = OAUTH2_LOGIN.message,
            data = UserLoginResponse(authentication.name.toLong())
        )
        println("body.code=".plus(body.code))
        println("body.message=".plus(body.message))
        println("body.data=".plus(body.data))

        response!!.status = OAUTH2_LOGIN.httpStatus.value()
        response.contentType = APPLICATION_JSON_VALUE
        response.setHeader(AUTHORIZATION, accessToken)
        response.setHeader(REFRESH_TOKEN, refreshToken)
        response.writer.write(objectMapper.writeValueAsString(body))
    }
}