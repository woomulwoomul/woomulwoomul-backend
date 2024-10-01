package com.woomulwoomul.core.domain.notification.custom

import com.woomulwoomul.core.common.request.PageCursorRequest
import com.woomulwoomul.core.common.response.PageData
import com.woomulwoomul.core.domain.notification.NotificationEntity

interface NotificationCustomRepository {

    fun findAll(userId: Long, pageCursorRequest: PageCursorRequest): PageData<NotificationEntity>

    fun findByNotificationIdAndUserId(notificationId: Long, userId: Long): NotificationEntity?
}