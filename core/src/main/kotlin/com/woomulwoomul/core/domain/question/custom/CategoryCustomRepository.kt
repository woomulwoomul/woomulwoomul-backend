package com.woomulwoomul.core.domain.question.custom

import com.woomulwoomul.core.common.request.PageCursorRequest
import com.woomulwoomul.core.common.request.PageOffsetRequest
import com.woomulwoomul.core.common.response.PageData
import com.woomulwoomul.core.domain.base.ServiceStatus
import com.woomulwoomul.core.domain.base.ServiceStatus.ACTIVE
import com.woomulwoomul.core.domain.question.CategoryEntity

interface CategoryCustomRepository {

    fun findAll(pageCursorRequest: PageCursorRequest): PageData<CategoryEntity>

    fun findAll(pageOffsetRequest: PageOffsetRequest): PageData<CategoryEntity>

    fun find(categoryId: Long, statuses: List<ServiceStatus> = listOf(ACTIVE)): CategoryEntity?

    fun findByIds(ids: List<Long>): List<CategoryEntity>

    fun find(categoryName: String): CategoryEntity?

    fun exists(categoryName: String): Boolean
}