package com.woomulwoomul.clientserver.api.service.user.response

import com.woomulwoomul.core.domain.user.UserEntity

data class UserProfileUpdateResponse(
    val userId: Long,
    val nickname: String,
    val imageUrl: String,
    val introduction: String,
) {

    constructor(user: UserEntity): this(user.id ?: 0, user.nickname, user.imageUrl, user.introduction ?: "")
}
