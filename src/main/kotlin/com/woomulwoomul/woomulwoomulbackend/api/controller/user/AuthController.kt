package com.woomulwoomul.woomulwoomulbackend.api.controller.user

import com.woomulwoomul.woomulwoomulbackend.common.constant.SuccessCode.TOKEN_REFRESHED
import com.woomulwoomul.woomulwoomulbackend.common.response.DefaultResponse
import com.woomulwoomul.woomulwoomulbackend.common.utils.UserUtils
import com.woomulwoomul.woomulwoomulbackend.config.auth.JwtProvider
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@Validated
@RestController
class AuthController(
    private val jwtProvider: JwtProvider,
) {

    @GetMapping("/api/auth/token")
    fun refreshToken(principal: Principal): ResponseEntity<DefaultResponse> {
        val headers = jwtProvider.createToken(UserUtils.getUserId(principal))

        return DefaultResponse.toResponseEntity(headers, TOKEN_REFRESHED)
    }
}