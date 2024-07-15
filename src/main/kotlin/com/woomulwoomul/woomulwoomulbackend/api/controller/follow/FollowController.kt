package com.woomulwoomul.woomulwoomulbackend.api.controller.follow

import com.woomulwoomul.woomulwoomulbackend.api.service.follow.FollowService
import com.woomulwoomul.woomulwoomulbackend.api.service.user.response.UserGetAllFollowingResponse
import com.woomulwoomul.woomulwoomulbackend.common.constant.SuccessCode
import com.woomulwoomul.woomulwoomulbackend.common.request.PageRequest
import com.woomulwoomul.woomulwoomulbackend.common.response.DefaultPageResponse
import com.woomulwoomul.woomulwoomulbackend.common.response.DefaultResponse
import com.woomulwoomul.woomulwoomulbackend.common.utils.UserUtils
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
            ResponseEntity<DefaultPageResponse<UserGetAllFollowingResponse>> {
        val response = followService.getAllFollowing(UserUtils.getUserId(principal), PageRequest.of(pageFrom, pageSize))

        return DefaultPageResponse.toResponseEntity(SuccessCode.FOLLOWING_FOUND, response)
    }

    // TODO
//    @DeleteMapping("/api/follow/user/{userId}")
//    fun deleteFollow(principal: Principal,
//                     @PathVariable userId: Long): ResponseEntity<DefaultResponse> {
//        followService.deleteFollow(UserUtils.getUserId(principal), userId)
//    }
}