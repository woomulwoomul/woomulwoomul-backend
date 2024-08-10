package com.woomulwoomul.clientserver.api.service.notification.response

import com.woomulwoomul.core.common.utils.DateTimeUtils
import com.woomulwoomul.core.domain.notification.NotificationEntity
import com.woomulwoomul.core.domain.notification.NotificationType
import com.woomulwoomul.core.domain.user.UserEntity
import java.time.LocalDateTime

data class NotificationGetAllResponse(
    val notificationId: Long,
    val notificationType: NotificationType,
    val notificationTitle: String,
    val notificationContext: String,
    val notificationLink: String,
    val notificationDateTime: String,
    val sender: NotificationGetAllSenderResponse,
) {

    constructor(notification: NotificationEntity, now: LocalDateTime): this(
        notification.id ?: 0,
        notification.type,
        notification.title,
        notification.context,
        notification.link ?: "",
        DateTimeUtils.getDurationDifference(notification.createDateTime!!, now),
        NotificationGetAllSenderResponse(notification.senderUser ?: notification.senderAdmin!!)
    )
}

data class NotificationGetAllSenderResponse(
    val userId: Long,
    val userNickname: String,
    val userImageUrl: String
) {

    constructor(user: UserEntity): this(user.id ?: 0, user.nickname, user.imageUrl)
}
