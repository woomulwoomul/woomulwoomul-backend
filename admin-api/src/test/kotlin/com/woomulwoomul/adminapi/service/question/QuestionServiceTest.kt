package com.woomulwoomul.adminapi.service.question

import com.woomulwoomul.core.common.request.PageOffsetRequest
import com.woomulwoomul.core.domain.question.*
import com.woomulwoomul.core.domain.user.*
import org.assertj.core.api.Assertions
import org.assertj.core.groups.Tuple
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
class QuestionServiceTest(
    @Autowired private val questionService: QuestionService,
    @Autowired private val questionRepository: QuestionRepository,
    @Autowired private val questionCategoryRepository: QuestionCategoryRepository,
    @Autowired private val categoryRepository: CategoryRepository,
    @Autowired private val userRoleRepository: UserRoleRepository,
    @Autowired private val userRepository: UserRepository,
) {

    @DisplayName("전체 카테고리 조회가 정상 작동한다")
    @Test
    fun givenValid_whenFindAllCategories_thenReturn() {
        // given
        val adminRole = createAndSaveUserRole(Role.ADMIN)
        val categories = listOf(
            createAndSaveCategory(adminRole.user, "카테고리1"),
            createAndSaveCategory(adminRole.user, "카테고리2"),
            createAndSaveCategory(adminRole.user, "카테고리3"),
            createAndSaveCategory(adminRole.user, "카테고리4"),
            createAndSaveCategory(adminRole.user, "카테고리5")
        )
        val pageOffsetRequest = PageOffsetRequest.of(2, 2)

        // when
        val result = questionService.getAllCategories(pageOffsetRequest)

        // then
        assertAll(
            {
                Assertions.assertThat(result.total).isEqualTo(categories.size.toLong())
            },
            {
                Assertions.assertThat(result.data)
                    .extracting("id", "name")
                    .containsExactly(
                        tuple(categories[2].id, categories[2].name),
                        tuple(categories[1].id, categories[1].name),
                    )
            }
        )
    }

    @DisplayName("전체 질문 조회가 정상 작동한다")
    @Test
    fun givenValid_whenFindAllQuestions_thenReturn() {
        // given
        val now = LocalDateTime.now()
        val adminRole = createAndSaveUserRole(Role.ADMIN)
        val questionCategories = listOf(
            createAndSaveQuestionCategory(
                adminRole.user,
                "질문1",
                "000001",
                now.withHour(0).withMinute(0).withSecond(0),
                now.withHour(23).withMinute(59).withSecond(59),
                "카테고리1"
            ),
            createAndSaveQuestionCategory(
                adminRole.user,
                "질문2",
                "000002",
                null,
                null,
                "카테고리2"
            ),
            createAndSaveQuestionCategory(
                adminRole.user,
                "질문3",
                "000003",
                now.withHour(0).withMinute(0).withSecond(0),
                now.withHour(23).withMinute(59).withSecond(59),
                "카테고리3"
            )
        )

        val pageOffsetRequest = PageOffsetRequest.of(2, 1)

        // when
        val result = questionService.getAllQuestions(pageOffsetRequest)

        // then
        assertAll(
            {
                Assertions.assertThat(result.total).isEqualTo(questionCategories.size.toLong())
            },
            {
                Assertions.assertThat(result.data)
                    .extracting("id", "text", "backgroundColor", "userNickname", "categoryNames", "startDateTime",
                        "endDateTime", "status", "createDateTime", "updateDateTime")
                    .containsExactly(
                        tuple(questionCategories[1].question.id, questionCategories[1].question.text,
                            questionCategories[1].question.backgroundColor,
                            questionCategories[1].question.user.nickname, listOf(questionCategories[1].category.name),
                            questionCategories[1].question.startDateTime, questionCategories[1].question.endDateTime,
                            questionCategories[1].question.status, questionCategories[1].question.createDateTime,
                            questionCategories[1].question.updateDateTime)
                    )
            }
        )
    }

    private fun createAndSaveCategory(
        user: UserEntity,
        name: String = "카테고리"
    ): CategoryEntity {
        return categoryRepository.save(CategoryEntity(name = name, admin = user))
    }

    private fun createAndSaveQuestionCategory(
        user: UserEntity,
        text: String = "질문",
        backgroundColor: String = "0F0F0F",
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