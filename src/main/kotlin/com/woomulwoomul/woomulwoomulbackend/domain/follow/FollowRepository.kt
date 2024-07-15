package com.woomulwoomul.woomulwoomulbackend.domain.follow

import com.woomulwoomul.woomulwoomulbackend.domain.follow.custom.FollowCustomRepository
import org.springframework.data.jpa.repository.JpaRepository

interface FollowRepository : JpaRepository<FollowEntity, Long>, FollowCustomRepository {
}