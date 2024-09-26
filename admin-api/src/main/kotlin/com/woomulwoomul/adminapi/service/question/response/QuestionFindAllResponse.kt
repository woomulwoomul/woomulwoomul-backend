package com.woomulwoomul.adminapi.service.question.response

import com.woomulwoomul.core.domain.question.BackgroundColor
import com.woomulwoomul.core.domain.base.ServiceStatus
import com.woomulwoomul.core.domain.question.CategoryEntity
import com.woomulwoomul.core.domain.question.QuestionEntity
import java.time.LocalDateTime

data class QuestionFindAllResponse(
    val id: Long,
    val text: String,
    val backgroundColor: BackgroundColor,
    val userNickname: String,
    val categoryNames: List<String>,
    val startDateTime: LocalDateTime?,
    val endDateTime: LocalDateTime?,
    val status: ServiceStatus,
    val createDateTime: LocalDateTime,
    val updateDateTime: LocalDateTime,
) {

    constructor(question: QuestionEntity, category: List<CategoryEntity>): this(
        question.id ?: 0,
        question.text,
        question.backgroundColor,
        question.user.nickname,
        category.map { it.name },
        question.startDateTime,
        question.endDateTime,
        question.status,
        question.createDateTime!!,
        question.updateDateTime!!
    )
}
