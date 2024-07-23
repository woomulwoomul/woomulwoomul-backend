package com.woomulwoomul.woomulwoomulbackend.domain.notification

import com.woomulwoomul.woomulwoomulbackend.domain.notification.custom.NotificationCustomRepository
import org.springframework.data.jpa.repository.JpaRepository

interface NotificationRepository : JpaRepository<NotificationEntity, Long>, NotificationCustomRepository {
}