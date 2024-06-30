package com.woomulwoomul.woomulwoomulbackend.api.service.question.response

import com.woomulwoomul.woomulwoomulbackend.domain.question.CategoryEntity
import com.woomulwoomul.woomulwoomulbackend.domain.question.QuestionAnswerEntity

data class AnswerFindAllResponse(
    val answerId: Long,
    val questionId: Long,
    val backgroundColor: String,
    val categories: List<AnswerFindAllCategoryResponse>
) {
    constructor(questionAnswer: QuestionAnswerEntity, categories: List<CategoryEntity>): this(
        questionAnswer.answer!!.id!!,
        questionAnswer.question.id!!,
        questionAnswer.question.backgroundColor,
        categories.map { AnswerFindAllCategoryResponse(it) }
    )
}

data class AnswerFindAllCategoryResponse(
    val categoryId: Long,
    val name: String,
) {
    constructor(category: CategoryEntity): this(category.id!!, category.name)
}
