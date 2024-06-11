package com.woomulwoomul.woomulwoomulbackend.domain.user.custom

import com.woomulwoomul.woomulwoomulbackend.domain.user.UserRoleEntity

interface UserRoleCustomRepository {

    fun findAllFetchUser(userId: Long): List<UserRoleEntity>

}
