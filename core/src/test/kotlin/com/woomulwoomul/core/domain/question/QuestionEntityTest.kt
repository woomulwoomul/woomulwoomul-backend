package com.woomulwoomul.core.domain.question

import com.woomulwoomul.core.common.constant.BackgroundColor
import com.woomulwoomul.core.domain.base.ServiceStatus
import com.woomulwoomul.core.domain.user.UserEntity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.time.LocalDateTime

class QuestionEntityTest {

    @DisplayName("질문 업데이트가 정상 작동한다")
    @Test
    fun givenValid_whenUpdate_thenReturn() {
        // given
        val question = createQuestion()

        val now = LocalDateTime.now()
        val text = "질문 업데이트"
        val backgroundColor = BackgroundColor.WHITE
        val status = ServiceStatus.ACTIVE

        // when
        question.update(text, backgroundColor, now, now, status)

        // then
        assertThat(question)
            .extracting("id", "text", "backgroundColor", "startDateTime", "endDateTime", "status", "createDateTime",
                "updateDateTime")
            .containsExactly(question.id!!, text, backgroundColor, now, now, status, question.createDateTime,
                question.updateDateTime)
    }

    @ParameterizedTest(name = "[{index}] {0} 상태로 질문 업데이트가 정상 작동한다")
    @EnumSource(ServiceStatus::class)
    @DisplayName("질문 상태 업데이트가 정상 작동한다")
    fun givenEnum_whenUpdateStatus_thenReturn(status: ServiceStatus) {
        // given
        val question = createQuestion()

        // when
        question.updateStatus(status)

        // then
        assertThat(question.status).isEqualTo(status)
    }

    private fun createQuestion(): QuestionEntity {
        val admin = UserEntity(
            1L,
            "관리자",
            "admin@woomulwoomul.com",
            "https://t1.kakaocdn.net/account_images/default_profile.jpeg"
        )

        return QuestionEntity(1L, admin, "질문", BackgroundColor.WHITE)
    }
}