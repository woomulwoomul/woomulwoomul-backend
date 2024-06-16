package com.woomulwoomul.woomulwoomulbackend.domain.question

import com.woomulwoomul.woomulwoomulbackend.domain.question.custom.CategoryCustomRepository
import org.springframework.data.jpa.repository.JpaRepository

interface CategoryRepository : JpaRepository<CategoryEntity, Long>, CategoryCustomRepository {
}