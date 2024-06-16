package com.woomulwoomul.woomulwoomulbackend.api.service.question

import com.woomulwoomul.woomulwoomulbackend.domain.question.*
import com.woomulwoomul.woomulwoomulbackend.domain.user.*
import com.woomulwoomul.woomulwoomulbackend.domain.user.Role.ADMIN
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.groups.Tuple
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class QuestionServiceTest(
    @Autowired private val questionService: QuestionService,
    @Autowired private val questionRepository: QuestionRepository,
    @Autowired private val questionCategoryRepository: QuestionCategoryRepository,
    @Autowired private val categoryRepository: CategoryRepository,
    @Autowired private val userRoleRepository: UserRoleRepository,
    @Autowired private val userRepository: UserRepository,
) {

    @DisplayName("빈 리스트로 기본 질문들 조회를 하면 정상 작동한다")
    @Test
    fun givenEmpty_whenGetDefaultQuestions_thenReturn() {
        // given
        val adminRole = createAndSaveUserRole(ADMIN)
        val questionCategory1 = createAndSaveQuestionCategory(adminRole.user, "질문1", backgroundColor = "000001")
        val questionCategory2 = createAndSaveQuestionCategory(adminRole.user, "질문2", backgroundColor = "000002")
        val questionCategory3 = createAndSaveQuestionCategory(adminRole.user, "질문3", backgroundColor = "000003")

        // when
        val response = questionService.getDefaultQuestions(emptyList())

        // then
        assertAll(
            {
                assertThat(response)
                    .extracting("questionId", "questionText", "backgroundColor")
                    .containsExactlyInAnyOrder(
                        Tuple.tuple(questionCategory1.question.id, questionCategory1.question.text,
                            questionCategory1.question.backgroundColor),
                        Tuple.tuple(questionCategory2.question.id, questionCategory2.question.text,
                            questionCategory2.question.backgroundColor),
                        Tuple.tuple(questionCategory3.question.id, questionCategory3.question.text,
                            questionCategory3.question.backgroundColor)
                    )
            },
            {
                assertThat(response)
                    .flatExtracting("categories")
                    .extracting("categoryId", "categoryName")
                    .containsExactlyInAnyOrder(
                        Tuple.tuple(questionCategory1.category.id, questionCategory1.category.name),
                        Tuple.tuple(questionCategory2.category.id, questionCategory2.category.name),
                        Tuple.tuple(questionCategory3.category.id, questionCategory3.category.name)
                    )
            }
        )
    }

    @DisplayName("질문 ID들로 기본 질문들 조회를 하면 정상 작동한다")
    @Test
    fun givenQuestionIds_whenGetDefaultQuestions_thenReturn() {
        // given
        val adminRole = createAndSaveUserRole(ADMIN)
        val questionCategory1 = createAndSaveQuestionCategory(adminRole.user, "질문1", backgroundColor = "000001")
        val questionCategory2 = createAndSaveQuestionCategory(adminRole.user, "질문2", backgroundColor = "000002")
        val questionCategory3 = createAndSaveQuestionCategory(adminRole.user, "질문3", backgroundColor = "000003")

        // when
        val response = questionService.getDefaultQuestions(listOf(
            questionCategory1.question.id!!,
            questionCategory2.question.id!!,
            questionCategory3.question.id!!
        ))

        // then
        assertAll(
            {
                assertThat(response)
                    .extracting("questionId", "questionText", "backgroundColor")
                    .containsExactlyInAnyOrder(
                        Tuple.tuple(questionCategory1.question.id, questionCategory1.question.text,
                            questionCategory1.question.backgroundColor),
                        Tuple.tuple(questionCategory2.question.id, questionCategory2.question.text,
                            questionCategory2.question.backgroundColor),
                        Tuple.tuple(questionCategory3.question.id, questionCategory3.question.text,
                            questionCategory3.question.backgroundColor)
                    )
            },
            {
                assertThat(response)
                    .flatExtracting("categories")
                    .extracting("categoryId", "categoryName")
                    .containsExactlyInAnyOrder(
                        Tuple.tuple(questionCategory1.category.id, questionCategory1.category.name),
                        Tuple.tuple(questionCategory2.category.id, questionCategory2.category.name),
                        Tuple.tuple(questionCategory3.category.id, questionCategory3.category.name)
                    )
            }
        )
    }

    private fun createAndSaveUserRole(
        role: Role,
        username: String = "tester",
        email: String = "tester@woomulwoomul.com",
    ): UserRoleEntity {
        val user = userRepository.save(
            UserEntity(
            username = username,
            email = email,
            imageUrl = "https://t1.kakaocdn.net/account_images/default_profile.jpeg.twg.thumb.R640x640"
        )
        )

        return userRoleRepository.save(UserRoleEntity(user = user, role = role))
    }

    private fun createAndSaveQuestionCategory(
        user: UserEntity,
        text: String = "질문",
        backgroundColor: String = "0F0F0F",
    ): QuestionCategoryEntity {
        val question = questionRepository.save(QuestionEntity(user = user, text = text, backgroundColor = backgroundColor))
        val category = categoryRepository.save(CategoryEntity(name = "카테고리명", admin = user))

        return questionCategoryRepository.save(QuestionCategoryEntity(question = question, category = category))
    }
}