package com.woomulwoomul.core.domain.user.custom

import com.woomulwoomul.core.domain.user.UserRoleEntity

interface UserRoleCustomRepository {

    fun findAllFetchUser(userId: Long): List<UserRoleEntity>

}
