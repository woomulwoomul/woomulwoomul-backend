package com.woomulwoomul.woomulwoomulbackend.api.service.notification

import com.woomulwoomul.woomulwoomulbackend.api.service.notification.response.NotificationGetAllResponse
import com.woomulwoomul.woomulwoomulbackend.common.constant.ExceptionCode
import com.woomulwoomul.woomulwoomulbackend.common.constant.ExceptionCode.*
import com.woomulwoomul.woomulwoomulbackend.common.request.PageRequest
import com.woomulwoomul.woomulwoomulbackend.common.response.CustomException
import com.woomulwoomul.woomulwoomulbackend.common.response.PageData
import com.woomulwoomul.woomulwoomulbackend.domain.notification.NotificationRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated
import java.time.LocalDateTime

@Service
@Validated
@Transactional(readOnly = true)
class NotificationService(
    private val notificationRepository: NotificationRepository,
) {

    /**
     * 알림 전체 조회
     * @param userId 회원 ID
     * @param pageRequest 페이징 요청
     * @param now 현재 시간
     * @return 알림 전체 조회 응답
     */
    fun getAllNotification(userId: Long, pageRequest: PageRequest, now: LocalDateTime):
            PageData<NotificationGetAllResponse> {
        val notifications = notificationRepository.findAll(userId, pageRequest)

        return PageData(notifications.data.map { NotificationGetAllResponse(it, now) }, notifications.total)
    }

    /**
     * 알림 읽음 처리
     * @param userId 회원 ID
     * @param notificationId 알림 ID
     * @throws NOTIFICATION_NOT_FOUND 404
     */
    @Transactional
    fun readNotification(userId: Long, notificationId: Long) {
        notificationRepository.findByNotificationIdAndUserId(notificationId, userId)?.read()
            ?: throw CustomException(NOTIFICATION_NOT_FOUND)
    }
}