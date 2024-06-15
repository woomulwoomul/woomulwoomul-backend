package com.woomulwoomul.woomulwoomulbackend.domain.question

import org.springframework.data.jpa.repository.JpaRepository

interface CategoryRepository : JpaRepository<CategoryEntity, Long> {
}