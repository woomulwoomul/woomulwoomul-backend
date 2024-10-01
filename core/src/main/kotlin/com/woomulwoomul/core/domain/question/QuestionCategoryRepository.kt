package com.woomulwoomul.core.domain.question

import com.woomulwoomul.core.domain.question.custom.QuestionCategoryCustomRepository
import org.springframework.data.jpa.repository.JpaRepository

interface QuestionCategoryRepository : JpaRepository<QuestionCategoryEntity, Long>, QuestionCategoryCustomRepository
