package com.woomulwoomul.core.domain.question

import com.woomulwoomul.core.domain.base.ServiceStatus
import com.woomulwoomul.core.domain.user.UserEntity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

class QuestionCategoryEntityTest {

    @ParameterizedTest(name = "[{index}] {0} 상태로 질문 카테고리 업데이트가 정상 작동한다")
    @EnumSource(ServiceStatus::class)
    @DisplayName("질문 카테고리 상태 업데이트가 정상 작동한다")
    fun givenEnum_whenUpdateStatus_thenReturn(status: ServiceStatus) {
        // given
        val questionCategory = createQuestionCategory()

        // when
        questionCategory.updateStatus(status)

        // then
        assertThat(questionCategory.status).isEqualTo(status)
    }

    private fun createQuestionCategory(): QuestionCategoryEntity {
        val admin = UserEntity(
            1L,
            "관리자",
            "admin@woomulwoomul.com",
            "https://t1.kakaocdn.net/account_images/default_profile.jpeg"
        )
        val question = QuestionEntity(1L, admin, "질문", "000000")
        val category = CategoryEntity(1L, admin, "카테고리")

        return QuestionCategoryEntity(1L, question, category)
    }
}