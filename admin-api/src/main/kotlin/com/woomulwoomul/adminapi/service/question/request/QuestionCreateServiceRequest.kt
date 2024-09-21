package com.woomulwoomul.adminapi.service.question.request

import com.woomulwoomul.core.domain.question.CategoryEntity
import com.woomulwoomul.core.domain.question.QuestionCategoryEntity
import com.woomulwoomul.core.domain.question.QuestionEntity
import com.woomulwoomul.core.domain.user.UserEntity
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class QuestionCreateServiceRequest(
    @field:Size(min = 1, max = 60, message = "질문 내용은 1자 ~ 60자 이내로 입력해 주세요.")
    var questionText: String,
    @field:Size(min = 6, max = 6, message = "질문 배경 색상은 6자만 가능합니다.")
    var questionBackgroundColor: String,
    @field:Size(min = 1, max = 3, message = "카테고리는 1개 ~ 3개 이내로 입력해 주세요.")
    var categoryNames: List<String>,

    val questionStartDateTime: LocalDateTime?,
    val questionEndDateTime: LocalDateTime?,
) {

    fun toQuestionEntity(user: UserEntity):QuestionEntity {
        return QuestionEntity(
            user = user,
            text = questionText,
            backgroundColor = questionBackgroundColor,
            startDateTime = questionStartDateTime,
            endDateTime = questionEndDateTime
        )
    }

    fun toQuestionCategoryEntity(question: QuestionEntity, category: CategoryEntity): QuestionCategoryEntity {
        return QuestionCategoryEntity(question = question, category =  category)
    }
}
