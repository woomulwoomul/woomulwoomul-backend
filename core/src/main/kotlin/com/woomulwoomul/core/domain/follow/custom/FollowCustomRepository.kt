package com.woomulwoomul.core.domain.follow.custom

import com.woomulwoomul.core.common.request.PageRequest
import com.woomulwoomul.core.common.response.PageData
import com.woomulwoomul.core.domain.follow.FollowEntity

interface FollowCustomRepository {

    fun exists(userId: Long, followerUserId: Long): Boolean

    fun findAllByFollower(userId: Long, pageRequest: PageRequest): PageData<FollowEntity>

    fun findAll(userId: Long, followerUserId: Long): List<FollowEntity>
}