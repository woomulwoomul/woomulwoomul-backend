package com.woomulwoomul.clientapi.controller.follow

import com.woomulwoomul.clientapi.service.follow.FollowService
import com.woomulwoomul.core.common.constant.SuccessCode.*
import com.woomulwoomul.core.common.request.PageCursorRequest
import com.woomulwoomul.core.common.response.DefaultPageResponse
import com.woomulwoomul.core.common.response.DefaultResponse
import com.woomulwoomul.core.common.utils.UserUtils
import jakarta.validation.constraints.NotBlank
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.security.Principal

@Validated
@RestController
class FollowController(
    private val followService: FollowService
) {

    @GetMapping("/api/following")
    fun getAllFollowing(principal: Principal,
                        @RequestParam(name = "page-from", required = false) pageFrom: Long?,
                        @RequestParam(name = "page-size", required = false) pageSize: Long?):
            ResponseEntity<DefaultPageResponse<com.woomulwoomul.clientapi.service.user.response.UserGetAllFollowingResponse>> {
        val response = followService.getAllFollowing(UserUtils.getUserId(principal), PageCursorRequest.of(pageFrom, pageSize))

        return DefaultPageResponse.toResponseEntity(FOLLOWING_FOUND, response)
    }

    @DeleteMapping("/api/follow")
    fun deleteFollow(principal: Principal,
                     @RequestParam(name = "user-id")
                     @NotBlank(message = "회원 ID는 필수 입력입니다.")
                     userId: Long): ResponseEntity<DefaultResponse> {
        followService.deleteFollow(UserUtils.getUserId(principal), userId)

        return DefaultResponse.toResponseEntity(FOLLOW_DELETED)
    }
}