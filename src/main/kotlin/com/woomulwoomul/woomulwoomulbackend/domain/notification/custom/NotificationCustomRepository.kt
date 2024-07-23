package com.woomulwoomul.woomulwoomulbackend.domain.notification.custom

import com.woomulwoomul.woomulwoomulbackend.common.request.PageRequest
import com.woomulwoomul.woomulwoomulbackend.common.response.PageData
import com.woomulwoomul.woomulwoomulbackend.domain.notification.NotificationEntity

interface NotificationCustomRepository {

    fun findAll(userId: Long, pageRequest: PageRequest): PageData<NotificationEntity>
}