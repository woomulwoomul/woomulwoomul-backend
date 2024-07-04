package com.woomulwoomul.woomulwoomulbackend.domain.notification

import com.woomulwoomul.woomulwoomulbackend.domain.base.BaseNotificationEntity
import com.woomulwoomul.woomulwoomulbackend.domain.user.UserEntity
import jakarta.persistence.*

@Table(name = "notification")
@Entity
class NotificationEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    val receiver: UserEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_user_id")
    val senderUser: UserEntity? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_admin_id")
    val senderAdmin: UserEntity? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    val type: NotificationType,
    @Column(nullable = false, length = 30)
    val title: String,
    @Column(nullable = false, length = 100)
    val context: String,
    @Column(length = 500)
    val link: String? = null,
) : BaseNotificationEntity()