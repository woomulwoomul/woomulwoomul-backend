package com.woomulwoomul.woomulwoomulbackend.api.service.question.request

import com.woomulwoomul.woomulwoomulbackend.common.exception.ByteSize
import com.woomulwoomul.woomulwoomulbackend.domain.question.CategoryEntity
import com.woomulwoomul.woomulwoomulbackend.domain.question.QuestionCategoryEntity
import com.woomulwoomul.woomulwoomulbackend.domain.question.QuestionEntity
import com.woomulwoomul.woomulwoomulbackend.domain.user.UserEntity

data class QuestionUserCreateServiceRequest(
    @field:ByteSize(min = 1, max = 60, message = "질문 내용은 1~60 바이트만 가능합니다.")
    var text: String,
    @field:ByteSize(min = 6, max = 6, message = "질문 배경색상은 1~6 바이트만 가능합니다.")
    var backgroundColor: String,
    var categoryIds: List<Long>
) {

    fun toQuestionEntity(user: UserEntity): QuestionEntity {
        return QuestionEntity(user = user, text = text, backgroundColor = backgroundColor)
    }

    fun toQuestionCategories(question: QuestionEntity, categories: List<CategoryEntity>): List<QuestionCategoryEntity> {
        return categories.stream()
            .map { QuestionCategoryEntity(question = question, category = it) }
            .toList()
    }
}