package com.woomulwoomul.woomulwoomulbackend.domain.question.custom

import com.woomulwoomul.woomulwoomulbackend.common.request.PageRequest
import com.woomulwoomul.woomulwoomulbackend.common.response.PageData
import com.woomulwoomul.woomulwoomulbackend.domain.question.CategoryEntity

interface CategoryCustomRepository {

    fun findAll(pageRequest: PageRequest): PageData<CategoryEntity>

    fun findByIds(ids: List<Long>): List<CategoryEntity>
}