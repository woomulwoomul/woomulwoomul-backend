package com.woomulwoomul.clientapi.service.question.request

import com.woomulwoomul.core.domain.question.BackgroundColor
import com.woomulwoomul.core.common.constant.RegexConstants.Companion.BACKGROUND_COLOR_CODE
import com.woomulwoomul.core.domain.question.CategoryEntity
import com.woomulwoomul.core.domain.question.QuestionCategoryEntity
import com.woomulwoomul.core.domain.question.QuestionEntity
import com.woomulwoomul.core.domain.user.UserEntity
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class QuestionUserCreateServiceRequest(
    @field:Size(min = 1, max = 60, message = "질문 내용은 1자 ~ 60자 이내로 입력해 주세요.")
    var questionText: String,
    @field:Pattern(regexp = BACKGROUND_COLOR_CODE, message = "질문 배경 색상은 '#FFACA8', '#FFA34F', '#FFC34F', " +
            "'#C5FFAA', '#1AE7D8', '#4FB5FF', '#868BFF', '#C58AFF', '#FF9CE3', '#FFFFFF' 중 하나만 가능합니다.")
    var questionBackgroundColor: String,
    @field:Size(min = 1, max = 3, message = "질문 카테고리는 1개 ~ 3개 이내로 입력해 주세요.")
    var categoryIds: List<Long>
) {

    fun toQuestionEntity(user: UserEntity): QuestionEntity {
        return QuestionEntity(user = user, text = questionText, backgroundColor = BackgroundColor.of(questionBackgroundColor))
    }

    fun toQuestionCategories(question: QuestionEntity, categories: List<CategoryEntity>): List<QuestionCategoryEntity> {
        return categories.stream()
            .map { QuestionCategoryEntity(question = question, category = it) }
            .toList()
    }
}