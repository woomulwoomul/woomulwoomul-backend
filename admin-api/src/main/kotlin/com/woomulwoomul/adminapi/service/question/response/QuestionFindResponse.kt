package com.woomulwoomul.adminapi.service.question.response

import com.woomulwoomul.core.domain.question.BackgroundColor
import com.woomulwoomul.core.domain.base.ServiceStatus
import com.woomulwoomul.core.domain.question.CategoryEntity
import com.woomulwoomul.core.domain.question.QuestionEntity
import com.woomulwoomul.core.domain.user.UserEntity
import java.time.LocalDateTime

data class QuestionFindResponse(
    val id: Long,
    val text: String,
    val backgroundColor: BackgroundColor,
    val userNickname: String,
    val categoryNames: List<String> = listOf(),
    val startDateTime: LocalDateTime?,
    val endDateTime: LocalDateTime?,
    val status: ServiceStatus,
    val createDateTime: LocalDateTime,
    val updateDateTime: LocalDateTime,

    val availableCategoryNames: List<String>,
    val availableBackgroundColors: List<BackgroundColor> = BackgroundColor.entries,
    val availableStatuses: List<ServiceStatus> = ServiceStatus.entries
) {

    constructor(
        question: QuestionEntity,
        user: UserEntity,
        categories: List<CategoryEntity>,
        availableCategories: List<CategoryEntity>,
    ): this(
        question.id ?: 0,
        question.text,
        question.backgroundColor,
        user.nickname,
        categories.map(CategoryEntity::name),
        question.startDateTime,
        question.endDateTime,
        question.status,
        question.createDateTime!!,
        question.updateDateTime!!,
        availableCategories.map(CategoryEntity::name)
    )
}
