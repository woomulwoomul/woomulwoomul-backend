package com.woomulwoomul.core.domain.question

import com.woomulwoomul.core.domain.base.DetailServiceStatus
import com.woomulwoomul.core.domain.base.ServiceStatus
import com.woomulwoomul.core.domain.user.UserEntity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Test

class QuestionAnswerEntityTest {

    @Test
    fun `회원 질문 답변 삭제가 정상 작동한다`() {
        // given
        val users = listOf(createUser(1L, "tester1", "tester1@woomulwoomul.com"),
            createUser(2L, "tester2", "tester2@woomulwoomul.com"))
        val question = createQuestion(1L, users[0], "질문1입니다.", BackgroundColor.WHITE)
        val answer = createAnswer(1L, "답변1입니다.", "")
        val questionAnswer = createQuestionAnswer(1L, users[1], users[0], question, answer)

        // when
        questionAnswer.deleteByUser()

        // then
        assertAll(
            {
                assertThat(questionAnswer.status).isEqualTo(DetailServiceStatus.USER_DEL)
            },
            {
                assertThat(questionAnswer.answer!!.status).isEqualTo(ServiceStatus.USER_DEL)
            }
        )
    }

    @Test
    fun `관리자 질문 답변 삭제가 정상 작동한다`() {
        // given
        val users = listOf(createUser(1L, "tester1", "tester1@woomulwoomul.com"),
            createUser(2L, "tester2", "tester2@woomulwoomul.com"))
        val question = createQuestion(1L, users[0], "질문1입니다.", BackgroundColor.WHITE)
        val answer = createAnswer(1L, "답변1입니다.", "")
        val questionAnswer = createQuestionAnswer(1L, users[1], users[0], question, answer)

        // when
        questionAnswer.deleteByAdmin()

        // then
        assertAll(
            {
                assertThat(questionAnswer.status).isEqualTo(DetailServiceStatus.ADMIN_DEL)
            },
            {
                assertThat(questionAnswer.answer!!.status).isEqualTo(ServiceStatus.ADMIN_DEL)
            }
        )
    }

    private fun createQuestionAnswer(
        id: Long,
        receiver: UserEntity,
        sender: UserEntity,
        question: QuestionEntity,
        answer: AnswerEntity,
    ): QuestionAnswerEntity {
        return QuestionAnswerEntity(id, receiver, sender, question, answer)
    }

    private fun createUser(id: Long, nickname: String, email: String): UserEntity {
        return UserEntity(id, nickname, email, "", "")
    }

    private fun createQuestion(id: Long, user: UserEntity, text: String, backgroundColor: BackgroundColor): QuestionEntity {
        return QuestionEntity(id, user, text, backgroundColor)
    }

    private fun createAnswer(id: Long, text: String, imageUrl: String): AnswerEntity {
        return AnswerEntity(id, text, imageUrl)
    }
}