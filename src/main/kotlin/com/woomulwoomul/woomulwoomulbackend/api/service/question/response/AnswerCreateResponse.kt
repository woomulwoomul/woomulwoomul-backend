package com.woomulwoomul.woomulwoomulbackend.api.service.question.response

import com.woomulwoomul.woomulwoomulbackend.domain.question.CategoryEntity
import com.woomulwoomul.woomulwoomulbackend.domain.question.QuestionEntity
import com.woomulwoomul.woomulwoomulbackend.domain.user.UserEntity

data class AnswerCreateResponse(
    val userId: Long,
    val userNickname: String,
    val questionId: Long,
    val questionText: String,
    val questionBackgroundColor: String,
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
