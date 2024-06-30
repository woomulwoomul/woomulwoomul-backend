package com.woomulwoomul.woomulwoomulbackend.api.service.question.response

import com.woomulwoomul.woomulwoomulbackend.domain.question.CategoryEntity

data class QuestionFindAllCategoryResponse (
    val categoryId: Long,
    val name: String,
) {

    constructor(category: CategoryEntity): this(category.id ?: 0, category.name)
}
