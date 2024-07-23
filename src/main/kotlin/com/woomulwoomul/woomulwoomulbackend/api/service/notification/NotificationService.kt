package com.woomulwoomul.woomulwoomulbackend.api.service.notification

import com.woomulwoomul.woomulwoomulbackend.api.service.notification.response.NotificationGetAllResponse
import com.woomulwoomul.woomulwoomulbackend.common.request.PageRequest
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
}