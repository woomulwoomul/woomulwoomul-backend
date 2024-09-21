package com.woomulwoomul.adminapi.controller.question.request

import com.woomulwoomul.adminapi.service.question.request.QuestionCreateServiceRequest
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime

data class QuestionCreateRequest(
    @field:NotBlank
    val questionText: String?,
    @field:NotBlank
    val questionBackgroundColor: String?,
    @field:NotNull
    val categoryNames: List<String>?,
    @field:DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val questionStartDateTime: LocalDateTime?,
    @field:DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val questionEndDateTime: LocalDateTime?,
) {

    fun toServiceRequest(): QuestionCreateServiceRequest {
        return QuestionCreateServiceRequest(
            questionText ?: "",
            questionBackgroundColor ?: "",
            categoryNames ?: emptyList(),
            questionStartDateTime,
            questionEndDateTime
        )
    }
}
