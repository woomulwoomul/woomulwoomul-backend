package com.woomulwoomul.core.domain.user.custom

import com.woomulwoomul.core.domain.user.UserEntity

interface UserCustomRepository {

    fun existsByNickname(nickname: String): Boolean

    fun findByUserId(userId: Long): UserEntity?

    fun findByNickname(nickname: String): UserEntity?
}