package com.woomulwoomul.adminapi.controller.question.request

import com.woomulwoomul.adminapi.service.question.request.CategoryCreateServiceRequest
import jakarta.validation.constraints.NotBlank

data class CategoryCreateRequest(

    @field:NotBlank
    val categoryName: String?,
) {

    fun toServiceRequest(): CategoryCreateServiceRequest {
        return CategoryCreateServiceRequest(categoryName ?: "")
    }

}
