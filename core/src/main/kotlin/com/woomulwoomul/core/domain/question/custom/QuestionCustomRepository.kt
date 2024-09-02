package com.woomulwoomul.core.domain.question.custom

import java.time.LocalDateTime

interface QuestionCustomRepository {

    fun findRandomAdminQuestionId(): Long?

    fun findAdminQuestionId(now: LocalDateTime): Long?
}