package com.woomulwoomul.woomulwoomulbackend.domain.user.custom

interface FollowCustomRepository {

    fun exists(userId: Long, followerUserId: Long): Boolean
}