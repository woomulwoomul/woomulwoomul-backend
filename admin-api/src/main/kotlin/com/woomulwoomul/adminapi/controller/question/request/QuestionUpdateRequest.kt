package com.woomulwoomul.adminapi.controller.question.request

import com.woomulwoomul.adminapi.service.question.request.QuestionUpdateServiceRequest
import com.woomulwoomul.core.common.utils.DateTimeUtils
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime

data class QuestionUpdateRequest(
    @field:NotBlank
    val questionText: String?,
    @field:NotBlank
    val questionBackgroundColor: String?,

    @field:NotNull
    val categoryNames: List<String>?,

    val questionStartDateTime: String?,
    val questionEndDateTime: String?,

    @field:NotBlank
    val questionStatus: String?
) {

    fun toServiceRequest(): QuestionUpdateServiceRequest {
        return QuestionUpdateServiceRequest(
            questionText ?: "",
            questionBackgroundColor ?: "",
            categoryNames ?: emptyList(),
            DateTimeUtils.toLocalDateTime(questionStartDateTime),
            DateTimeUtils.toLocalDateTime(questionEndDateTime),
            questionStatus ?: ""
        )
    }
}
