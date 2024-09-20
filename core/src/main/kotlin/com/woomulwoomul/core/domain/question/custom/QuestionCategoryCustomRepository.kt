package com.woomulwoomul.core.domain.question.custom

import com.woomulwoomul.core.domain.base.ServiceStatus
import com.woomulwoomul.core.domain.question.QuestionCategoryEntity
import java.time.LocalDateTime

interface QuestionCategoryCustomRepository {

    fun findByQuestionIds(questionIds: List<Long>): List<QuestionCategoryEntity>

    fun findByQuestionId(questionId: Long,
                         statuses: List<ServiceStatus> = listOf(ServiceStatus.ACTIVE)): List<QuestionCategoryEntity>

    fun findAdmin(now: LocalDateTime): List<QuestionCategoryEntity>
}