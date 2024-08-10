package com.woomulwoomul.core.domain.follow

import com.woomulwoomul.core.domain.follow.custom.FollowCustomRepository
import org.springframework.data.jpa.repository.JpaRepository

interface FollowRepository : JpaRepository<FollowEntity, Long>, FollowCustomRepository {
}