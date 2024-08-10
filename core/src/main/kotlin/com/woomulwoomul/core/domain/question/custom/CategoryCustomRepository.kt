package com.woomulwoomul.core.domain.question.custom

import com.woomulwoomul.core.common.request.PageRequest
import com.woomulwoomul.core.common.response.PageData
import com.woomulwoomul.core.domain.question.CategoryEntity

interface CategoryCustomRepository {

    fun findAll(pageRequest: PageRequest): PageData<CategoryEntity>

    fun findByIds(ids: List<Long>): List<CategoryEntity>
}