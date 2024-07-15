package com.woomulwoomul.woomulwoomulbackend.domain.user.custom

import com.woomulwoomul.woomulwoomulbackend.common.request.PageRequest
import com.woomulwoomul.woomulwoomulbackend.common.response.PageData
import com.woomulwoomul.woomulwoomulbackend.domain.user.FollowEntity

interface FollowCustomRepository {

    fun exists(userId: Long, followerUserId: Long): Boolean

    fun findAllByFollower(userId: Long, pageRequest: PageRequest): PageData<FollowEntity>
}