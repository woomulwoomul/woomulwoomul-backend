package com.woomulwoomul.woomulwoomulbackend.domain.user

import com.woomulwoomul.woomulwoomulbackend.domain.user.custom.UserProviderCustomRepository
import org.springframework.data.jpa.repository.JpaRepository

interface UserProviderRepository : JpaRepository<UserProviderEntity, Long>, UserProviderCustomRepository {
}