package com.woomulwoomul.core.domain.question

import com.woomulwoomul.core.common.request.PageOffsetRequest
import com.woomulwoomul.core.domain.user.*
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.groups.Tuple
import org.assertj.core.groups.Tuple.tuple
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class QuestionRepositoryTest(
    @Autowired private val questionRepository: QuestionRepository,
    @Autowired private val userRepository: UserRepository,
    @Autowired private val userRoleRepository: UserRoleRepository,
) {

    @DisplayName("관리자 질문 ID 랜덤 조회가 정상 작동한다")
    @Test
    fun givenValid_whenFindRandomQuestionId_thenReturn() {
        // given
        val adminRole = createAndSaveUserRole(Role.ADMIN)
        val question = createAndSaveQuestion(adminRole.user, "질문", backgroundColor = "000000")

        // when
        val questionId = questionRepository.findRandomAdminQuestionId()

        // then
        assertThat(questionId).isEqualTo(question.id!!)
    }

    @DisplayName("현재 날짜 기준 관리자 질문 ID 조회를 하면 정상 작동한다")
    @Test
    fun givenValid_whenFindAdminQuestionId_thenReturn() {
        // given
        val now = LocalDateTime.now()
        val adminRole = createAndSaveUserRole(Role.ADMIN)
        val question = createAndSaveQuestion(
            adminRole.user,
            "질문",
            "000000",
            now.withHour(0).withMinute(0).withSecond(0),
            now.withHour(23).withMinute(59).withSecond(59)
        )

        // when
        val questionId = questionRepository.findAdminQuestionId(now)

        // then
        assertThat(questionId).isEqualTo(question.id!!)
    }

    @DisplayName("질문 전체 조회가 정상 작동한다")
    @Test
    fun givenValid_whenFindAll_thenReturn() {
        // given
        val now = LocalDateTime.now()
        val adminRole = createAndSaveUserRole(Role.ADMIN)
        val questions = listOf(
            createAndSaveQuestion(
                adminRole.user,
                "질문1",
                "000001",
                now.withHour(0).withMinute(0).withSecond(0),
                now.withHour(23).withMinute(59).withSecond(59)
            ),
            createAndSaveQuestion(
                adminRole.user,
                "질문2",
                "000002",
                now.withHour(0).withMinute(0).withSecond(0),
                now.withHour(23).withMinute(59).withSecond(59)
            ),
            createAndSaveQuestion(
                adminRole.user,
                "질문3",
                "000003",
                now.withHour(0).withMinute(0).withSecond(0),
                now.withHour(23).withMinute(59).withSecond(59)
            ),
            createAndSaveQuestion(
                adminRole.user,
                "질문4",
                "000004",
                now.withHour(0).withMinute(0).withSecond(0),
                now.withHour(23).withMinute(59).withSecond(59)
            )
        )
        val pageOffsetRequest = PageOffsetRequest.of(2, 2)

        // when
        val foundQuestions = questionRepository.findAll(pageOffsetRequest)

        // then
        assertAll(
            {
                assertThat(foundQuestions.total).isEqualTo(questions.size.toLong())
            },
            {
                assertThat(foundQuestions.data)
                    .extracting("id", "text", "backgroundColor", "startDateTime", "endDateTime", "status", "createDateTime",
                        "updateDateTime")
                    .containsExactly(
                        tuple(questions[2].id, questions[2].text, questions[2].backgroundColor,
                            questions[2].startDateTime, questions[2].endDateTime, questions[2].status,
                            questions[2].createDateTime, questions[2].updateDateTime),
                        tuple(questions[3].id, questions[3].text, questions[3].backgroundColor,
                            questions[3].startDateTime, questions[3].endDateTime, questions[3].status,
                            questions[3].createDateTime, questions[3].updateDateTime)
                    )
            }
        )
    }

    @DisplayName("질문이 없을때 질문 전체 조회가 정상 작동한다")
    @Test
    fun givenEmpty_whenFindAll_thenReturn() {
        // given
        val pageOffsetRequest = PageOffsetRequest.of(1, 2)

        // when
        val foundQuestions = questionRepository.findAll(pageOffsetRequest)

        // then
        assertAll(
            {
                assertThat(foundQuestions.total).isEqualTo(0L)
            },
            {
                assertThat(foundQuestions.data).isEmpty()
            }
        )
    }

    private fun createAndSaveUserRole(
        role: Role,
        nickname: String = "tester",
        email: String = "tester@woomulwoomul.com",
    ): UserRoleEntity {
        val user = userRepository.save(UserEntity(
            nickname = nickname,
            email = email,
            imageUrl = "https://t1.kakaocdn.net/account_images/default_profile.jpeg"
        ))

        return userRoleRepository.save(UserRoleEntity(user = user, role = role))
    }

    private fun createAndSaveQuestion(
        user: UserEntity,
        text: String = "질문",
        backgroundColor: String = "0F0F0F",
        startDateTime: LocalDateTime? = null,
        endDateTime: LocalDateTime? = null,
    ): QuestionEntity {
        return questionRepository.save(QuestionEntity(
            user = user,
            text = text,
            backgroundColor = backgroundColor,
            startDateTime = startDateTime,
            endDateTime = endDateTime
        ))
    }
}