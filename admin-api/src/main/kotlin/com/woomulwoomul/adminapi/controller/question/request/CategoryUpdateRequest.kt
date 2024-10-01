package com.woomulwoomul.adminapi.controller.question.request

import com.woomulwoomul.adminapi.service.question.request.CategoryUpdateServiceRequest
import jakarta.validation.constraints.NotBlank

data class CategoryUpdateRequest(

    @field:NotBlank
    val categoryName: String?,

    @field:NotBlank
    val categoryStatus: String?,
) {

    fun toServiceRequest(): CategoryUpdateServiceRequest {
        return CategoryUpdateServiceRequest(categoryName ?: "", categoryStatus ?: "")
    }
}
