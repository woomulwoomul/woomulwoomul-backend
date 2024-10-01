package com.woomulwoomul.core.domain.notification

import com.woomulwoomul.core.domain.notification.custom.NotificationCustomRepository
import org.springframework.data.jpa.repository.JpaRepository

interface NotificationRepository : JpaRepository<NotificationEntity, Long>, NotificationCustomRepository {
}