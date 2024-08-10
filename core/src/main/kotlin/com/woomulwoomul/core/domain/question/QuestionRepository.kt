package com.woomulwoomul.core.domain.question

import com.woomulwoomul.core.domain.question.custom.QuestionCustomRepository
import org.springframework.data.jpa.repository.JpaRepository

interface QuestionRepository : JpaRepository<QuestionEntity, Long>, QuestionCustomRepository {
}