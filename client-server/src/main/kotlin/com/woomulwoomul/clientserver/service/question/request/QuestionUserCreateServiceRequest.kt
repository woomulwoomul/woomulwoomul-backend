package com.woomulwoomul.clientserver.service.question.request

import com.woomulwoomul.core.domain.question.CategoryEntity
import com.woomulwoomul.core.domain.question.QuestionCategoryEntity
import com.woomulwoomul.core.domain.question.QuestionEntity
import com.woomulwoomul.core.domain.user.UserEntity
import jakarta.validation.constraints.Size

data class QuestionUserCreateServiceRequest(
    @field:Size(min = 1, max = 60, message = "질문 내용은 1자 ~ 60자 이내로 입력해 주세요.")
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