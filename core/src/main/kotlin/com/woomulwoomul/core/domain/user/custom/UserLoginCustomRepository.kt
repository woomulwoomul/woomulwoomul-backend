package com.woomulwoomul.core.domain.user.custom

import com.woomulwoomul.core.common.request.PageOffsetRequest
import com.woomulwoomul.core.common.response.PageData
import com.woomulwoomul.core.domain.user.UserLoginEntity

interface UserLoginCustomRepository {
    fun findAll(pageOffsetRequest: PageOffsetRequest): PageData<UserLoginEntity>
}