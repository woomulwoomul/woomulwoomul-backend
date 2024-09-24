package com.woomulwoomul.adminapi.service.question.response

import com.woomulwoomul.core.domain.base.ServiceStatus
import com.woomulwoomul.core.domain.question.CategoryEntity
import java.time.LocalDateTime

data class CategoryFindAllResponse (
    val id: Long,
    val name: String,
    val status: ServiceStatus,
    val createDateTime: LocalDateTime,
    val updateDateTime: LocalDateTime,
) {

    constructor(category: CategoryEntity): this(
        category.id ?: 0,
        category.name,
        category.status,
        category.createDateTime!!,
        category.updateDateTime!!
    )
}
