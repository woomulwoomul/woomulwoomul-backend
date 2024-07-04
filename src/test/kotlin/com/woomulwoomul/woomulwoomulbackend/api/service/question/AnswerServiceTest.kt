package com.woomulwoomul.woomulwoomulbackend.api.service.question

import com.woomulwoomul.woomulwoomulbackend.api.service.question.request.AnswerCreateServiceRequest
import com.woomulwoomul.woomulwoomulbackend.api.service.question.response.AnswerFindAllCategoryResponse
import com.woomulwoomul.woomulwoomulbackend.common.constant.ExceptionCode.*
import com.woomulwoomul.woomulwoomulbackend.common.request.PageRequest
import com.woomulwoomul.woomulwoomulbackend.common.response.CustomException
import com.woomulwoomul.woomulwoomulbackend.domain.base.DetailServiceStatus
import com.woomulwoomul.woomulwoomulbackend.domain.question.*
import com.woomulwoomul.woomulwoomulbackend.domain.user.UserEntity
import com.woomulwoomul.woomulwoomulbackend.domain.user.UserRepository
import jakarta.validation.ConstraintViolationException
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class AnswerServiceTest(
    @Autowired private val answerService: AnswerService,
    @Autowired private val userRepository: UserRepository,
    @Autowired private val categoryRepository: CategoryRepository,
    @Autowired private val questionRepository: QuestionRepository,
    @Autowired private val questionCategoryRepository: QuestionCategoryRepository,
    @Autowired private val answerRepository: AnswerRepository,
    @Autowired private val questionAnswerRepository: QuestionAnswerRepository,
) {

    @DisplayName("답변 전체 조회가 정상 작동한다")
    @Test
    fun givenValid_whenGetAllAnswers_thenReturn() {
        // given
        val admin = createAndSaveUser("admin","admin@woomulwoomul.com")
        val user = createAndSaveUser("user","user@woomulwoomul.com")

        val categories = listOf(
            createAndSaveCategory(admin, "카테고리1"),
            createAndSaveCategory(admin, "카테고리2"),
            createAndSaveCategory(admin, "카테고리3")
        )
        val questions = listOf(
            createAndSaveQuestion(categories, admin, "질문1"),
            createAndSaveQuestion(categories, admin, "질문2"),
            createAndSaveQuestion(categories, admin, "질문3")
        )
        val questionAnswers = listOf(
            createAndSaveQuestionAnswer(user, admin, questions[0]),
            createAndSaveQuestionAnswer(user, admin, questions[1]),
            createAndSaveQuestionAnswer(user, admin, questions[2]),
        )
        val answers = listOf(
            createAndSaveAnswer(questionAnswers[0], "답변1", ""),
            createAndSaveAnswer(questionAnswers[1], "", "답변2")
        )

        val pageRequest = PageRequest.of(0, 1)

        // when
        val response = answerService.getAllAnswers(admin.id!!, user.id!!, pageRequest)

        // then
        assertAll(
            {
                assertThat(response.total).isEqualTo(answers.size.toLong())
            },
            {
                assertThat(response.data)
                    .extracting("answerId", "questionId", "backgroundColor", "categories")
                    .containsExactly(
                        tuple(answers[1].id!!, questions[1].id!!, questions[1].backgroundColor,
                            listOf(AnswerFindAllCategoryResponse(categories[0].id!!, categories[0].name),
                                AnswerFindAllCategoryResponse(categories[1].id!!, categories[1].name),
                                AnswerFindAllCategoryResponse(categories[2].id!!, categories[2].name))
                        )
                    )
            }
        )
    }

    @DisplayName("존재하지 않은 답변는데 답변 전체 조회를 하면 정상 작동한다")
    @Test
    fun givenNonExistingAnswer_whenGetAllAnswers_thenReturn() {
        // given
        val admin = createAndSaveUser("admin","admin@woomulwoomul.com")
        val user = createAndSaveUser("user","user@woomulwoomul.com")

        val pageRequest = PageRequest.of(0, 1)

        // when
        val response = answerService.getAllAnswers(admin.id!!, user.id!!, pageRequest)

        // then
        assertAll(
            {
                assertThat(response.total).isEqualTo(0L)
            },
            {
                assertThat(response.data).isEmpty()
            }
        )
    }

    @DisplayName("존재하지 않은 회원 답변 전체 조회를 하면 예외가 발생한다")
    @Test
    fun givenNonExistingUser_whenGetAllAnswers_thenThrow() {
        // given
        val visitorUserId = 1L
        val user = createAndSaveUser("user","user@woomulwoomul.com")

        val pageRequest = PageRequest.of(0, 1)

        // when & then
        assertThatThrownBy { answerService.getAllAnswers(visitorUserId, user.id!!, pageRequest) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(USER_NOT_FOUND)
    }

    @DisplayName("존재하지 않은 방문자 회원으로 회원 답변 전체 조회를 하면 예외가 발생한다")
    @Test
    fun givenNonExistingVisitorUser_whenGetAllAnswers_thenThrow() {
        // given
        val user = createAndSaveUser("user","user@woomulwoomul.com")
        val userId = 1L

        val pageRequest = PageRequest.of(0, 1)

        // when & then
        assertThatThrownBy { answerService.getAllAnswers(user.id!!, userId, pageRequest) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(USER_NOT_FOUND)
    }

    @DisplayName("답변 조회가 정상 작동한다")
    @Test
    fun givenValid_whenGetAnswer_thenReturn() {
        // given
        val admin = createAndSaveUser("admin","admin@woomulwoomul.com")
        val user = createAndSaveUser("user","user@woomulwoomul.com")

        val categories = listOf(
            createAndSaveCategory(admin, "카테고리1"),
            createAndSaveCategory(admin, "카테고리2"),
            createAndSaveCategory(admin, "카테고리3")
        )
        val question = createAndSaveQuestion(categories, admin, "질문")

        val questionAnswer = createAndSaveQuestionAnswer(user, admin, question)
        val answer = createAndSaveAnswer(questionAnswer, "답변1", "")

        // when
        val response = answerService.getAnswer(user.id!!, answer.id!!)

        // then
        assertAll(
            {
                assertThat(response)
                    .extracting("answerId", "answerText", "answerImageUrl", "answerUpdateDateTime", "answeredUserCnt",
                        "answeredUserImageUrls", "questionId", "questionText", "questionBackgroundColor")
                    .containsExactly(answer.id, answer.text, answer.imageUrl, answer.updateDateTime, 1L,
                        listOf(user.imageUrl), question.id, question.text, question.backgroundColor)
            },
            {
                assertThat(response.categories)
                    .extracting("categoryId", "name")
                    .containsExactly(
                        tuple(categories[0].id, categories[0].name),
                        tuple(categories[1].id, categories[1].name),
                        tuple(categories[2].id, categories[2].name)
                    )
            }
        )
    }

    @DisplayName("존재하지 않는 답변을 조회하면 예외가 발생한다")
    @Test
    fun givenNonExistingAnswer_whenGetAnswer_thenThrow() {
        // given
        val user = createAndSaveUser("user","user@woomulwoomul.com")

        val answerId = 1L

        // when & then
        assertThatThrownBy { answerService.getAnswer(user.id!!, answerId) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(ANSWER_NOT_FOUND)
    }

    @DisplayName("답변 작성이 정상 작동한다")
    @Test
    fun givenValid_whenCreateAnswer_thenReturn() {
        // given
        val admin = createAndSaveUser("admin","admin@woomulwoomul.com")
        val user = createAndSaveUser("user","user@woomulwoomul.com")

        val categories = listOf(
            createAndSaveCategory(admin, "카테고리1"),
            createAndSaveCategory(admin, "카테고리2"),
            createAndSaveCategory(admin, "카테고리3")
        )
        val question = createAndSaveQuestion(categories, admin, "질문")

        val request = createValidAnswerCreateServiceRequest()

        // when
        val response = answerService.createAnswer(user.id!!, admin.id!!, question.id!!, request)

        // then
        assertAll(
            {
                assertThat(response)
                    .extracting("userId", "userNickname", "questionId", "questionText", "questionBackgroundColor")
                    .containsExactly(user.id, user.nickname, question.id, question.text, question.backgroundColor)
            },
            {
                assertThat(response.categories)
                    .extracting("categoryId", "name")
                    .containsExactly(
                        tuple(categories[0].id, categories[0].name),
                        tuple(categories[1].id, categories[1].name),
                        tuple(categories[2].id, categories[2].name)
                    )
            }
        )
    }

    @DisplayName("본인 질문에 대한 답변 작성이 정상 작동한다")
    @Test
    fun givenOwnQuestion_whenCreateAnswer_thenReturn() {
        // given
        val admin = createAndSaveUser("admin","admin@woomulwoomul.com")
        val user = createAndSaveUser("user","user@woomulwoomul.com")

        val categories = listOf(
            createAndSaveCategory(admin, "카테고리1"),
            createAndSaveCategory(admin, "카테고리2"),
            createAndSaveCategory(admin, "카테고리3")
        )
        val question = createAndSaveQuestion(categories, user, "질문")

        val request = createValidAnswerCreateServiceRequest()

        // when
        val response = answerService.createAnswer(user.id!!, user.id!!, question.id!!, request)

        // then
        assertAll(
            {
                assertThat(response)
                    .extracting("userId", "userNickname", "questionId", "questionText", "questionBackgroundColor")
                    .containsExactly(user.id, user.nickname, question.id, question.text, question.backgroundColor)
            },
            {
                assertThat(response.categories)
                    .extracting("categoryId", "name")
                    .containsExactly(
                        tuple(categories[0].id, categories[0].name),
                        tuple(categories[1].id, categories[1].name),
                        tuple(categories[2].id, categories[2].name)
                    )
            }
        )
    }

    @DisplayName("팔로우 관계가 존재하는 회원의 질문에 답변 작성이 정상 작동한다")
    @Test
    fun givenExistingFollow_whenCreateAnswer_thenReturn() {
        // given
        val admin = createAndSaveUser("admin","admin@woomulwoomul.com")
        val user1 = createAndSaveUser("user1","user1@woomulwoomul.com")
        val user2 = createAndSaveUser("user2","user2@woomulwoomul.com")

        val categories = listOf(
            createAndSaveCategory(admin, "카테고리1"),
            createAndSaveCategory(admin, "카테고리2"),
            createAndSaveCategory(admin, "카테고리3")
        )
        val question = createAndSaveQuestion(categories, admin, "질문")

        val request = createValidAnswerCreateServiceRequest()

        // when
        val response = answerService.createAnswer(user1.id!!, user2.id!!, question.id!!, request)

        // then
        assertAll(
            {
                assertThat(response)
                    .extracting("userId", "userNickname", "questionId", "questionText", "questionBackgroundColor")
                    .containsExactly(user1.id, user1.nickname, question.id, question.text, question.backgroundColor)
            },
            {
                assertThat(response.categories)
                    .extracting("categoryId", "name")
                    .containsExactly(
                        tuple(categories[0].id, categories[0].name),
                        tuple(categories[1].id, categories[1].name),
                        tuple(categories[2].id, categories[2].name)
                    )
            }
        )
    }

    @DisplayName("답변 내용 280자 초과일시 답변 작성을 하면 예외가 발생한다")
    @Test
    fun givenGreaterThan280SizeAnswerText_whenCreateAnswer_thenThrow() {
        // given
        val receiverUserId = 1L
        val senderUserId = 2L
        val questionId = 1L
        val request = createValidAnswerCreateServiceRequest()
        request.answerText = "a".repeat(281)

        // when & then
        assertThatThrownBy { answerService.createAnswer(receiverUserId, senderUserId, questionId, request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .message()
            .asString()
            .contains(ANSWER_TEXT_SIZE_INVALID.message)
    }

    @DisplayName("답변 이미지 500자 초과일시 답변 작성을 하면 예외가 발생한다")
    @Test
    fun givenGreaterThan500SizeAnswerImageUrl_whenCreateAnswer_thenThrow() {
        // given
        val receiverUserId = 1L
        val senderUserId = 2L
        val questionId = 1L
        val request = createValidAnswerCreateServiceRequest()
        request.answerImageUrl = "a".repeat(5001)

        // when & then
        assertThatThrownBy { answerService.createAnswer(receiverUserId, senderUserId, questionId, request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .message()
            .asString()
            .contains(ANSWER_IMAGE_URL_INVALID.message)
    }

    @DisplayName("존재하지 않는 수신자 회원의 질문에 답변 작성을 하면 예외가 발생한다")
    @Test
    fun givenNonExistingReceiver_whenCreateAnswer_thenThrow() {
        // given
        val admin = createAndSaveUser("admin","admin@woomulwoomul.com")
        val user = createAndSaveUser("user","user@woomulwoomul.com")
        val receiverUserId = Long.MAX_VALUE

        val categories = listOf(
            createAndSaveCategory(admin, "카테고리1"),
            createAndSaveCategory(admin, "카테고리2"),
            createAndSaveCategory(admin, "카테고리3")
        )
        val question = createAndSaveQuestion(categories, admin, "질문")

        val request = createValidAnswerCreateServiceRequest()

        // when & then
        assertThatThrownBy { answerService.createAnswer(receiverUserId, user.id!!, question.id!!, request) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(USER_NOT_FOUND)
    }

    @DisplayName("존재하지 않는 수신자 회원의 질문에 답변 작성을 하면 예외가 발생한다")
    @Test
    fun givenNonExistingSender_whenCreateAnswer_thenThrow() {
        // given
        val admin = createAndSaveUser("admin","admin@woomulwoomul.com")
        val user = createAndSaveUser("user","user@woomulwoomul.com")
        val senderUserId = Long.MAX_VALUE

        val categories = listOf(
            createAndSaveCategory(admin, "카테고리1"),
            createAndSaveCategory(admin, "카테고리2"),
            createAndSaveCategory(admin, "카테고리3")
        )
        val question = createAndSaveQuestion(categories, admin, "질문")

        val request = createValidAnswerCreateServiceRequest()

        // when & then
        assertThatThrownBy { answerService.createAnswer(user.id!!, senderUserId, question.id!!, request) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(USER_NOT_FOUND)
    }

    @DisplayName("존재하지 않는 수신자 회원의 질문에 답변 작성을 하면 예외가 발생한다")
    @Test
    fun givenNonExistingQuestion_whenCreateAnswer_thenThrow() {
        // given
        val admin = createAndSaveUser("admin","admin@woomulwoomul.com")
        val user = createAndSaveUser("user","user@woomulwoomul.com")

        val questionId = Long.MAX_VALUE

        val request = createValidAnswerCreateServiceRequest()

        // when & then
        assertThatThrownBy { answerService.createAnswer(user.id!!, admin.id!!, questionId, request) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(QUESTION_NOT_FOUND)
    }

    private fun createValidAnswerCreateServiceRequest(): AnswerCreateServiceRequest {
        return AnswerCreateServiceRequest("답변", "")
    }

    private fun createAndSaveAnswer(questionAnswer: QuestionAnswerEntity, text: String, imageUrl: String): AnswerEntity {
        val answer = answerRepository.save(AnswerEntity(text = text, imageUrl = imageUrl))

        questionAnswer.apply {
            status = DetailServiceStatus.COMPLETE
            this.answer = answer
            questionAnswerRepository.save(this)
        }

        return answer
    }

    private fun createAndSaveQuestionAnswer(
        receiver: UserEntity,
        sender: UserEntity,
        question: QuestionEntity,
    ): QuestionAnswerEntity {
        return questionAnswerRepository.save(
            QuestionAnswerEntity(
            receiver = receiver,
            sender = sender,
            question = question
        )
        )
    }

    private fun createAndSaveQuestion(categories: List<CategoryEntity>, user: UserEntity, text: String): QuestionEntity {
        val question = questionRepository.save(
            QuestionEntity(user = user, text = text, backgroundColor = "0F0F0F")
        )

        questionCategoryRepository.saveAll(
            categories.map { QuestionCategoryEntity(question = question, category = it) }
        )

        return question
    }

    private fun createAndSaveCategory(admin: UserEntity, name: String): CategoryEntity {
        return categoryRepository.save(
            CategoryEntity(admin = admin, name = name)
        )
    }

    private fun createAndSaveUser(nickname: String, email: String): UserEntity {
        return userRepository.save(
            UserEntity(
                nickname = nickname,
                email = email,
                imageUrl = "https://t1.kakaocdn.net/account_images/default_profile.jpeg.twg.thumb.R640x640"
            )
        )
    }
}