package com.woomulwoomul.core.domain.question

import com.woomulwoomul.core.common.request.PageRequest
import com.woomulwoomul.core.domain.base.DetailServiceStatus.*
import com.woomulwoomul.core.domain.user.UserEntity
import com.woomulwoomul.core.domain.user.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.groups.Tuple.tuple
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

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

    @DisplayName("질문 ID와 회원 ID로 질문 답변 전체 조회가 정상 작동한다")
    @Test
    fun givenValid_whenFindAllAnswered_thenReturn() {
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

        val pageRequest = PageRequest.of(null, 1)

        // when
        val foundQuestionAnswers = questionAnswerRepository.findAllAnswered(user.id!!, pageRequest)
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

    @DisplayName("질문 ID와 회원 ID로 질문 답변 조회가 정상 작동한다")
    @Test
    fun givenValid_whenFindAnswered_thenReturn() {
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
        val foundQuestionAnswer = questionAnswerRepository.findAnswered(user.id!!, answer.id!!)

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
                    .containsExactly(questionAnswer.sender.id, questionAnswer.sender.nickname,
                            questionAnswer.sender.email, questionAnswer.sender.imageUrl,
                            questionAnswer.sender.introduction, questionAnswer.sender.status,
                            questionAnswer.sender.createDateTime, questionAnswer.sender.updateDateTime)
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

    @DisplayName("질문 ID로 답변한 회원들의 프로필 이미지들 조회가 정상 작동한다")
    @Test
    fun givenQuestionId_whenCountAnsweredUsers_thenReturn() {
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

    @DisplayName("질문 ID로 답변한 회원들수 조회가 정상 작동한다")
    @Test
    fun givenValid_whenCountAnsweredUser_thenReturn() {
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

    @DisplayName("질문 ID들로 답변한 회원들수 조회가 정상 작동한다")
    @Test
    fun givenValid_whenFindRandomAnsweredUserImageUrls_thenReturn() {
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
            QuestionEntity(user = user, text = text, backgroundColor = "0F0F0F")
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
            imageUrl = "https://t1.kakaocdn.net/account_images/default_profile.jpeg.twg.thumb.R640x640"
        )
        )
    }
}