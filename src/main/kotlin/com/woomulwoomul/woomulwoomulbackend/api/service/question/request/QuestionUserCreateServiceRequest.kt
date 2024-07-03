package com.woomulwoomul.woomulwoomulbackend.api.service.question.request

import com.woomulwoomul.woomulwoomulbackend.domain.question.CategoryEntity
import com.woomulwoomul.woomulwoomulbackend.domain.question.QuestionCategoryEntity
import com.woomulwoomul.woomulwoomulbackend.domain.question.QuestionEntity
import com.woomulwoomul.woomulwoomulbackend.domain.user.UserEntity
import jakarta.validation.constraints.Size

data class QuestionUserCreateServiceRequest(
    @field:Size(min = 1, max = 60, message = "질문 내용은 1~60자만 가능합니다.")
    var questionText: String,
    @field:Size(min = 6, max = 6, message = "질문 배경 색상은 6자만 가능합니다.")
    var questionBackgroundColor: String,
    var categoryIds: List<Long>
) {

    fun toQuestionEntity(user: UserEntity): QuestionEntity {
        return QuestionEntity(user = user, text = questionText, backgroundColor = questionBackgroundColor)
    }

    fun toQuestionCategories(question: QuestionEntity, categories: List<CategoryEntity>): List<QuestionCategoryEntity> {
        return categories.stream()
            .map { QuestionCategoryEntity(question = question, category = it) }
            .toList()
    }
}