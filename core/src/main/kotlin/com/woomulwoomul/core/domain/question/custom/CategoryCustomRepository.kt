package com.woomulwoomul.core.domain.question.custom

import com.woomulwoomul.core.common.request.PageCursorRequest
import com.woomulwoomul.core.common.request.PageOffsetRequest
import com.woomulwoomul.core.common.response.PageData
import com.woomulwoomul.core.domain.question.CategoryEntity

interface CategoryCustomRepository {

    fun findAll(pageCursorRequest: PageCursorRequest): PageData<CategoryEntity>

    fun findAll(pageOffsetRequest: PageOffsetRequest): PageData<CategoryEntity>

    fun findByIds(ids: List<Long>): List<CategoryEntity>
}