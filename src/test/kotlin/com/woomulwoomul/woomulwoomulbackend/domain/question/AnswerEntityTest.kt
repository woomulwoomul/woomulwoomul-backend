package com.woomulwoomul.woomulwoomulbackend.domain.question

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class AnswerEntityTest {

    @DisplayName("답변 업데이트가 정상 작동한다")
    @Test
    fun givenValid_whenUpdate_thenReturn() {
        // given
        val answer = createAnswer()
        val answerText = "수정 답변"
        val answerImageUrl = ""

        // when
        answer.update(answerText, answerImageUrl)

        // then
        assertThat(answer)
            .extracting("text", "imageUrl")
            .containsExactly(answerText, answerImageUrl)
    }

    private fun createAnswer(): AnswerEntity {
        return AnswerEntity(1L, "답변", "")
    }
}