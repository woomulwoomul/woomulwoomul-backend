package com.woomulwoomul.woomulwoomulbackend.domain.notification.custom

import com.querydsl.jpa.impl.JPAQueryFactory
import com.woomulwoomul.woomulwoomulbackend.common.request.PageRequest
import com.woomulwoomul.woomulwoomulbackend.common.response.PageData
import com.woomulwoomul.woomulwoomulbackend.domain.base.NotificationServiceStatus.READ
import com.woomulwoomul.woomulwoomulbackend.domain.base.NotificationServiceStatus.UNREAD
import com.woomulwoomul.woomulwoomulbackend.domain.base.ServiceStatus.ACTIVE
import com.woomulwoomul.woomulwoomulbackend.domain.notification.NotificationEntity
import com.woomulwoomul.woomulwoomulbackend.domain.notification.QNotificationEntity.notificationEntity
import com.woomulwoomul.woomulwoomulbackend.domain.user.QUserEntity
import org.springframework.stereotype.Repository

@Repository
class NotificationRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : NotificationCustomRepository {

    override fun findAll(userId: Long, pageRequest: PageRequest): PageData<NotificationEntity> {
        val receiver = QUserEntity("receiver")
        val senderUser = QUserEntity("senderUser")
        val senderAdmin = QUserEntity("senderAdmin")

        val total = queryFactory
            .select(notificationEntity.id.count())
            .from(notificationEntity)
            .innerJoin(receiver)
            .on(receiver.id.eq(notificationEntity.receiver.id)
                .and(receiver.id.eq(userId))
                .and(receiver.status.eq(ACTIVE)))
            .leftJoin(senderUser)
            .on(senderUser.id.eq(notificationEntity.senderUser.id))
            .leftJoin(senderAdmin)
            .on(senderAdmin.id.eq(notificationEntity.senderAdmin.id))
            .where(notificationEntity.status.`in`(READ, UNREAD))
            .fetchFirst() ?: 0L

        if (total == 0L) return PageData(emptyList(), total)

        val data = queryFactory
            .selectFrom(notificationEntity)
            .innerJoin(receiver)
            .on(receiver.id.eq(notificationEntity.receiver.id)
                .and(receiver.status.eq(ACTIVE))
                .and(receiver.id.eq(userId)))
            .leftJoin(senderUser)
            .on(senderUser.id.eq(notificationEntity.senderUser.id))
            .fetchJoin()
            .leftJoin(senderAdmin)
            .on(senderAdmin.id.eq(notificationEntity.senderAdmin.id))
            .fetchJoin()
            .where(
                notificationEntity.status.`in`(READ, UNREAD),
                notificationEntity.id.loe(pageRequest.from)
            ).limit(pageRequest.size)
            .orderBy(notificationEntity.id.desc())
            .fetch()

        return PageData(data, total)
    }
}