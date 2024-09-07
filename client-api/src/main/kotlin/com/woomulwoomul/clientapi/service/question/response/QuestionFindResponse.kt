package com.woomulwoomul.clientapi.service.question.response

import com.woomulwoomul.core.domain.question.CategoryEntity
import com.woomulwoomul.core.domain.question.QuestionEntity

data class QuestionFindResponse(
    val questionId: Long,
    val questionText: String,
    val backgroundColor: String,
    val categories: List<QuestionFindCategoryResponse>,
    val userId: Long,
) {
    constructor(question: QuestionEntity, categories: Set<CategoryEntity>): this(
        question.id ?: 0,
        question.text,
        question.backgroundColor,
        categories.ifEmpty { listOf() }
            .map { QuestionFindCategoryResponse(it) },
        question.user.id ?: 0
    )
}

data class QuestionFindCategoryResponse(
    val categoryId: Long,
    val categoryName: String,
) {
    constructor(category: CategoryEntity): this(category.id ?: 0, category.name)
}