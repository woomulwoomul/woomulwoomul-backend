package com.woomulwoomul.woomulwoomulbackend.api.service.user.response

import com.woomulwoomul.woomulwoomulbackend.domain.user.UserEntity

data class UserLoginResponse(
    val userId: Long,
    val nickname: String,
    val imageUrl: String,
    val email: String,
    val introduction: String,
) {

    constructor(user: UserEntity): this(
        user.id ?: 0,
        user.nickname,
        user.imageUrl,
        user.email,
        user.introduction ?: "",
    )
}