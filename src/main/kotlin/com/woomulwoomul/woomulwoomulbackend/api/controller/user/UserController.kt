package com.woomulwoomul.woomulwoomulbackend.api.controller.user

import com.woomulwoomul.woomulwoomulbackend.api.service.user.UserService
import com.woomulwoomul.woomulwoomulbackend.common.constant.SuccessCode.USER_PROFILE_FOUND
import com.woomulwoomul.woomulwoomulbackend.common.request.PageRequest
import com.woomulwoomul.woomulwoomulbackend.common.response.DefaultSingleResponse
import com.woomulwoomul.woomulwoomulbackend.common.utils.UserUtils
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@Validated
@RestController
class UserController(
    private val userService: UserService,
) {

    @GetMapping("/api/users/{user-id}")
    fun getUserProfile(@PathVariable(name = "user-id") userId: Long): ResponseEntity<DefaultSingleResponse> {
        val response = userService.getUserProfile(userId)

        return DefaultSingleResponse.toResponseEntity(USER_PROFILE_FOUND, response)
    }
}