package com.woomulwoomul.woomulwoomulbackend.domain.user.custom

import com.woomulwoomul.woomulwoomulbackend.domain.user.UserProviderEntity

interface UserProviderCustomRepository {

    fun findInnerFetchJoinUser(providerId: String): UserProviderEntity?
}