package com.woomulwoomul.woomulwoomulbackend.api.controller.user

import com.woomulwoomul.woomulwoomulbackend.api.controller.user.request.UserProfileUpdateRequest
import com.woomulwoomul.woomulwoomulbackend.api.service.user.UserService
import com.woomulwoomul.woomulwoomulbackend.common.constant.SuccessCode.USER_PROFILE_FOUND
import com.woomulwoomul.woomulwoomulbackend.common.constant.SuccessCode.USER_PROFILE_UPDATED
import com.woomulwoomul.woomulwoomulbackend.common.response.DefaultSingleResponse
import com.woomulwoomul.woomulwoomulbackend.common.utils.UserUtils
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
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

    @PatchMapping("/api/users")
    fun updateUserProfile(principal: Principal, @RequestBody @Valid request: UserProfileUpdateRequest):
            ResponseEntity<DefaultSingleResponse> {
        val response = userService.updateUserProfile(UserUtils.getUserId(principal), request.toServiceRequest())

        return DefaultSingleResponse.toResponseEntity(USER_PROFILE_UPDATED, response)
    }
}