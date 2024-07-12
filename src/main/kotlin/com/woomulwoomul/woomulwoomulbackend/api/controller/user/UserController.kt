package com.woomulwoomul.woomulwoomulbackend.api.controller.user

import com.woomulwoomul.woomulwoomulbackend.api.controller.user.request.UserProfileUpdateRequest
import com.woomulwoomul.woomulwoomulbackend.api.controller.user.request.UserValidateNicknameRequest
import com.woomulwoomul.woomulwoomulbackend.api.service.user.UserService
import com.woomulwoomul.woomulwoomulbackend.common.constant.SuccessCode.*
import com.woomulwoomul.woomulwoomulbackend.common.response.DefaultResponse
import com.woomulwoomul.woomulwoomulbackend.common.response.DefaultSingleResponse
import com.woomulwoomul.woomulwoomulbackend.common.utils.UserUtils
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.security.Principal

@Validated
@RestController
class UserController(
    private val userService: UserService,
) {

    @GetMapping("/api/users/nickname")
    fun validateNickname(@RequestParam(name = "nickname")
                         @NotBlank(message = "회원 닉네임은 필수 입력입니다.")
                         nickname: String):
            ResponseEntity<DefaultResponse> {
        userService.validateNickname(UserValidateNicknameRequest(nickname))

        return DefaultResponse.toResponseEntity(NICKNAME_AVAILABLE)
    }

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

    @PostMapping("/api/users/image", consumes = [MULTIPART_FORM_DATA_VALUE], produces = [APPLICATION_JSON_VALUE])
    fun uploadImage(principal: Principal, @RequestPart(required = false) file: MultipartFile?):
            ResponseEntity<DefaultSingleResponse> {
        val response = userService.uploadImage(UserUtils.getUserId(principal), file)

        return DefaultSingleResponse.toResponseEntity(USER_IMAGE_UPLOADED, response)
    }
}