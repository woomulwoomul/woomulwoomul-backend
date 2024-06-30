package com.woomulwoomul.woomulwoomulbackend.domain.question

import org.springframework.data.jpa.repository.JpaRepository

interface AnswerRepository : JpaRepository<AnswerEntity, Long> {
}