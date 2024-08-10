package com.woomulwoomul.core.domain.user.custom

import com.woomulwoomul.core.domain.user.UserProviderEntity

interface UserProviderCustomRepository {

    fun findInnerFetchJoinUser(providerId: String): UserProviderEntity?
}