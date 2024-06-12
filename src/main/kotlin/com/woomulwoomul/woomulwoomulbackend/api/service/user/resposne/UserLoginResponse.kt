package com.woomulwoomul.woomulwoomulbackend.api.service.user.resposne

import com.woomulwoomul.woomulwoomulbackend.domain.user.UserEntity

data class UserLoginResponse(
    val userId: Long,
    val username: String,
    val imageUrl: String,
    val email: String,
) {

    constructor(user: UserEntity): this(user.id ?: 0, user.username, user.imageUrl, user.email)
}