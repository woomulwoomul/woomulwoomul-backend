package com.woomulwoomul.woomulwoomulbackend.domain.question

import com.woomulwoomul.woomulwoomulbackend.common.request.PageRequest
import com.woomulwoomul.woomulwoomulbackend.domain.base.DetailServiceStatus.*
import com.woomulwoomul.woomulwoomulbackend.domain.user.UserEntity
import com.woomulwoomul.woomulwoomulbackend.domain.user.UserRepository
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

    @DisplayName("질문 ID로 질문 카테고리 조회를 하면 정상 작동한다")
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

        val pageRequest = PageRequest.of(0, 1)

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
                    .extracting("status", "createDateTime", "updateDateTime")
                    .containsExactly(
                        tuple(questionAnswers[1].status, questionAnswers[1].createDateTime, questionAnswers[1].updateDateTime),
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
        return categoryRepository.save(
            CategoryEntity(admin = admin, name = name,)
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