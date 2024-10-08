package com.woomulwoomul.core.domain.question

import com.woomulwoomul.core.domain.base.ServiceStatus
import com.woomulwoomul.core.domain.base.ServiceStatus.ADMIN_DEL
import com.woomulwoomul.core.domain.user.UserEntity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

class CategoryEntityTest {

    @DisplayName("카테고리명 업데이트가 정상 작동한다")
    @Test
    fun givenValid_whenUpdateName_thenReturn() {
        // given
        val category = createCategory()
        val categoryName = "카테고리 수정"

        // when
        category.updateName(categoryName)

        // then
        assertThat(category)
            .extracting("name")
            .isEqualTo(categoryName)
    }

    @ParameterizedTest(name = "[{index}] {0} 상태로 카테고리 업데이트가 정상 작동한다")
    @EnumSource(ServiceStatus::class)
    @DisplayName("카테고리 상태 업데이트가 정상 작동한다")
    fun givenEnum_whenUpdateStatus_thenReturn(status: ServiceStatus) {
        // given
        val category = createCategory()

        // when
        category.updateStatus(status)

        // then
        assertThat(category.status).isEqualTo(status)
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