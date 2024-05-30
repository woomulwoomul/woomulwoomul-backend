package com.woomulwoomul.woomulwoomulbackend.domain.user.custom

import com.woomulwoomul.woomulwoomulbackend.domain.user.UserRole

interface UserRoleCustomRepository {

    fun findAllFetchUser(userId: Long): List<UserRole>

}
