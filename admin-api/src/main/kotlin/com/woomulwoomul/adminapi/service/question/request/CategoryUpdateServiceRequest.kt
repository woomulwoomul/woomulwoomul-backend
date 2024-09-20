package com.woomulwoomul.adminapi.service.question.request

import com.woomulwoomul.core.common.constant.RegexConstants.Companion.ACTIVE_OR_ADMIN_DEL
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class CategoryUpdateServiceRequest(

    @field:Size(min = 1, max = 10, message = "카테고리명은 1자 ~ 10자 이내로 입력해 주세요.")
    val categoryName: String,

    @field:Pattern(regexp = ACTIVE_OR_ADMIN_DEL, message = "카테고리 상태는 'ACTIVE' 또는 'ADMIN_DEL'만 가능합니다.")
    val categoryStatus: String,
)
