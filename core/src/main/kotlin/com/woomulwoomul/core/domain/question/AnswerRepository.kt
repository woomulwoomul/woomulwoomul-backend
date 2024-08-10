package com.woomulwoomul.core.domain.question

import org.springframework.data.jpa.repository.JpaRepository

interface AnswerRepository : JpaRepository<AnswerEntity, Long> {
}