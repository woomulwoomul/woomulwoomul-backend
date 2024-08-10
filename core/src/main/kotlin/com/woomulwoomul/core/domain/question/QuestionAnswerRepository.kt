package com.woomulwoomul.core.domain.question

import com.woomulwoomul.core.domain.question.custom.QuestionAnswerCustomRepository
import org.springframework.data.jpa.repository.JpaRepository

interface QuestionAnswerRepository : JpaRepository<QuestionAnswerEntity, Long>, QuestionAnswerCustomRepository {
}