package com.woomulwoomul.adminapi.service.question.response

import com.woomulwoomul.core.common.constant.BackgroundColor
import com.woomulwoomul.core.domain.question.CategoryEntity

data class QuestionCreateResponse(
    val availableCategoryNames: List<String>,
    val availableBackgroundColors: List<BackgroundColor> = BackgroundColor.entries
) {

    constructor(categories: List<CategoryEntity>): this(categories.map(CategoryEntity::name))
}