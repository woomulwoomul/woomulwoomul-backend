package com.woomulwoomul.core.domain.user

import com.woomulwoomul.core.domain.user.custom.UserRoleCustomRepository
import org.springframework.data.jpa.repository.JpaRepository

interface UserRoleRepository : JpaRepository<UserRoleEntity, Long>, UserRoleCustomRepository {
}