package com.woomulwoomul.clientserver.api.controller.question.request

import com.woomulwoomul.clientserver.api.service.question.request.QuestionUserCreateServiceRequest
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class QuestionUserCreateRequest(
    @field:NotBlank(message = "질문은 필수 입력입니다.")
    var questionText: String?,
    @field:NotBlank(message = "질문 배경 색상은 필수 입력입니다.")
    var questionBackgroundColor: String?,

    @field:NotNull(message = "카테고리 ID는 필수 입력입니다.")
    var categoryIds: List<Long>?
) {

    fun toServiceRequest(): QuestionUserCreateServiceRequest {
        return QuestionUserCreateServiceRequest(questionText!!, questionBackgroundColor!!, categoryIds!!)
    }
}
