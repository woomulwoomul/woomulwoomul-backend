package com.woomulwoomul.woomulwoomulbackend.api.controller.user

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController {

    @GetMapping("/api/user")
    fun getOAuthUser(@AuthenticationPrincipal oAuth2User: OAuth2User): OAuth2User {
        return oAuth2User;
    }
}