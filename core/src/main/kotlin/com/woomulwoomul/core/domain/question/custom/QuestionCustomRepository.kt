package com.woomulwoomul.core.domain.question.custom

interface QuestionCustomRepository {

    fun findRandomAdminQuestionId(): Long?
}