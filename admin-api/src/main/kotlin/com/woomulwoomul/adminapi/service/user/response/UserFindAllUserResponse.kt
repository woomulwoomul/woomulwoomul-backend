package com.woomulwoomul.adminapi.service.user.response

import com.woomulwoomul.core.domain.base.ServiceStatus
import com.woomulwoomul.core.domain.user.UserEntity
import com.woomulwoomul.core.domain.user.UserLoginEntity
import java.time.LocalDateTime

data class UserFindAllUserResponse(
    val id: Long,
    val nickname: String,
    val email: String,
    val imageUrl: String,
    val lastLoginDateTime: LocalDateTime,
    val status: ServiceStatus,
    val createDateTime: LocalDateTime,
    val updateDateTime: LocalDateTime,
) {

    constructor(user: UserEntity, userLogin: UserLoginEntity): this(
        user.id ?: 0,
        user.nickname,
        user.email,
        user.imageUrl,
        userLogin.createDateTime!!,
        user.status,
        user.createDateTime!!,
        user.updateDateTime!!,
    )
}