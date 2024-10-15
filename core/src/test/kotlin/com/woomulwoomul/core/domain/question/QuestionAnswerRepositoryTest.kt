package com.woomulwoomul.core.domain.question

import com.woomulwoomul.core.common.request.PageCursorRequest
import com.woomulwoomul.core.common.request.PageOffsetRequest
import com.woomulwoomul.core.domain.base.DetailServiceStatus.COMPLETE
import com.woomulwoomul.core.domain.user.UserEntity
import com.woomulwoomul.core.domain.user.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.groups.Tuple.tuple
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.util.stream.Stream

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class QuestionAnswerRepositoryTest(
    @Autowired private val questionAnswerRepository: QuestionAnswerRepository,
    @Autowired private val userRepository: UserRepository,
    @Autowired private val categoryRepository: CategoryRepository,
    @Autowired private val questionRepository: QuestionRepository,
    @Autowired private val questionCategoryRepository: QuestionCategoryRepository,
    @Autowired private val answerRepository: AnswerRepository,
) {

    @Test
    fun `질문 ID로 질문 답변 전체 조회가 정상 작동한다`() {
        // given
        val admin = createAndSaveUser("admin","admin@woomulwoomul.com")
        val users = listOf(createAndSaveUser("user1","user1@woomulwoomul.com"),
            createAndSaveUser("user2","user2@woomulwoomul.com"),
            createAndSaveUser("user3","user3@woomulwoomul.com"),
            createAndSaveUser("user4","user4@woomulwoomul.com"),
            createAndSaveUser("user5","user5@woomulwoomul.com"))

        val categories = listOf(
            createAndSaveCategory(admin, "카테고리1"),
            createAndSaveCategory(admin, "카테고리2"),
            createAndSaveCategory(admin, "카테고리3")
        )
        val question = createAndSaveQuestion(categories, admin, "질문")
        val questionAnswers = listOf(
            createAndSaveQuestionAnswer(users[0], admin, question),
            createAndSaveQuestionAnswer(users[1], admin, question),
            createAndSaveQuestionAnswer(users[2], admin, question),
            createAndSaveQuestionAnswer(users[3], admin, question),
            createAndSaveQuestionAnswer(users[4], admin, question)
        )
        val answers = listOf(
            createAndSaveAnswer(questionAnswers[0], "답변1", ""),
            createAndSaveAnswer(questionAnswers[1], "", "답변2"),
            createAndSaveAnswer(questionAnswers[2], "답변3", ""),
            createAndSaveAnswer(questionAnswers[3], "", "답변4"),
            createAndSaveAnswer(questionAnswers[4], "답변5", "")
        )

        val pageOffsetRequest = PageOffsetRequest.of(2, 2)

        // when
        val foundQuestionAnswers = questionAnswerRepository.findAllByQuestionId(question.id!!, pageOffsetRequest)

        // then
        assertAll(
            {
                assertThat(foundQuestionAnswers.total)
                    .isEqualTo(questionAnswers.size.toLong())
            },
            {
                assertThat(foundQuestionAnswers.data)
                    .extracting("id", "status", "createDateTime", "updateDateTime")
                    .containsExactly(
                        tuple(questionAnswers[2].id, questionAnswers[2].status, questionAnswers[2].createDateTime,
                            questionAnswers[2].updateDateTime),
                        tuple(questionAnswers[1].id, questionAnswers[1].status, questionAnswers[1].createDateTime,
                            questionAnswers[1].updateDateTime)
                    )
            },
            {
                assertThat(foundQuestionAnswers.data)
                    .extracting("question")
                    .extracting("id", "text", "backgroundColor", "startDateTime", "endDateTime", "status",
                        "createDateTime", "updateDateTime")
                    .containsExactly(
                        tuple(question.id, question.text, question.backgroundColor, question.startDateTime,
                            question.endDateTime, question.status, question.createDateTime, question.updateDateTime),
                        tuple(question.id, question.text, question.backgroundColor, question.startDateTime,
                            question.endDateTime, question.status, question.createDateTime, question.updateDateTime)
                    )
            },
            {
                assertThat(foundQuestionAnswers.data)
                    .extracting("answer")
                    .extracting("id", "text", "imageUrl", "status", "createDateTime", "updateDateTime")
                    .containsExactly(
                        tuple(answers[2].id, answers[2].text, answers[2].imageUrl, answers[2].status,
                            answers[2].createDateTime, answers[2].updateDateTime),
                        tuple(answers[1].id, answers[1].text, answers[1].imageUrl, answers[1].status,
                            answers[1].createDateTime, answers[1].updateDateTime)
                    )
            },
            {
                assertThat(foundQuestionAnswers.data)
                    .extracting("receiver")
                    .extracting("id", "nickname", "email", "imageUrl", "introduction", "status", "createDateTime",
                        "updateDateTime")
                    .containsExactly(
                        tuple(users[2].id, users[2].nickname, users[2].email, users[2].imageUrl, users[2].introduction,
                            users[2].status, users[2].createDateTime, users[2].updateDateTime),
                        tuple(users[1].id, users[1].nickname, users[1].email, users[1].imageUrl, users[1].introduction,
                            users[1].status, users[1].createDateTime, users[1].updateDateTime)
                    )
            }
        )
    }

    @Test
    fun `질문 답변이 없을때 질문 ID로 질문 답변 전체 조회가 정상 작동한다`() {
        // given
        val admin = createAndSaveUser("admin","admin@woomulwoomul.com")
        val categories = listOf(createAndSaveCategory(admin, "카테고리"))
        val question = createAndSaveQuestion(categories, admin, "질문")

        val pageOffsetRequest = PageOffsetRequest.of(1, 20)

        // when
        val foundQuestionAnswers = questionAnswerRepository.findAllByQuestionId(question.id!!, pageOffsetRequest)

        // then
        assertAll(
            {
                assertThat(foundQuestionAnswers.total).isEqualTo(0L)
            },
            {
                assertThat(foundQuestionAnswers.data).isEmpty()
            }
        )
    }

    @Test
    fun `회원 ID와 답변 ID로 질문 답변 전체 조회가 정상 작동한다`() {
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
            createAndSaveQuestionAnswer(user, admin, questions[1])
        )
        val answers = listOf(
            createAndSaveAnswer(questionAnswers[0], "답변1", ""),
            createAndSaveAnswer(questionAnswers[1], "", "답변2")
        )

        val pageCursorRequest = PageCursorRequest.of(null, 1)

        // when
        val foundQuestionAnswers = questionAnswerRepository.findAllAnswered(user.id!!, pageCursorRequest)

        // then
        assertAll(
            {
                assertThat(foundQuestionAnswers.total)
                    .isEqualTo(answers.size.toLong())
            },
            {
                assertThat(foundQuestionAnswers.data)
                    .extracting("id", "status", "createDateTime", "updateDateTime")
                    .containsExactly(
                        tuple(questionAnswers[1].id, questionAnswers[1].status, questionAnswers[1].createDateTime,
                            questionAnswers[1].updateDateTime),
                    )
            },
            {
                assertThat(foundQuestionAnswers.data)
                    .extracting("receiver")
                    .extracting("id", "nickname", "email", "imageUrl", "introduction", "status", "createDateTime",
                        "updateDateTime")
                    .containsExactly(
                        tuple(user.id!!, user.nickname, user.email, user.imageUrl, user.introduction, user.status,
                            user.createDateTime, user.updateDateTime))
            },
            {
                assertThat(foundQuestionAnswers.data)
                    .extracting("sender")
                    .extracting("id", "nickname", "email", "imageUrl", "introduction", "status", "createDateTime",
                        "updateDateTime")
                    .containsExactly(
                        tuple(admin.id!!, admin.nickname, admin.email, admin.imageUrl, admin.introduction, admin.status,
                            admin.createDateTime, admin.updateDateTime)
                    )
            },
            {
                assertThat(foundQuestionAnswers.data)
                    .extracting("question")
                    .extracting("id", "text", "backgroundColor", "status", "createDateTime", "updateDateTime")
                    .containsExactly(
                        tuple(questions[1].id!!, questions[1].text, questions[1].backgroundColor, questions[1].status,
                            questions[1].createDateTime, questions[1].updateDateTime)
                    )
            },
            {
                assertThat(foundQuestionAnswers.data)
                    .extracting("answer")
                    .extracting("id", "text", "imageUrl", "status", "createDateTime", "updateDateTime")
                    .containsExactly(
                        tuple(answers[1].id!!, answers[1].text, answers[1].imageUrl, answers[1].status,
                            answers[1].createDateTime, answers[1].updateDateTime)
                    )
            }
        )
    }

    @Test
    fun `질문 답변이 없을때 회원 ID와 답변 ID로 질문 답변 전체 조회를 하면 정상 작동한다`() {
        // given
        val admin = createAndSaveUser("admin","admin@woomulwoomul.com")
        val user = createAndSaveUser("user","user@woomulwoomul.com")

        val pageCursorRequest = PageCursorRequest.of(null, 1)

        // when
        val foundQuestionAnswers = questionAnswerRepository.findAllAnswered(user.id!!, pageCursorRequest)
        // then
        assertAll(
            {
                assertThat(foundQuestionAnswers.total).isEqualTo(0)
            },
            {
                assertThat(foundQuestionAnswers.data).isEmpty()
            }
        )
    }

    @Test
    fun `답변 ID로 질문 답변 조회가 정상 작동한다`()  {
        // given
        val admin = createAndSaveUser("admin","admin@woomulwoomul.com")
        val user = createAndSaveUser("user","user@woomulwoomul.com")

        val categories = listOf(createAndSaveCategory(admin, "카테고리"))
        val question = createAndSaveQuestion(categories, admin, "질문")
        val questionAnswer = createAndSaveQuestionAnswer(user, admin, question)
        val answer = createAndSaveAnswer(questionAnswer, "답변", "")

        // when
        val foundQuestionAnswer = questionAnswerRepository.findByAnswerId(answer.id!!)

        // then
        assertAll(
            {
                assertThat(foundQuestionAnswer)
                    .extracting("id", "status", "createDateTime", "updateDateTime")
                    .containsExactly(questionAnswer.id, questionAnswer.status, questionAnswer.createDateTime,
                        questionAnswer.updateDateTime)
            },
            {
                assertThat(foundQuestionAnswer!!.answer)
                    .extracting("id", "text", "imageUrl", "status", "createDateTime", "updateDateTime")
                    .containsExactly(answer.id, answer.text, answer.imageUrl, answer.status, answer.createDateTime,
                        answer.updateDateTime)
            }
        )
    }

    @Test
    fun `회원 ID와 답변 ID로 질문 답변 조회가 정상 작동한다`() {
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
        val foundQuestionAnswer = questionAnswerRepository.findAnsweredByUserIdAndAnswerId(user.id!!, answer.id!!)

        // then
        assertAll(
            {
                assertThat(foundQuestionAnswer)
                    .extracting("id", "status", "createDateTime", "updateDateTime")
                    .containsExactly(questionAnswer.id, questionAnswer.status, questionAnswer.createDateTime,
                            questionAnswer.updateDateTime)
            },
            {
                assertThat(foundQuestionAnswer!!.receiver)
                    .extracting("id", "nickname", "email", "imageUrl", "introduction", "status", "createDateTime",
                        "updateDateTime")
                    .containsExactly(questionAnswer.receiver.id, questionAnswer.receiver.nickname,
                            questionAnswer.receiver.email, questionAnswer.receiver.imageUrl,
                            questionAnswer.receiver.introduction, questionAnswer.receiver.status,
                            questionAnswer.receiver.createDateTime, questionAnswer.receiver.updateDateTime)
            },
            {
                assertThat(foundQuestionAnswer!!.sender)
                    .extracting("id", "nickname", "email", "imageUrl", "introduction", "status", "createDateTime",
                        "updateDateTime")
                    .containsExactly(questionAnswer.sender!!.id, questionAnswer.sender!!.nickname,
                            questionAnswer.sender!!.email, questionAnswer.sender!!.imageUrl,
                            questionAnswer.sender!!.introduction, questionAnswer.sender!!.status,
                            questionAnswer.sender!!.createDateTime, questionAnswer.sender!!.updateDateTime)
            },
            {
                assertThat(foundQuestionAnswer!!.question)
                    .extracting("id", "text", "backgroundColor", "status", "createDateTime", "updateDateTime")
                    .containsExactly(questionAnswer.question.id, questionAnswer.question.text,
                        questionAnswer.question.backgroundColor, questionAnswer.question.status,
                        questionAnswer.question.createDateTime, questionAnswer.question.updateDateTime)
            },
            {
                assertThat(foundQuestionAnswer!!.answer)
                    .extracting("id", "text", "imageUrl", "status", "createDateTime", "updateDateTime")
                    .containsExactly(questionAnswer.answer!!.id, questionAnswer.answer!!.text,
                        questionAnswer.answer!!.imageUrl, questionAnswer.answer!!.status,
                        questionAnswer.answer!!.createDateTime, questionAnswer.answer!!.updateDateTime)
            }
        )
    }

    @Test
    fun `회원 ID와 질문 ID로 질문 답변 조회가 정상 작동한다`() {
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
        val foundQuestionAnswer = questionAnswerRepository.findAnsweredByUserIdAndQuestionId(user.id!!, question.id!!)

        // then
        assertAll(
            {
                assertThat(foundQuestionAnswer)
                    .extracting("id", "status", "createDateTime", "updateDateTime")
                    .containsExactly(questionAnswer.id, questionAnswer.status, questionAnswer.createDateTime,
                        questionAnswer.updateDateTime)
            },
            {
                assertThat(foundQuestionAnswer!!.receiver)
                    .extracting("id", "nickname", "email", "imageUrl", "introduction", "status", "createDateTime",
                        "updateDateTime")
                    .containsExactly(questionAnswer.receiver.id, questionAnswer.receiver.nickname,
                        questionAnswer.receiver.email, questionAnswer.receiver.imageUrl,
                        questionAnswer.receiver.introduction, questionAnswer.receiver.status,
                        questionAnswer.receiver.createDateTime, questionAnswer.receiver.updateDateTime)
            },
            {
                assertThat(foundQuestionAnswer!!.sender)
                    .extracting("id", "nickname", "email", "imageUrl", "introduction", "status", "createDateTime",
                        "updateDateTime")
                    .containsExactly(questionAnswer.sender!!.id, questionAnswer.sender!!.nickname,
                        questionAnswer.sender!!.email, questionAnswer.sender!!.imageUrl,
                        questionAnswer.sender!!.introduction, questionAnswer.sender!!.status,
                        questionAnswer.sender!!.createDateTime, questionAnswer.sender!!.updateDateTime)
            },
            {
                assertThat(foundQuestionAnswer!!.question)
                    .extracting("id", "text", "backgroundColor", "status", "createDateTime", "updateDateTime")
                    .containsExactly(questionAnswer.question.id, questionAnswer.question.text,
                        questionAnswer.question.backgroundColor, questionAnswer.question.status,
                        questionAnswer.question.createDateTime, questionAnswer.question.updateDateTime)
            },
            {
                assertThat(foundQuestionAnswer!!.answer)
                    .extracting("id", "text", "imageUrl", "status", "createDateTime", "updateDateTime")
                    .containsExactly(questionAnswer.answer!!.id, questionAnswer.answer!!.text,
                        questionAnswer.answer!!.imageUrl, questionAnswer.answer!!.status,
                        questionAnswer.answer!!.createDateTime, questionAnswer.answer!!.updateDateTime)
            }
        )
    }

    @Test
    fun `질문 ID로 답변한 회원들의 프로필 이미지들 조회가 정상 작동한다`() {
        // given
        val admin = createAndSaveUser("admin", "admin@woomulwoomul.com")
        val user1 = createAndSaveUser("user1", "user1@woomulwoomul.com")
        val user2 = createAndSaveUser("user2", "user2@woomulwoomul.com")
        val user3 = createAndSaveUser("user3", "user3@woomulwoomul.com")

        val categories = listOf(
            createAndSaveCategory(admin, "카테고리1"),
            createAndSaveCategory(admin, "카테고리2"),
            createAndSaveCategory(admin, "카테고리3")
        )
        val question = createAndSaveQuestion(categories, admin, "질문")
        val questionAnswers = listOf(
            createAndSaveQuestionAnswer(user1, admin, question),
            createAndSaveQuestionAnswer(user2, admin, question),
            createAndSaveQuestionAnswer(user3, admin, question),
        )
        listOf(
            createAndSaveAnswer(questionAnswers[0], "답변1", ""),
            createAndSaveAnswer(questionAnswers[1], "답변2", ""),
            createAndSaveAnswer(questionAnswers[2], "답변3", "")
        )

        // when
        val randomAnsweredImageUrls = questionAnswerRepository.findRandomAnsweredUserImageUrls(question.id!!, 3L)

        // then
        assertThat(randomAnsweredImageUrls).containsExactlyInAnyOrder(user1.imageUrl, user2.imageUrl, user3.imageUrl)
    }

    @Test
    fun `질문 ID로 답변한 회원들수 조회가 정상 작동한다`() {
        // given
        val admin = createAndSaveUser("admin", "admin@woomulwoomul.com")
        val user1 = createAndSaveUser("user1", "user1@woomulwoomul.com")
        val user2 = createAndSaveUser("user2", "user2@woomulwoomul.com")
        val user3 = createAndSaveUser("user3", "user3@woomulwoomul.com")

        val categories = listOf(
            createAndSaveCategory(admin, "카테고리1"),
            createAndSaveCategory(admin, "카테고리2"),
            createAndSaveCategory(admin, "카테고리3")
        )
        val question = createAndSaveQuestion(categories, admin, "질문")
        val questionAnswers = listOf(
            createAndSaveQuestionAnswer(user1, admin, question),
            createAndSaveQuestionAnswer(user2, admin, question),
            createAndSaveQuestionAnswer(user3, admin, question),
        )
        val answers = listOf(
            createAndSaveAnswer(questionAnswers[0], "답변1", ""),
            createAndSaveAnswer(questionAnswers[1], "답변2", ""),
            createAndSaveAnswer(questionAnswers[2], "답변3", "")
        )

        // when
        val answeredUserCnt = questionAnswerRepository.countAnsweredUser(question.id!!)

        // then
        assertThat(answeredUserCnt).isEqualTo(answers.size.toLong())
    }

    @Test
    fun `질문 ID들로 답변한 회원들수 조회가 정상 작동한다`() {
        // given
        val admin = createAndSaveUser("admin", "admin@woomulwoomul.com")
        val user1 = createAndSaveUser("user1", "user1@woomulwoomul.com")
        val user2 = createAndSaveUser("user2", "user2@woomulwoomul.com")
        val user3 = createAndSaveUser("user3", "user3@woomulwoomul.com")

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
            createAndSaveQuestionAnswer(user1, admin, questions[0]),
            createAndSaveQuestionAnswer(user2, admin, questions[0]),
            createAndSaveQuestionAnswer(user3, admin, questions[0]),
            createAndSaveQuestionAnswer(user1, admin, questions[1]),
            createAndSaveQuestionAnswer(user2, admin, questions[1]),
            createAndSaveQuestionAnswer(user1, admin, questions[2])
        )
        val answers = listOf(
            createAndSaveAnswer(questionAnswers[0], "답변1", ""),
            createAndSaveAnswer(questionAnswers[1], "답변2", ""),
            createAndSaveAnswer(questionAnswers[2], "답변3", ""),
            createAndSaveAnswer(questionAnswers[3], "답변4", ""),
            createAndSaveAnswer(questionAnswers[4], "답변5", ""),
            createAndSaveAnswer(questionAnswers[5], "답변6", "")
        )

        // when
        val answeredUserCnts = questionAnswerRepository.countAnsweredUsers(questions.map { it.id!! })

        // then
        assertThat(answeredUserCnts)
            .extracting("questionId", "userCnt")
            .containsExactlyInAnyOrder(
                tuple(questions[0].id, 3L),
                tuple(questions[1].id, 2L),
                tuple(questions[2].id, 1L),
            )
    }

    @ParameterizedTest(name = "[{index}] 수신자 회원 ID와 질문 ID로 질문 답변 존재 여부 조회를 하면 {0}를 반환한다")
    @MethodSource("providerExists")
    fun `수신자 회원 ID와 질문 ID로 질문 답변 존재 여부 조회가 정상 작동한다`(expected: Boolean) {
        // given
        val (receiverUserId, questionId) = if (expected) {
            val admin = createAndSaveUser("admin", "admin@woomulwoomul.com")
            val user = createAndSaveUser("user", "user@woomulwoomul.com")

            val categories = listOf(
                createAndSaveCategory(admin, "카테고리1"),
                createAndSaveCategory(admin, "카테고리2"),
                createAndSaveCategory(admin, "카테고리3")
            )
            val question = createAndSaveQuestion(categories, admin, "질문")
            val questionAnswer = createAndSaveQuestionAnswer(user, admin, question)
            createAndSaveAnswer(questionAnswer, "답변", "")

            Pair(user.id!!, question.id!!)
        } else {
            Pair(Long.MAX_VALUE, Long.MAX_VALUE)
        }

        // when
        val result = questionAnswerRepository.exists(receiverUserId, questionId)

        // then
        assertThat(result).isEqualTo(expected)
    }

    private fun createAndSaveAnswer(questionAnswer: QuestionAnswerEntity, text: String, imageUrl: String): AnswerEntity {
        val answer = answerRepository.save(AnswerEntity(text = text, imageUrl = imageUrl))

        questionAnswer.apply {
            status = COMPLETE
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
        return questionAnswerRepository.save(QuestionAnswerEntity(
            receiver = receiver,
            sender = sender,
            question = question
        ))
    }

    private fun createAndSaveQuestion(categories: List<CategoryEntity>, user: UserEntity, text: String): QuestionEntity {
        val question = questionRepository.save(
            QuestionEntity(user = user, text = text, backgroundColor = BackgroundColor.WHITE)
        )

        questionCategoryRepository.saveAll(
            categories.map { QuestionCategoryEntity(question = question, category = it) }
        )

        return question
    }

    private fun createAndSaveCategory(admin: UserEntity, name: String): CategoryEntity {
        return categoryRepository.save(CategoryEntity(admin = admin, name = name))
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

    companion object {
        @JvmStatic
        fun providerExists(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(true),
                Arguments.of(false)
            )
        }
    }
}