package com.woomulwoomul.core.domain.question

import com.woomulwoomul.core.domain.question.custom.CategoryCustomRepository
import org.springframework.data.jpa.repository.JpaRepository

interface CategoryRepository : JpaRepository<CategoryEntity, Long>, CategoryCustomRepository {
}