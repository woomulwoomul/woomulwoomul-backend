package com.woomulwoomul.woomulwoomulbackend.api.service.follow

import com.woomulwoomul.woomulwoomulbackend.api.service.user.response.UserGetAllFollowingResponse
import com.woomulwoomul.woomulwoomulbackend.common.request.PageRequest
import com.woomulwoomul.woomulwoomulbackend.common.response.PageData
import com.woomulwoomul.woomulwoomulbackend.domain.user.FollowRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
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
}
