package com.woomulwoomul.woomulwoomulbackend.domain.question.custom

import com.woomulwoomul.woomulwoomulbackend.common.response.PageData
import com.woomulwoomul.woomulwoomulbackend.domain.question.CategoryEntity

interface CategoryCustomRepository {

    fun findAll(pageFrom: Long, pageSize: Long): PageData<CategoryEntity>
}