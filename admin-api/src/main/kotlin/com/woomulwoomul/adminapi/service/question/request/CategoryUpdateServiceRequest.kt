package com.woomulwoomul.adminapi.service.question.request

import jakarta.validation.constraints.Size

data class CategoryUpdateServiceRequest(

    @field:Size(min = 1, max = 10, message = "카테고리명은 1자 ~ 10자 이내로 입력해 주세요.")
    val categoryName: String,
)
