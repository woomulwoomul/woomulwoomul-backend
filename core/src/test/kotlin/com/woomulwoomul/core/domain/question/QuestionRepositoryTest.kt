package com.woomulwoomul.core.domain.question

import com.woomulwoomul.core.common.constant.BackgroundColor
import com.woomulwoomul.core.common.request.PageOffsetRequest
import com.woomulwoomul.core.domain.base.ServiceStatus
import com.woomulwoomul.core.domain.user.*
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.groups.Tuple.tuple
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.stream.Stream

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
        val question = createAndSaveQuestion(adminRole.user, "질문")

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
            BackgroundColor.WHITE,
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
                BackgroundColor.entries[0],
                now.withHour(0).withMinute(0).withSecond(0),
                now.withHour(23).withMinute(59).withSecond(59)
            ),
            createAndSaveQuestion(
                adminRole.user,
                "질문2",
                BackgroundColor.entries[1],
                now.withHour(0).withMinute(0).withSecond(0),
                now.withHour(23).withMinute(59).withSecond(59)
            ),
            createAndSaveQuestion(
                adminRole.user,
                "질문3",
                BackgroundColor.entries[2],
                now.withHour(0).withMinute(0).withSecond(0),
                now.withHour(23).withMinute(59).withSecond(59)
            ),
            createAndSaveQuestion(
                adminRole.user,
                "질문4",
                BackgroundColor.entries[3],
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
                        tuple(questions[1].id, questions[1].text, questions[1].backgroundColor,
                            questions[1].startDateTime, questions[1].endDateTime, questions[1].status,
                            questions[1].createDateTime, questions[1].updateDateTime),
                        tuple(questions[0].id, questions[0].text, questions[0].backgroundColor,
                            questions[0].startDateTime, questions[0].endDateTime, questions[0].status,
                            questions[0].createDateTime, questions[0].updateDateTime)
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

    @ParameterizedTest(name = "[{index}] 질문 ID로 {0} 상태인 질문을 {1} 상태 조회를 하면 정상 작동한다")
    @MethodSource("providerFind")
    @DisplayName("질문 ID로 특정 상태인 질문을 조회하면 정상 작동한다")
    fun givenProvider_whenFind_thenReturn(status: ServiceStatus, statusesQuery: List<ServiceStatus>) {
        // given
        val adminRole = createAndSaveUserRole(Role.ADMIN)
        val question = createAndSaveQuestion(
            adminRole.user,
            "질문",
            BackgroundColor.WHITE,
            null,
            null
        )
        question.updateStatus(status)

        // when
        val foundQuestion = questionRepository.find(question.id!!, statusesQuery)

        // then
        assertAll(
            {
                assertThat(foundQuestion)
                    .extracting("id", "text", "backgroundColor", "startDateTime", "endDateTime", "status",
                        "createDateTime", "updateDateTime")
                    .containsExactly(question.id, question.text, question.backgroundColor, question.startDateTime,
                        question.endDateTime, question.status, question.createDateTime, question.updateDateTime)
            }, {
                assertThat(foundQuestion)
                    .extracting("user")
                    .extracting("id", "nickname", "email", "imageUrl", "introduction", "status", "createDateTime",
                        "updateDateTime")
                    .containsExactly(adminRole.user.id, adminRole.user.nickname, adminRole.user.email,
                        adminRole.user.imageUrl, adminRole.user.introduction, adminRole.user.status,
                        adminRole.user.createDateTime, adminRole.user.updateDateTime)
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
        backgroundColor: BackgroundColor = BackgroundColor.WHITE,
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

    companion object {
        @JvmStatic
        fun providerFind(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(ServiceStatus.ACTIVE, listOf(ServiceStatus.ACTIVE)),
                Arguments.of(ServiceStatus.ACTIVE, listOf(ServiceStatus.ACTIVE, ServiceStatus.USER_DEL)),
                Arguments.of(ServiceStatus.ACTIVE, listOf(ServiceStatus.ACTIVE, ServiceStatus.ADMIN_DEL)),
                Arguments.of(ServiceStatus.ACTIVE, listOf(ServiceStatus.ACTIVE, ServiceStatus.USER_DEL, ServiceStatus.ADMIN_DEL)),
                Arguments.of(ServiceStatus.USER_DEL, listOf(ServiceStatus.USER_DEL)),
                Arguments.of(ServiceStatus.USER_DEL, listOf(ServiceStatus.USER_DEL, ServiceStatus.ACTIVE)),
                Arguments.of(ServiceStatus.USER_DEL, listOf(ServiceStatus.USER_DEL, ServiceStatus.ADMIN_DEL)),
                Arguments.of(ServiceStatus.USER_DEL, listOf(ServiceStatus.USER_DEL, ServiceStatus.ACTIVE, ServiceStatus.ADMIN_DEL)),
                Arguments.of(ServiceStatus.ADMIN_DEL, listOf(ServiceStatus.ADMIN_DEL)),
                Arguments.of(ServiceStatus.ADMIN_DEL, listOf(ServiceStatus.ADMIN_DEL, ServiceStatus.ACTIVE)),
                Arguments.of(ServiceStatus.ADMIN_DEL, listOf(ServiceStatus.ADMIN_DEL, ServiceStatus.USER_DEL)),
                Arguments.of(ServiceStatus.ADMIN_DEL, listOf(ServiceStatus.ADMIN_DEL, ServiceStatus.ACTIVE, ServiceStatus.USER_DEL)),
            )
        }
    }
}