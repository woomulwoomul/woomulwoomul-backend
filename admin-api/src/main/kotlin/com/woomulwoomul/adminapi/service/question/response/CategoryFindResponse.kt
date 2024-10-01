package com.woomulwoomul.adminapi.service.question.response

import com.woomulwoomul.core.domain.base.ServiceStatus
import com.woomulwoomul.core.domain.question.CategoryEntity
import java.time.LocalDateTime

data class CategoryFindResponse(
    val id: Long,
    val name: String,
    val adminNickname: String,
    val status: ServiceStatus,
    val createDateTime: LocalDateTime,
    val updateDateTime: LocalDateTime,

    val availableStatuses: List<ServiceStatus> = listOf(ServiceStatus.ACTIVE, ServiceStatus.ADMIN_DEL)
) {

    constructor(category: CategoryEntity): this(
        category.id ?: 0,
        category.name,
        category.admin.nickname,
        category.status,
        category.createDateTime!!,
        category.updateDateTime!!
    )
}
