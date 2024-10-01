package com.woomulwoomul.adminapi.service.question.request

import com.woomulwoomul.core.domain.question.CategoryEntity
import com.woomulwoomul.core.domain.user.UserEntity
import jakarta.validation.constraints.Size

data class CategoryCreateServiceRequest(

    @field:Size(min = 1, max = 10, message = "카테고리명은 1자 ~ 10자 이내로 입력해 주세요.")
    val categoryName: String,
) {

    fun toCategoryEntity(admin: UserEntity): CategoryEntity {
        return CategoryEntity(admin = admin, name = categoryName)
    }
}
