package com.woomulwoomul.core.domain.question

import com.woomulwoomul.core.domain.user.UserEntity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class CategoryEntityTest {

    @DisplayName("카테고리 업데이트가 정상 작동한다")
    @Test
    fun givenValid_whenUpdate_thenReturn() {
        // given
        val category = createCategory()
        val categoryName = "카테고리 수정"

        // when
        category.update(categoryName)

        // then
        assertThat(category)
            .extracting("name")
            .isEqualTo(categoryName)
    }

    private fun createCategory(): CategoryEntity {
        val admin = UserEntity(
            1L,
            "관리자",
            "admin@woomulwoomul.com",
            "https://t1.kakaocdn.net/account_images/default_profile.jpeg"
        )

        return CategoryEntity(1L, admin, "카테고리")
    }
}