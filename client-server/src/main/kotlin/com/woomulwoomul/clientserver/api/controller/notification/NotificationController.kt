package com.woomulwoomul.clientserver.api.controller.notification

import com.woomulwoomul.clientserver.api.service.notification.NotificationService
import com.woomulwoomul.clientserver.api.service.notification.response.NotificationGetAllResponse
import com.woomulwoomul.core.common.constant.SuccessCode.NOTIFICATIONS_FOUND
import com.woomulwoomul.core.common.constant.SuccessCode.NOTIFICATION_READ
import com.woomulwoomul.core.common.request.PageRequest
import com.woomulwoomul.core.common.response.DefaultPageResponse
import com.woomulwoomul.core.common.response.DefaultResponse
import com.woomulwoomul.core.common.utils.UserUtils
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.security.Principal
import java.time.LocalDateTime

@Validated
@RestController
class NotificationController(
    private val notificationService: NotificationService,
) {

    @GetMapping("/api/notification")
    fun getAllNotifications(@RequestParam(name = "page-from", required = false) pageFrom: Long?,
                            @RequestParam(name = "page-size", required = false) pageSize: Long?,
                            principal: Principal): ResponseEntity<DefaultPageResponse<NotificationGetAllResponse>> {
        val response = notificationService.getAllNotification(UserUtils.getUserId(principal),
            PageRequest.of(pageFrom, pageSize),
            LocalDateTime.now())

        return DefaultPageResponse.toResponseEntity(NOTIFICATIONS_FOUND, response)
    }

    @PatchMapping("/api/notification/{notificationId}")
    fun readNotification(@PathVariable notificationId: Long, principal: Principal): ResponseEntity<DefaultResponse> {
        notificationService.readNotification(UserUtils.getUserId(principal), notificationId)

        return DefaultResponse.toResponseEntity(NOTIFICATION_READ)
    }
}