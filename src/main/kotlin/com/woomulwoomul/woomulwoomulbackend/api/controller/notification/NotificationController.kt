package com.woomulwoomul.woomulwoomulbackend.api.controller.notification

import com.woomulwoomul.woomulwoomulbackend.api.service.notification.NotificationService
import com.woomulwoomul.woomulwoomulbackend.api.service.notification.response.NotificationGetAllResponse
import com.woomulwoomul.woomulwoomulbackend.common.constant.SuccessCode.NOTIFICATIONS_FOUND
import com.woomulwoomul.woomulwoomulbackend.common.request.PageRequest
import com.woomulwoomul.woomulwoomulbackend.common.response.DefaultPageResponse
import com.woomulwoomul.woomulwoomulbackend.common.utils.UserUtils
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
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
}