package com.woomulwoomul.adminapi.service.question.request

import com.woomulwoomul.core.common.constant.RegexConstants.Companion.BACKGROUND_COLOR_CODE
import com.woomulwoomul.core.common.constant.RegexConstants.Companion.SERVICE_STATUS
import com.woomulwoomul.core.domain.question.CategoryEntity
import com.woomulwoomul.core.domain.question.QuestionCategoryEntity
import com.woomulwoomul.core.domain.question.QuestionEntity
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class QuestionUpdateServiceRequest(
    @field:Size(min = 1, max = 60, message = "질문 내용은 1자 ~ 60자 이내로 입력해 주세요.")
    var questionText: String,
    @field:Pattern(regexp = BACKGROUND_COLOR_CODE, message = "질문 배경 색상은 '#FFACA8', '#FFA34F', '#FFC34F', " +
            "'#C5FFAA', '#1AE7D8', '#4FB5FF', '#868BFF', '#C58AFF', '#FF9CE3', '#FFFFFF' 중 하나만 가능합니다.")
    var questionBackgroundColor: String,
    @field:Size(min = 1, max = 3, message = "카테고리는 1개 ~ 3개 이내로 입력해 주세요.")
    var categoryNames: List<String>,

    val questionStartDateTime: LocalDateTime?,
    val questionEndDateTime: LocalDateTime?,

    @field:Pattern(regexp = SERVICE_STATUS, message = "질문 상태는 'ACTIVE', 'USER_DEL', 또는 'ADMIN_DEL'만 가능합니다.")
    var questionStatus: String
) {

    fun toQuestionCategoryEntity(question: QuestionEntity, category: CategoryEntity): QuestionCategoryEntity {
        return QuestionCategoryEntity(question = question, category = category)
    }
}
