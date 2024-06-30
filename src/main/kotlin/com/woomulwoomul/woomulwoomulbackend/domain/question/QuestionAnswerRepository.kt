package com.woomulwoomul.woomulwoomulbackend.domain.question

import com.woomulwoomul.woomulwoomulbackend.domain.question.custom.QuestionAnswerCustomRepository
import org.springframework.data.jpa.repository.JpaRepository

interface QuestionAnswerRepository : JpaRepository<QuestionAnswerEntity, Long>, QuestionAnswerCustomRepository {
}