package com.woomulwoomul.core.domain.user

import com.woomulwoomul.core.domain.user.custom.UserLoginCustomRepository
import org.springframework.data.jpa.repository.JpaRepository

interface UserLoginRepository : JpaRepository<UserLoginEntity, Long>, UserLoginCustomRepository {
}