package com.woomulwoomul.woomulwoomulbackend.domain.question

import com.woomulwoomul.woomulwoomulbackend.domain.user.*
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.groups.Tuple.tuple
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
class QuestionCategoryRepositoryTest(
    @Autowired private val questionCategoryRepository: QuestionCategoryRepository,
    @Autowired private val questionRepository: QuestionRepository,
    @Autowired private val categoryRepository: CategoryRepository,
    @Autowired private val userRepository: UserRepository,
    @Autowired private val userRoleRepository: UserRoleRepository,
) {

    @DisplayName("질문 ID로 질문 카테고리 조회를 하면 정상 작동한다")
    @Test
    fun givenValid_whenFindByQuestionIds_thenReturn() {
        // given
        val adminRole = createAndSaveUserRole(Role.ADMIN)
        val questionCategory1 = createAndSaveQuestionCategory(adminRole.user, "질문1", backgroundColor = "000001")
        val questionCategory2 = createAndSaveQuestionCategory(adminRole.user, "질문2", backgroundColor = "000002")
        val questionCategory3 = createAndSaveQuestionCategory(adminRole.user, "질문3", backgroundColor = "000003")

        // when
        val questionCategories = questionCategoryRepository.findByQuestionIds(listOf(
            questionCategory1.question.id!!,
            questionCategory2.question.id!!,
            questionCategory3.question.id!!
        ))

        // then
        assertAll(
            {
                assertThat(questionCategories)
                    .extracting("status", "createDateTime", "updateDateTime")
                    .containsExactlyInAnyOrder(
                        tuple(questionCategory1.status, questionCategory1.createDateTime,
                            questionCategory1.updateDateTime),
                        tuple(questionCategory2.status, questionCategory2.createDateTime,
                            questionCategory2.updateDateTime),
                        tuple(questionCategory3.status, questionCategory3.createDateTime,
                            questionCategory3.updateDateTime)
                    )
            }, {
                assertThat(questionCategories)
                    .extracting("question")
                    .extracting("text", "backgroundColor", "status", "createDateTime", "updateDateTime")
                    .containsExactlyInAnyOrder(
                        tuple(questionCategory1.question.text, questionCategory1.question.backgroundColor,
                            questionCategory1.question.status, questionCategory1.question.createDateTime,
                            questionCategory1.question.updateDateTime),
                        tuple(questionCategory2.question.text, questionCategory2.question.backgroundColor,
                            questionCategory2.question.status, questionCategory2.question.createDateTime,
                            questionCategory2.question.updateDateTime),
                        tuple(questionCategory3.question.text, questionCategory3.question.backgroundColor,
                            questionCategory3.question.status, questionCategory3.question.createDateTime,
                            questionCategory3.question.updateDateTime)
                    )
            }, {
                assertThat(questionCategories)
                    .extracting("category")
                    .extracting("name", "status", "createDateTime", "updateDateTime")
                    .containsExactlyInAnyOrder(
                        tuple(questionCategory1.category.name, questionCategory1.category.status,
                            questionCategory1.category.createDateTime, questionCategory1.category.updateDateTime),
                        tuple(questionCategory2.category.name, questionCategory2.category.status,
                            questionCategory2.category.createDateTime, questionCategory2.category.updateDateTime),
                        tuple(questionCategory3.category.name, questionCategory3.category.status,
                            questionCategory3.category.createDateTime, questionCategory3.category.updateDateTime)
                    )
            }
        )
    }

    @DisplayName("랜덤 질문 카테고리 조회를 하면 정상 작동한다")
    @Test
    fun givenValid_whenFindRandom_thenReturn() {
        // given
        val adminRole = createAndSaveUserRole(Role.ADMIN)
        val questionCategory1 = createAndSaveQuestionCategory(adminRole.user, "질문1", backgroundColor = "000001")
        val questionCategory2 = createAndSaveQuestionCategory(adminRole.user, "질문2", backgroundColor = "000002")
        val questionCategory3 = createAndSaveQuestionCategory(adminRole.user, "질문3", backgroundColor = "000003")

        val limit = 3L

        // when
        val questionCategories = questionCategoryRepository.findRandom(limit)

        // then
        assertAll(
            {
                assertThat(questionCategories)
                    .extracting("status", "createDateTime", "updateDateTime")
                    .containsExactlyInAnyOrder(
                        tuple(questionCategory1.status, questionCategory1.createDateTime,
                            questionCategory1.updateDateTime),
                        tuple(questionCategory2.status, questionCategory2.createDateTime,
                            questionCategory2.updateDateTime),
                        tuple(questionCategory3.status, questionCategory3.createDateTime,
                            questionCategory3.updateDateTime)
                    )
            }, {
                assertThat(questionCategories)
                    .extracting("question")
                    .extracting("text", "backgroundColor", "status", "createDateTime", "updateDateTime")
                    .containsExactlyInAnyOrder(
                        tuple(questionCategory1.question.text, questionCategory1.question.backgroundColor,
                            questionCategory1.question.status, questionCategory1.question.createDateTime,
                            questionCategory1.question.updateDateTime),
                        tuple(questionCategory2.question.text, questionCategory2.question.backgroundColor,
                            questionCategory2.question.status, questionCategory2.question.createDateTime,
                            questionCategory2.question.updateDateTime),
                        tuple(questionCategory3.question.text, questionCategory3.question.backgroundColor,
                            questionCategory3.question.status, questionCategory3.question.createDateTime,
                            questionCategory3.question.updateDateTime)
                    )
            }, {
                assertThat(questionCategories)
                    .extracting("category")
                    .extracting("name", "status", "createDateTime", "updateDateTime")
                    .containsExactlyInAnyOrder(
                        tuple(questionCategory1.category.name, questionCategory1.category.status,
                            questionCategory1.category.createDateTime, questionCategory1.category.updateDateTime),
                        tuple(questionCategory2.category.name, questionCategory2.category.status,
                            questionCategory2.category.createDateTime, questionCategory2.category.updateDateTime),
                        tuple(questionCategory3.category.name, questionCategory3.category.status,
                            questionCategory3.category.createDateTime, questionCategory3.category.updateDateTime)
                    )
            }
        )
    }

    private fun createAndSaveUserRole(
        role: Role,
        username: String = "tester",
        email: String = "tester@woomulwoomul.com",
    ): UserRoleEntity {
        val user = userRepository.save(UserEntity(
            username = username,
            email = email,
            imageUrl = "https://t1.kakaocdn.net/account_images/default_profile.jpeg.twg.thumb.R640x640"
        ))

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