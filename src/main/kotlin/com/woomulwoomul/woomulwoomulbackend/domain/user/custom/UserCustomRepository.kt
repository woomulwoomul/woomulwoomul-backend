package com.woomulwoomul.woomulwoomulbackend.domain.user.custom

import com.woomulwoomul.woomulwoomulbackend.domain.user.UserEntity

interface UserCustomRepository {

    fun exists(nickname: String?): Boolean

    fun find(id: Long?): UserEntity?
}