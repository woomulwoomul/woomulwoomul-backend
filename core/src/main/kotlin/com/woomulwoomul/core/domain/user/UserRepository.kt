package com.woomulwoomul.core.domain.user

import com.woomulwoomul.core.domain.user.custom.UserCustomRepository
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<UserEntity, Long>, UserCustomRepository {
}