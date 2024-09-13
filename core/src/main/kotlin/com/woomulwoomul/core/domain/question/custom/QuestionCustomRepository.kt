package com.woomulwoomul.core.domain.question.custom

import com.woomulwoomul.core.common.request.PageOffsetRequest
import com.woomulwoomul.core.common.response.PageData
import com.woomulwoomul.core.domain.question.QuestionEntity
import java.time.LocalDateTime

interface QuestionCustomRepository {

    fun findRandomAdminQuestionId(): Long?

    fun findAdminQuestionId(now: LocalDateTime): Long?

    fun findAll(pageOffsetRequest: PageOffsetRequest): PageData<QuestionEntity>
}