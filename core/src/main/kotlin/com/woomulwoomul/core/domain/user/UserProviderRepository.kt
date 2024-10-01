package com.woomulwoomul.core.domain.user

import com.woomulwoomul.core.domain.user.custom.UserProviderCustomRepository
import org.springframework.data.jpa.repository.JpaRepository

interface UserProviderRepository : JpaRepository<UserProviderEntity, Long>, UserProviderCustomRepository {
}