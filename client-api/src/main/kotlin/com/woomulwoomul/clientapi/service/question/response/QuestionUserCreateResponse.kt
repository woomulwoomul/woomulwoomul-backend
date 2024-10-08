package com.woomulwoomul.clientapi.service.question.response

import com.woomulwoomul.core.domain.question.CategoryEntity
import com.woomulwoomul.core.domain.question.QuestionEntity

data class QuestionUserCreateResponse(
    val questionId: Long,
    val questionText: String,
    val questionBackgroundColor: String,
    val categories: List<QuestionUserCreateCategoryResponse>
) {

    constructor(question: QuestionEntity, categories: List<CategoryEntity>): this(
        question.id ?: 0,
        question.text,
        question.backgroundColor.value,
        categories.ifEmpty { listOf() }
            .map { QuestionUserCreateCategoryResponse(it) }
            .toList()
    )
}

data class QuestionUserCreateCategoryResponse(
    val categoryId: Long,
    val categoryName: String,
) {
    constructor(category: CategoryEntity): this(category.id ?: 0, category.name)
}
