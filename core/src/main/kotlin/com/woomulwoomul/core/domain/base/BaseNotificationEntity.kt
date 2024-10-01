package com.woomulwoomul.core.domain.base

import com.woomulwoomul.core.domain.base.NotificationServiceStatus.UNREAD
import jakarta.persistence.Column
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.MappedSuperclass
import java.time.LocalDateTime

@MappedSuperclass
abstract class BaseNotificationEntity(
    @Column(nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    var status: NotificationServiceStatus = UNREAD,

    createDateTime: LocalDateTime? = null,
    updateDateTime: LocalDateTime? = null,
) : BaseTimeEntity(createDateTime, updateDateTime)