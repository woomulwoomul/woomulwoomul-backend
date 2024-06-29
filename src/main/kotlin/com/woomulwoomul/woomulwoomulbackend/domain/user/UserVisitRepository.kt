package com.woomulwoomul.woomulwoomulbackend.domain.user

import org.springframework.data.jpa.repository.JpaRepository

interface UserVisitRepository : JpaRepository<UserVisitEntity, Long> {
}