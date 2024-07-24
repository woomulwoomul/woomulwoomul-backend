package com.woomulwoomul.woomulwoomulbackend.api.service.user.response

import com.woomulwoomul.woomulwoomulbackend.domain.user.UserEntity

data class UserLoginResponse(
    val userId: Long,
    val nickname: String,
    val imageUrl: String,
    val email: String,
    val introduction: String,
    val accessToken: String,
    val refreshToken: String,
) {

    constructor(user: UserEntity, accessToken: String, refreshToken: String): this(
        user.id ?: 0,
        user.nickname,
        user.imageUrl,
        user.email,
        user.introduction ?: "",
        accessToken,
        refreshToken
    )
}