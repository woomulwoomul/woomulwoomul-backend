package com.woomulwoomul.clientserver.service.user.response

import com.woomulwoomul.core.domain.follow.FollowEntity

data class UserGetAllFollowingResponse(
    val followId: Long,
    val userId: Long,
    val userNickname: String,
    val userImageUrl: String,
) {

    constructor(follow: FollowEntity): this(
        follow.id ?: 0,
        follow.user.id ?: 0,
        follow.user.nickname,
        follow.user.imageUrl
    )
}