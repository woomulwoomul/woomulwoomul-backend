package com.woomulwoomul.adminapi.controller.question.request

import com.woomulwoomul.adminapi.service.question.request.QuestionCreateServiceRequest
import com.woomulwoomul.core.common.utils.DateTimeUtils
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class QuestionCreateRequest(
    @field:NotBlank
    val questionText: String?,
    @field:NotBlank
    val questionBackgroundColor: String?,
    @field:NotNull
    val categoryNames: List<String>?,
    @field:NotBlank
    val questionStartDateTime: String?,
    @field:NotBlank
    val questionEndDateTime: String?,
) {

    fun toServiceRequest(): QuestionCreateServiceRequest {
        return QuestionCreateServiceRequest(
            questionText ?: "",
            questionBackgroundColor ?: "",
            categoryNames ?: emptyList(),
            DateTimeUtils.toLocalDateTime(questionStartDateTime ?: ""),
            DateTimeUtils.toLocalDateTime(questionEndDateTime ?: "")
        )
    }
}
