package com.woomulwoomul.adminapi.service.question

import com.woomulwoomul.core.common.constant.ExceptionCode.ANSWER_NOT_FOUND
import com.woomulwoomul.core.common.request.PageOffsetRequest
import com.woomulwoomul.core.common.response.CustomException
import com.woomulwoomul.core.domain.base.DetailServiceStatus
import com.woomulwoomul.core.domain.base.ServiceStatus
import com.woomulwoomul.core.domain.question.*
import com.woomulwoomul.core.domain.user.*
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.groups.Tuple.tuple
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class AnswerServiceTest(
    @Autowired private val answerService: AnswerService,
    @Autowired private val userRepository: UserRepository,
    @Autowired private val userRoleRepository: UserRoleRepository,
    @Autowired private val questionRepository: QuestionRepository,
    @Autowired private val categoryRepository: CategoryRepository,
    @Autowired private val questionCategoryRepository: QuestionCategoryRepository,
    @Autowired private val answerRepository: AnswerRepository,
    @Autowired private val questionAnswerRepository: QuestionAnswerRepository
) {

    @Test
    fun `질문 답변 전체 조회가 정상 작동한다`() {
        // given
        val now = LocalDateTime.now()
        val adminRole = createAndSaveUserRole(Role.ADMIN, "admin", "admin@woomulwoomul.com")
        val userRoles = listOf(createAndSaveUserRole(Role.USER, "user1", "user1@woomulwoomul.com"),
            createAndSaveUserRole(Role.USER, "user2", "user2@woomulwoomul.com"),
            createAndSaveUserRole(Role.USER, "user3", "user3@woomulwoomul.com"))
        val questionCategory = createAndSaveQuestionCategory(
            adminRole.user,
            "질문",
            BackgroundColor.entries[0],
            now.withHour(0).withMinute(0).withSecond(0),
            now.withHour(23).withMinute(59).withSecond(59)
        )
        val questionAnswers = listOf(
            createAndSaveQuestionAnswer(userRoles[0].user, adminRole.user, questionCategory.question, "답변1"),
            createAndSaveQuestionAnswer(userRoles[1].user, adminRole.user, questionCategory.question, "답변2"),
            createAndSaveQuestionAnswer(userRoles[2].user, adminRole.user, questionCategory.question, "답변3")
        )

        val pageOffsetRequest = PageOffsetRequest.of(2, 1)

        // when
        val result = answerService.getAllQuestionAnswers(questionCategory.question.id!!, pageOffsetRequest)

        // then
        assertAll(
            {
                assertThat(result.total).isEqualTo(questionAnswers.size.toLong())
            },
            {
                assertThat(result.data)
                    .extracting("id", "text", "imageUrl", "receiverId", "receiverNickname", "senderId",
                        "senderNickname", "status", "createDateTime", "updateDateTime")
                    .containsExactly(
                        tuple(questionAnswers[1].answer!!.id, questionAnswers[1].answer!!.text,
                            questionAnswers[1].answer!!.imageUrl, questionAnswers[1].receiver.id,
                            questionAnswers[1].receiver.nickname, questionAnswers[1].sender!!.id,
                            questionAnswers[1].sender!!.nickname, questionAnswers[1].status,
                            questionAnswers[1].createDateTime, questionAnswers[1].updateDateTime)
                    )
            }
        )
    }

    @Test
    fun `질문 답변 삭제가 정상 작동한다`() {
        // given
        val now = LocalDateTime.now()
        val adminRole = createAndSaveUserRole(Role.ADMIN, "admin", "admin@woomulwoomul.com")
        val userRole = createAndSaveUserRole(Role.USER)
        val questionCategory = createAndSaveQuestionCategory(
            adminRole.user,
            "질문",
            BackgroundColor.entries[0],
            now.withHour(0).withMinute(0).withSecond(0),
            now.withHour(23).withMinute(59).withSecond(59)
        )
        val questionAnswer = createAndSaveQuestionAnswer(userRole.user, adminRole.user, questionCategory.question, "답변")

        // when
        answerService.delete(questionAnswer.answer!!.id!!)

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

    @Test
    fun `존재하지 않은 답변 ID로 질문 답변 삭제를 하면 예외가 발생한다`() {
        // given
        val questionId = Long.MAX_VALUE

        // when & then
        assertThatThrownBy { answerService.delete(questionId) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(ANSWER_NOT_FOUND)
    }

    private fun createAndSaveQuestionAnswer(
        receiver: UserEntity,
        sender: UserEntity? = null,
        question: QuestionEntity,
        text: String = "답변",
    ): QuestionAnswerEntity {
        val answer = answerRepository.save(AnswerEntity(text = text, imageUrl = ""))

        return questionAnswerRepository.save(
            QuestionAnswerEntity(
                receiver = receiver,
                sender = sender,
                question = question,
                answer = answer
            )
        )
    }

    private fun createAndSaveQuestionCategory(
        user: UserEntity,
        text: String = "질문",
        backgroundColor: BackgroundColor = BackgroundColor.WHITE,
        startDateTime: LocalDateTime? = null,
        endDateTime: LocalDateTime? = null,
        categoryName: String = "카테고리"
    ): QuestionCategoryEntity {
        val question = questionRepository.save(
            QuestionEntity(
                user = user,
                text = text,
                backgroundColor = backgroundColor,
                startDateTime = startDateTime,
                endDateTime = endDateTime
            )
        )
        val category = categoryRepository.save(CategoryEntity(name = categoryName, admin = user))

        return questionCategoryRepository.save(QuestionCategoryEntity(question = question, category = category))
    }

    private fun createAndSaveUserRole(
        role: Role,
        nickname: String = "tester",
        email: String = "tester@woomulwoomul.com",
    ): UserRoleEntity {
        val user = userRepository.save(
            UserEntity(
                nickname = nickname,
                email = email,
                imageUrl = "https://t1.kakaocdn.net/account_images/default_profile.jpeg"
            )
        )

        return userRoleRepository.save(UserRoleEntity(user = user, role = role))
    }
}