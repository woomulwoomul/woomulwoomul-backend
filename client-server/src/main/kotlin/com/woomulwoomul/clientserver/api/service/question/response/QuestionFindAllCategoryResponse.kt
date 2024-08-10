package com.woomulwoomul.clientserver.api.service.question.response

import com.woomulwoomul.core.domain.question.CategoryEntity

data class QuestionFindAllCategoryResponse (
    val categoryId: Long,
    val name: String,
) {

    constructor(category: CategoryEntity): this(category.id ?: 0, category.name)
}
