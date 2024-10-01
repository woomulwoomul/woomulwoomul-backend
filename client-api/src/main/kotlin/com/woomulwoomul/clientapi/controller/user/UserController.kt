package com.woomulwoomul.clientapi.controller.user

import com.woomulwoomul.clientapi.controller.user.request.UserProfileUpdateRequest
import com.woomulwoomul.clientapi.controller.user.request.UserValidateNicknameRequest
import com.woomulwoomul.core.common.constant.SuccessCode.*
import com.woomulwoomul.core.common.response.DefaultResponse
import com.woomulwoomul.core.common.response.DefaultSingleResponse
import com.woomulwoomul.core.common.utils.UserUtils
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
    private val userService: com.woomulwoomul.clientapi.service.user.UserService,
) {

    @GetMapping("/api/users/nickname")
    fun validateNickname(@RequestParam(name = "nickname")
                         @NotBlank(message = "회원 닉네임은 필수 입력입니다.")
                         nickname: String):
            ResponseEntity<DefaultResponse> {
        userService.validateNickname(UserValidateNicknameRequest(nickname))

        return DefaultResponse.toResponseEntity(NICKNAME_AVAILABLE)
    }

    @GetMapping("/api/users/{userId}")
    fun getUserProfile(@PathVariable userId: Long): ResponseEntity<DefaultSingleResponse> {
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