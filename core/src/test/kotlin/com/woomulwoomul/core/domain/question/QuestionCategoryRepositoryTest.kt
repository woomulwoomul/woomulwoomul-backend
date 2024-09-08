package com.woomulwoomul.core.domain.question

import com.woomulwoomul.core.domain.user.*
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.groups.Tuple.tuple
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

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
                    .extracting("id", "status", "createDateTime", "updateDateTime")
                    .containsExactlyInAnyOrder(
                        tuple(questionCategory1.id, questionCategory1.status, questionCategory1.createDateTime,
                            questionCategory1.updateDateTime),
                        tuple(questionCategory2.id, questionCategory2.status, questionCategory2.createDateTime,
                            questionCategory2.updateDateTime),
                        tuple(questionCategory3.id, questionCategory3.status, questionCategory3.createDateTime,
                            questionCategory3.updateDateTime)
                    )
            }, {
                assertThat(questionCategories)
                    .extracting("question")
                    .extracting("id", "text", "backgroundColor", "status", "createDateTime", "updateDateTime")
                    .containsExactlyInAnyOrder(
                        tuple(questionCategory1.question.id, questionCategory1.question.text,
                            questionCategory1.question.backgroundColor, questionCategory1.question.status,
                            questionCategory1.question.createDateTime, questionCategory1.question.updateDateTime),
                        tuple(questionCategory2.question.id, questionCategory2.question.text,
                            questionCategory2.question.backgroundColor, questionCategory2.question.status,
                            questionCategory2.question.createDateTime, questionCategory2.question.updateDateTime),
                        tuple(questionCategory3.question.id, questionCategory3.question.text,
                            questionCategory3.question.backgroundColor, questionCategory3.question.status,
                            questionCategory3.question.createDateTime, questionCategory3.question.updateDateTime)
                    )
            }, {
                assertThat(questionCategories)
                    .extracting("category")
                    .extracting("id", "name", "status", "createDateTime", "updateDateTime")
                    .containsExactlyInAnyOrder(
                        tuple(questionCategory1.category.id, questionCategory1.category.name,
                            questionCategory1.category.status, questionCategory1.category.createDateTime,
                            questionCategory1.category.updateDateTime),
                        tuple(questionCategory2.category.id, questionCategory2.category.name,
                            questionCategory2.category.status, questionCategory2.category.createDateTime,
                            questionCategory2.category.updateDateTime),
                        tuple(questionCategory3.category.id, questionCategory3.category.name,
                            questionCategory3.category.status, questionCategory3.category.createDateTime,
                            questionCategory3.category.updateDateTime)
                    )
            }
        )
    }

    @DisplayName("질문 ID로 질문 카테고리 조회를 하면 정상 작동한다")
    @Test
    fun givenValid_whenFindByQuestionId_thenReturn() {
        // given
        val adminRole = createAndSaveUserRole(Role.ADMIN)
        val questionCategory = createAndSaveQuestionCategory(adminRole.user, "질문", backgroundColor = "000001")

        // when
        val foundQuestionCategories = questionCategoryRepository.findByQuestionId(questionCategory.question.id!!)

        // then
        assertAll(
            {
                assertThat(foundQuestionCategories)
                    .extracting("id", "status", "createDateTime", "updateDateTime")
                    .containsExactly(
                        tuple(questionCategory.id, questionCategory.status, questionCategory.createDateTime,
                            questionCategory.updateDateTime)
                    )
            }, {
                assertThat(foundQuestionCategories)
                    .extracting("question")
                    .extracting("id", "text", "backgroundColor", "status", "createDateTime", "updateDateTime")
                    .containsExactly(
                        tuple(questionCategory.question.id, questionCategory.question.text,
                            questionCategory.question.backgroundColor, questionCategory.question.status,
                            questionCategory.question.createDateTime, questionCategory.question.updateDateTime))
            }, {
                assertThat(foundQuestionCategories)
                    .extracting("category")
                    .extracting("id", "name", "status", "createDateTime", "updateDateTime")
                    .containsExactly(
                        tuple(questionCategory.category.id, questionCategory.category.name,
                            questionCategory.category.status, questionCategory.category.createDateTime,
                            questionCategory.category.updateDateTime)
                    )
            }, {
                assertThat(foundQuestionCategories)
                    .extracting("question")
                    .extracting("user")
                    .extracting("id", "nickname", "email", "imageUrl", "introduction", "status", "createDateTime",
                        "updateDateTime")
                    .containsExactly(
                        tuple(questionCategory.question.user.id, questionCategory.question.user.nickname,
                        questionCategory.question.user.email, questionCategory.question.user.imageUrl,
                        questionCategory.question.user.introduction, questionCategory.question.user.status,
                        questionCategory.question.user.createDateTime, questionCategory.question.user.updateDateTime)
                    )
            }
        )
    }

    @DisplayName("관리자 질문 카테고리 조회가 정상 작동한다")
    @Test
    fun givenValid_whenFindAdmin_thenReturn() {
        // given
        val now = LocalDateTime.now()
        val adminRole = createAndSaveUserRole(Role.ADMIN)

        val startDateTime = now.withHour(0).withMinute(0).withSecond(0)
        val endDateTime = now.withHour(23).withMinute(59).withSecond(59)
        val questionCategory1 = createAndSaveQuestionCategory(
            adminRole.user,
            "질문1",
            backgroundColor = "000001",
            startDateTime.minusDays(1),
            endDateTime.minusDays(1)
        )
        val questionCategory2 = createAndSaveQuestionCategory(
            adminRole.user,
            "질문2",
            backgroundColor = "000002",
            startDateTime,
            endDateTime
        )
        val questionCategory3 = createAndSaveQuestionCategory(
            adminRole.user,
            "질문3",
            backgroundColor = "000003",
            startDateTime.plusDays(1),
            endDateTime.plusDays(1)
        )

        // when
        val questionCategories = questionCategoryRepository.findAdmin(now)

        // then
        assertAll(
            {
                assertThat(questionCategories)
                    .extracting("id", "status", "createDateTime", "updateDateTime")
                    .containsExactlyInAnyOrder(
                        tuple(questionCategory2.id, questionCategory2.status, questionCategory2.createDateTime,
                            questionCategory2.updateDateTime)
                    )
            }, {
                assertThat(questionCategories)
                    .extracting("question")
                    .extracting("id", "text", "backgroundColor", "status", "createDateTime", "updateDateTime")
                    .containsExactlyInAnyOrder(
                        tuple(questionCategory2.question.id, questionCategory2.question.text,
                            questionCategory2.question.backgroundColor, questionCategory2.question.status,
                            questionCategory2.question.createDateTime, questionCategory2.question.updateDateTime)
                    )
            }, {
                assertThat(questionCategories)
                    .extracting("category")
                    .extracting("id", "name", "status", "createDateTime", "updateDateTime")
                    .containsExactlyInAnyOrder(
                        tuple(questionCategory2.category.id, questionCategory2.category.name, questionCategory2.category.status,
                            questionCategory2.category.createDateTime, questionCategory2.category.updateDateTime)
                    )
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

    private fun createAndSaveQuestionCategory(
        user: UserEntity,
        text: String = "질문",
        backgroundColor: String = "0F0F0F",
        startDateTime: LocalDateTime? = null,
        endDateTime: LocalDateTime? = null,
    ): QuestionCategoryEntity {
        val question = questionRepository.save(QuestionEntity(
            user = user,
            text = text,
            backgroundColor = backgroundColor,
            startDateTime = startDateTime,
            endDateTime = endDateTime
        ))
        val category = categoryRepository.save(CategoryEntity(name = "카테고리명", admin = user))

        return questionCategoryRepository.save(QuestionCategoryEntity(question = question, category = category))
    }
}