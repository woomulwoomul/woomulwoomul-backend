package com.woomulwoomul.woomulwoomulbackend.domain.user.custom

import com.woomulwoomul.woomulwoomulbackend.domain.user.UserEntity

interface UserCustomRepository {

    fun existsByNickname(nickname: String): Boolean

    fun findByUserId(userId: Long): UserEntity?

    fun findByNickname(nickname: String): UserEntity?
}