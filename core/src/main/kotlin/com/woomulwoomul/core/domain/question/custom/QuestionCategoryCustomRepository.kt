package com.woomulwoomul.core.domain.question.custom

import com.woomulwoomul.core.domain.question.QuestionCategoryEntity
import java.time.LocalDateTime

interface QuestionCategoryCustomRepository {

    fun findByQuestionIds(questionIds: List<Long>): List<QuestionCategoryEntity>

    fun findByQuestionId(questionId: Long): List<QuestionCategoryEntity>

    fun findAdmin(now: LocalDateTime): List<QuestionCategoryEntity>
}