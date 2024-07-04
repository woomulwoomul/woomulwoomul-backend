package com.woomulwoomul.woomulwoomulbackend.domain.question.custom

import com.woomulwoomul.woomulwoomulbackend.domain.question.QuestionCategoryEntity

interface QuestionCategoryCustomRepository {

    fun findByQuestionIds(questionIds: List<Long>): List<QuestionCategoryEntity>

    fun findByQuestionId(questionId: Long): List<QuestionCategoryEntity>

    fun findRandom(limit: Long = 5): List<QuestionCategoryEntity>
}