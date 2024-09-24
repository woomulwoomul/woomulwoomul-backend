package com.woomulwoomul.clientapi.service.question.response

import com.woomulwoomul.core.common.constant.BackgroundColor
import com.woomulwoomul.core.domain.question.CategoryEntity
import com.woomulwoomul.core.domain.question.QuestionEntity
import com.woomulwoomul.core.domain.user.UserEntity

data class AnswerCreateResponse(
    val userId: Long,
    val userNickname: String,
    val questionId: Long,
    val questionText: String,
    val questionBackgroundColor: BackgroundColor,
    val categories: List<AnswerCreateCategoryResponse>
) {
    constructor(user: UserEntity, question: QuestionEntity, categories: Set<CategoryEntity>): this(
        user.id ?: 0,
        user.nickname,
        question.id ?: 0,
        question.text,
        question.backgroundColor,
        categories.ifEmpty { listOf() }
            .map { AnswerCreateCategoryResponse(it) }
    )
}

data class AnswerCreateCategoryResponse(
    val categoryId: Long,
    val name: String,
) {
    constructor(category: CategoryEntity): this(category.id ?: 0, category.name)
}
