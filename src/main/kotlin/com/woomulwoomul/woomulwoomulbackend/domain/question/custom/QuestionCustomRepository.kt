package com.woomulwoomul.woomulwoomulbackend.domain.question.custom

interface QuestionCustomRepository {

    fun findRandomAdminQuestionId(): Long?
}