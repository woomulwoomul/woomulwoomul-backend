package com.woomulwoomul.woomulwoomulbackend.domain.user.custom

import com.woomulwoomul.woomulwoomulbackend.domain.user.UserEntity

interface UserCustomRepository {

    fun exists(username: String?): Boolean

    fun find(userId: Long?): UserEntity?
}