package com.woomulwoomul.woomulwoomulbackend.api.service.follow

import com.woomulwoomul.woomulwoomulbackend.api.service.user.response.UserGetAllFollowingResponse
import com.woomulwoomul.woomulwoomulbackend.common.request.PageRequest
import com.woomulwoomul.woomulwoomulbackend.common.response.PageData
import com.woomulwoomul.woomulwoomulbackend.domain.follow.FollowRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated

@Service
@Validated
@Transactional(readOnly = true)
class FollowService(
    private val followRepository: FollowRepository,
) {

    /**
     * 팔로잉 전체 조회
     * @param userId 회원 ID
     * @param pageRequest 페이징 요청
     * @return 팔로잉 전체 조회 응답
     */
    fun getAllFollowing(userId: Long, pageRequest: PageRequest): PageData<UserGetAllFollowingResponse> {
        val follows = followRepository.findAllByFollower(userId, pageRequest)
        return PageData(follows.data.map { UserGetAllFollowingResponse(it) }, follows.total)
    }

    /**
     * 팔로우 삭제
     * @param userId 회원 ID
     * @param followUserId 팔로우 회원 ID
     */
    @Transactional
    fun deleteFollow(userId: Long, followUserId: Long) {
        followRepository.findAll(userId, followUserId).map { it.unfollow() }
    }
}
