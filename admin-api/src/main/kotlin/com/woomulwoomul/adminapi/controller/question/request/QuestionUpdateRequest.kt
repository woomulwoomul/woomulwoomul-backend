package com.woomulwoomul.adminapi.controller.question.request

import com.woomulwoomul.adminapi.service.question.request.QuestionUpdateServiceRequest
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

    @field:DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val questionStartDateTime: LocalDateTime?,
    @field:DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val questionEndDateTime: LocalDateTime?,

    @field:NotBlank
    val questionStatus: String?
) {

    fun toServiceRequest(): QuestionUpdateServiceRequest {
        return QuestionUpdateServiceRequest(
            questionText ?: "",
            questionBackgroundColor ?: "",
            categoryNames ?: emptyList(),
            questionStartDateTime,
            questionEndDateTime,
            questionStatus ?: ""
        )
    }
}
