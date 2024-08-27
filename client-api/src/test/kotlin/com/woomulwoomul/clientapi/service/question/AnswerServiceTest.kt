package com.woomulwoomul.clientapi.service.question

import com.woomulwoomul.clientapi.service.question.request.AnswerCreateServiceRequest
import com.woomulwoomul.clientapi.service.question.request.AnswerUpdateServiceRequest
import com.woomulwoomul.clientapi.service.question.response.AnswerFindAllCategoryResponse
import com.woomulwoomul.core.common.constant.ExceptionCode.*
import com.woomulwoomul.core.common.request.PageRequest
import com.woomulwoomul.core.common.response.CustomException
import com.woomulwoomul.core.domain.base.DetailServiceStatus
import com.woomulwoomul.core.domain.base.ServiceStatus
import com.woomulwoomul.core.domain.question.*
import com.woomulwoomul.core.domain.user.UserEntity
import com.woomulwoomul.core.domain.user.UserRepository
import jakarta.validation.ConstraintViolationException
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

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
        )
        val answers = listOf(
            createAndSaveAnswer(questionAnswers[0], "답변1", ""),
            createAndSaveAnswer(questionAnswers[1], "", "답변2")
        )

        val pageRequest = PageRequest.of(null, 1)

        // when
        val response = answerService.getAllAnswers(admin.id!!, user.id!!, pageRequest)

        // then
        assertAll(
            {
                assertThat(response.total).isEqualTo(answers.size.toLong())
            },
            {
                assertThat(response.data)
                    .extracting("answerId", "answerText", "answerImageUrl", "answerUpdateDateTime", "answeredUserCnt",
                        "answeredUserImageUrls", "questionId", "questionText", "questionBackgroundColor", "categories")
                    .containsExactly(
                        tuple(answers[1].id!!, answers[1].text, answers[1].imageUrl, answers[1].updateDateTime, 1L,
                            listOf(questionAnswers[1].receiver.imageUrl), questions[1].id!!, questions[1].text,
                            questions[1].backgroundColor,
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

    @DisplayName("회원 ID와 답변 ID로 답변 조회가 정상 작동한다")
    @Test
    fun givenValid_whenGetAnswerByUserIdAndAnswerId_thenReturn() {
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
        val response = answerService.getAnswerByUserIdAndAnswerId(user.id!!, answer.id!!)

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

    @DisplayName("존재하지 않는 답변 ID로 답변을 조회하면 예외가 발생한다")
    @Test
    fun givenNonExistingAnswer_whenGetAnswerByUserIdAndAnswerId_thenThrow() {
        // given
        val user = createAndSaveUser("user","user@woomulwoomul.com")

        val answerId = 1L

        // when & then
        assertThatThrownBy { answerService.getAnswerByUserIdAndAnswerId(user.id!!, answerId) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(ANSWER_NOT_FOUND)
    }

    @DisplayName("회원 ID와 질문 ID로 답변 조회가 정상 작동한다")
    @Test
    fun givenValid_whenGetAnswerByUserIdAndQuestionId_thenReturn() {
        // given
        val admin = createAndSaveUser("admin", "admin@woomulwoomul.com")
        val user = createAndSaveUser("user", "user@woomulwoomul.com")

        val categories = listOf(
            createAndSaveCategory(admin, "카테고리1"),
            createAndSaveCategory(admin, "카테고리2"),
            createAndSaveCategory(admin, "카테고리3")
        )
        val question = createAndSaveQuestion(categories, admin, "질문")

        val questionAnswer = createAndSaveQuestionAnswer(user, admin, question)
        val answer = createAndSaveAnswer(questionAnswer, "답변1", "")

        // when
        val response = answerService.getAnswerByUserIdAndQuestionId(user.id!!, question.id!!)

        // then
        assertAll(
            {
                assertThat(response)
                    .extracting(
                        "answerId", "answerText", "answerImageUrl", "answerUpdateDateTime", "answeredUserCnt",
                        "answeredUserImageUrls", "questionId", "questionText", "questionBackgroundColor"
                    )
                    .containsExactly(
                        answer.id, answer.text, answer.imageUrl, answer.updateDateTime, 1L,
                        listOf(user.imageUrl), question.id, question.text, question.backgroundColor
                    )
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

    @DisplayName("존재하지 않는 질문 ID로 답변을 조회하면 예외가 발생한다")
    @Test
    fun givenNonExistingAnswer_whenGetAnswerByUserIdAndQuestionId_thenThrow() {
        // given
        val user = createAndSaveUser("user","user@woomulwoomul.com")

        val questionId = 1L

        // when & then
        assertThatThrownBy { answerService.getAnswerByUserIdAndQuestionId(user.id!!, questionId) }
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
        request.answerImageUrl = "a".repeat(501)

        // when & then
        assertThatThrownBy { answerService.createAnswer(receiverUserId, senderUserId, questionId, request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .message()
            .asString()
            .contains(ANSWER_IMAGE_URL_SIZE_INVALID.message)
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

    @DisplayName("답변 업데이트가 정상 작동한다")
    @Test
    fun givenValid_whenUpdateAnswer_thenReturn() {
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
        val answer = createAndSaveAnswer(questionAnswer, "답변", "")

        val request = createValidAnswerUpdateResponse()

        // when
        val response = answerService.updateAnswer(user.id!!, answer.id!!, request)

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

    @DisplayName("답변 내용이 280자 초과일시 답변 업데이트를 하면 예외가 발생한다")
    @Test
    fun givenGreaterThan280SizeAnswerText_whenUpdateAnswer_thenThrow() {
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
        val answer = createAndSaveAnswer(questionAnswer, "답변", "")

        val request = createValidAnswerUpdateResponse()
        request.answerText = "a".repeat(281)

        // when & then
        assertThatThrownBy { answerService.updateAnswer(user.id!!, answer.id!!, request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .message()
            .asString()
            .contains(ANSWER_TEXT_SIZE_INVALID.message)
    }

    @DisplayName("답변 이미지 URL이 501자 초과일시 답변 업데이트를 하면 예외가 발생한다")
    @Test
    fun givenGreaterThan500SizeAnswerImageUrl_whenUpdateAnswer_thenThrow() {
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
        val answer = createAndSaveAnswer(questionAnswer, "답변", "")

        val request = createValidAnswerUpdateResponse()
        request.answerImageUrl = "a".repeat(501)

        // when & then
        assertThatThrownBy { answerService.updateAnswer(user.id!!, answer.id!!, request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .message()
            .asString()
            .contains(ANSWER_IMAGE_URL_SIZE_INVALID.message)
    }

    @DisplayName("존재하지 않는 답변 업데이트를 하면 예외가 발생한다")
    @Test
    fun givenNonExistingAnswer_whenUpdateAnswer_thenThrow() {
        // given
        val user = createAndSaveUser("user","user@woomulwoomul.com")
        val answerId = 1L

        val request = createValidAnswerUpdateResponse()

        // when & then
        assertThatThrownBy { answerService.updateAnswer(user.id!!, answerId, request) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(ANSWER_NOT_FOUND)
    }

    @DisplayName("답변 삭제가 정상 작동한다")
    @Test
    fun givenValid_whenDeleteAnswer_thenReturn() {
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
        val answer = createAndSaveAnswer(questionAnswer, "답변", "")

        // when
        answerService.deleteAnswer(user.id!!, answer.id!!)

        // then
        assertAll(
            {
                assertThat(questionAnswerRepository.findById(questionAnswer.id!!).get())
                    .extracting("status")
                    .isEqualTo(DetailServiceStatus.USER_DEL)
            },
            {
                assertThat(answerRepository.findById(answer.id!!).get())
                    .extracting("status")
                    .isEqualTo(ServiceStatus.USER_DEL)
            }
        )
    }

    @DisplayName("존재하지 않는 답변을 삭제하면 예외가 발생한다")
    @Test
    fun givenNonExistingAnswer_whenDeleteAnswer_thenThrow() {
        // given
        val user = createAndSaveUser("user","user@woomulwoomul.com")
        val answerId = Long.MAX_VALUE

        // when & then
        assertThatThrownBy { answerService.deleteAnswer(user.id!!, answerId) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(ANSWER_NOT_FOUND)
    }

    @DisplayName("답변 이미지 업로드가 정상 작동한다")
    @Test
    fun givenValid_whenUploadImage_thenReturn() {
        // given
        val user = createAndSaveUser("user","user@woomulwoomul.com")
        val questionId = 1L

        val file = MockMultipartFile("file", "file.png", "image/png", ByteArray(1))

        // when
        val response = answerService.uploadImage(user.id!!, questionId, file)

        // then
        assertThat(response).isNotNull()
    }

    @DisplayName("파일 없이 답변 이미지 업로드를 하면 예외가 발생한다")
    @Test
    fun givenNullFile_whenUploadImage_thenReturn() {
        // given
        val user = createAndSaveUser("user","user@woomulwoomul.com")
        val questionId = 1L

        val file: MultipartFile? = null

        // when & then
        assertThatThrownBy { answerService.uploadImage(user.id!!, questionId, file) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(FILE_FIELD_REQUIRED)
    }

    @DisplayName("지원하지 않는 파일 타입으로 답변 이미지 업로드를 하면 예외가 발생한다")
    @Test
    fun givenUnsupportedImageType_whenUploadImage_thenThrow() {
        // given
        val user = createAndSaveUser("user","user@woomulwoomul.com")
        val questionId = 1L

        val file = MockMultipartFile("file", "file.doc", "file/doc", ByteArray(1))

        // when & then
        assertThatThrownBy { answerService.uploadImage(user.id!!, questionId, file) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(IMAGE_TYPE_UNSUPPORTED)
    }

    private fun createValidAnswerUpdateResponse(): AnswerUpdateServiceRequest {
        return AnswerUpdateServiceRequest("수정 답변", "")
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
                imageUrl = "https://t1.kakaocdn.net/account_images/default_profile.jpeg"
            )
        )
    }
}