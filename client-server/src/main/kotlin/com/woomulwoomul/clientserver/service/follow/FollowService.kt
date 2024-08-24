package com.woomulwoomul.clientserver.service.follow

import com.woomulwoomul.clientserver.service.user.response.UserGetAllFollowingResponse
import com.woomulwoomul.core.common.request.PageRequest
import com.woomulwoomul.core.common.response.PageData
import com.woomulwoomul.core.domain.follow.FollowRepository
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
    fun getAllFollowing(userId: Long, pageRequest: PageRequest): PageData<com.woomulwoomul.clientserver.service.user.response.UserGetAllFollowingResponse> {
        val follows = followRepository.findAllByFollower(userId, pageRequest)
        return PageData(follows.data.map {
            com.woomulwoomul.clientserver.service.user.response.UserGetAllFollowingResponse(
                it
            )
        }, follows.total)
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
