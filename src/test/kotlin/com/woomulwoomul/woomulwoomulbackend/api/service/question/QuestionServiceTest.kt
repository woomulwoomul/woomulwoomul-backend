package com.woomulwoomul.woomulwoomulbackend.api.service.question

import com.woomulwoomul.woomulwoomulbackend.api.service.question.request.QuestionUserCreateServiceRequest
import com.woomulwoomul.woomulwoomulbackend.common.constant.ExceptionCode.*
import com.woomulwoomul.woomulwoomulbackend.common.request.PageRequest
import com.woomulwoomul.woomulwoomulbackend.common.response.CustomException
import com.woomulwoomul.woomulwoomulbackend.domain.question.*
import com.woomulwoomul.woomulwoomulbackend.domain.user.*
import com.woomulwoomul.woomulwoomulbackend.domain.user.Role.ADMIN
import com.woomulwoomul.woomulwoomulbackend.domain.user.Role.USER
import jakarta.validation.ConstraintViolationException
import org.assertj.core.api.Assertions.*
import org.assertj.core.groups.Tuple
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
        val result = questionService.getDefaultQuestions(emptyList())

        // then
        assertAll(
            {
                assertThat(result)
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
                assertThat(result)
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
        val result = questionService.getDefaultQuestions(listOf(
            questionCategory1.question.id!!,
            questionCategory2.question.id!!,
            questionCategory3.question.id!!
        ))

        // then
        assertAll(
            {
                assertThat(result)
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
                assertThat(result)
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

    @DisplayName("전체 카테고리 조회가 정상 작동한다")
    @Test
    fun givenValid_whenFindAllCategories_thenReturn() {
        // given
        val adminRole = createAndSaveUserRole(ADMIN)
        val categories = listOf(
            createAndSaveCategory(adminRole.user, "카테고리1"),
            createAndSaveCategory(adminRole.user, "카테고리2"),
            createAndSaveCategory(adminRole.user, "카테고리3"),
            createAndSaveCategory(adminRole.user, "카테고리4"),
            createAndSaveCategory(adminRole.user, "카테고리5")
        )
        val pageRequest = PageRequest(1L, 2L)

        // when
        val result = questionService.getAllCategories(pageRequest)

        // then
        assertAll(
            {
                assertThat(result.total).isEqualTo(categories.size.toLong())
            },
            {
                assertThat(result.data)
                    .extracting("categoryId", "name")
                    .containsExactly(
                        tuple(categories[1].id, categories[1].name),
                        tuple(categories[2].id, categories[2].name),
                    )
            }
        )
    }

    @DisplayName("회원 질문 생성이 정상 작동한다")
    @Test
    fun givenValid_whenCreateUserQuestion_theReturn() {
        // given
        val adminRole = createAndSaveUserRole(ADMIN, "tester1", "tester1@woomulwoomul.com")
        val userRole = createAndSaveUserRole(USER, "tester2", "tester2@woomulwoomul.com")
        val categories = listOf(
            createAndSaveCategory(adminRole.user, "카테고리1"),
            createAndSaveCategory(adminRole.user, "카테고리2"),
            createAndSaveCategory(adminRole.user, "카테고리3"),
        )
        val categoryIds = categories.stream()
            .map { it.id!! }
            .toList()

        val request = createValidQuestionUserCreateServiceRequest(categoryIds)

        // when
        val response = questionService.createUserQuestion(userRole.user.id!!, request)

        // then
        assertAll(
            {
                assertThat(response.questionId).isNotNull()
            },
            {
                assertThat(response)
                    .extracting("questionText", "questionBackgroundColor")
                    .containsExactly(request.questionText, request.questionBackgroundColor)
            },
            {
                assertThat(response.categories)
                    .extracting("categoryId", "categoryName")
                    .containsExactlyInAnyOrder(
                        tuple(categories[0].id!!, categories[0].name),
                        tuple(categories[1].id!!, categories[1].name),
                        tuple(categories[2].id!!, categories[2].name),
                    )
            }
        )
    }

    @DisplayName("존재하지 않은 회원으로 회원 질문 생성을 하면 예외를 던진다")
    @Test
    fun givenNonExistingUser_whenCreateUserQuestion_thenThrow() {
        // given
        val adminRole = createAndSaveUserRole(ADMIN)
        val categories = listOf(
            createAndSaveCategory(adminRole.user, "카테고리1"),
            createAndSaveCategory(adminRole.user, "카테고리2"),
            createAndSaveCategory(adminRole.user, "카테고리3"),
        )
        val categoryIds = categories.stream()
            .map { it.id!! }
            .toList()

        val request = createValidQuestionUserCreateServiceRequest(categoryIds)

        // when & then
        assertThatThrownBy { questionService.createUserQuestion(Long.MAX_VALUE, request) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(USER_NOT_FOUND)
    }

    @DisplayName("존재하지 않은 카테고리로 회원 질문 생성을 하면 예외를 던진다")
    @Test
    fun givenNonExistingCategory_whenCreateUserQuestion_thenThrows() {
        // given
        val userRole = createAndSaveUserRole(USER, "tester", "tester@woomulwoomul.com")

        val request = createValidQuestionUserCreateServiceRequest(listOf(Long.MAX_VALUE))

        // when & then
        assertThatThrownBy { questionService.createUserQuestion(userRole.user.id!!, request) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(CATEGORY_NOT_FOUND)
    }

    @DisplayName("질문 내용 1바이트 미만으로 회원 질문 생성을 하면 예외를 던진다")
    @Test
    fun givenLesserThan1ByteSizeQuestionText_whenCreateUserQuestion_thenThrows() {
        // given
        val adminRole = createAndSaveUserRole(ADMIN, "tester1", "tester1@woomulwoomul.com")
        val userRole = createAndSaveUserRole(USER, "tester2", "tester2@woomulwoomul.com")
        val categories = listOf(
            createAndSaveCategory(adminRole.user, "카테고리1"),
            createAndSaveCategory(adminRole.user, "카테고리2"),
            createAndSaveCategory(adminRole.user, "카테고리3"),
        )
        val categoryIds = categories.stream()
            .map { it.id!! }
            .toList()

        val request = createValidQuestionUserCreateServiceRequest(categoryIds)
        request.questionText = ""

        // when & then
        assertThatThrownBy { questionService.createUserQuestion(userRole.user.id!!, request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .message()
            .asString()
            .contains(QUESTION_TEXT_BYTE_SIZE_INVALID.message)
    }

    @DisplayName("질문 내용 60바이트 초과로 회원 질문 생성을 하면 예외를 던진다")
    @Test
    fun givenGreaterThan60ByteSizeQuestionText_whenCreateUserQuestion_thenThrows() {
        // given
        val adminRole = createAndSaveUserRole(ADMIN, "tester1", "tester1@woomulwoomul.com")
        val userRole = createAndSaveUserRole(USER, "tester2", "tester2@woomulwoomul.com")
        val categories = listOf(
            createAndSaveCategory(adminRole.user, "카테고리1"),
            createAndSaveCategory(adminRole.user, "카테고리2"),
            createAndSaveCategory(adminRole.user, "카테고리3"),
        )
        val categoryIds = categories.stream()
            .map { it.id!! }
            .toList()

        val request = createValidQuestionUserCreateServiceRequest(categoryIds)
        request.questionText = "가".repeat(31)

        // when & then
        assertThatThrownBy { questionService.createUserQuestion(userRole.user.id!!, request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .message()
            .asString()
            .contains(QUESTION_TEXT_BYTE_SIZE_INVALID.message)
    }

    @DisplayName("질문 배경 색상 6자 미만으로 회원 질문 생성을 하면 예외를 던진다")
    @Test
    fun givenLesserThan6SizeQuestionBackgroundColor_whenCreateUserQuestion_thenThrows() {
        // given
        val adminRole = createAndSaveUserRole(ADMIN, "tester1", "tester1@woomulwoomul.com")
        val userRole = createAndSaveUserRole(USER, "tester2", "tester2@woomulwoomul.com")
        val categories = listOf(
            createAndSaveCategory(adminRole.user, "카테고리1"),
            createAndSaveCategory(adminRole.user, "카테고리2"),
            createAndSaveCategory(adminRole.user, "카테고리3"),
        )
        val categoryIds = categories.stream()
            .map { it.id!! }
            .toList()

        val request = createValidQuestionUserCreateServiceRequest(categoryIds)
        request.questionBackgroundColor = "0F0F0"

        // when & then
        assertThatThrownBy { questionService.createUserQuestion(userRole.user.id!!, request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .message()
            .asString()
            .contains(QUESTION_BACKGROUND_COLOR_SIZE_INVALID.message)
    }

    @DisplayName("질문 배경 색상 6자 초과로 회원 질문 생성을 하면 예외를 던진다")
    @Test
    fun givenGreaterThan6SizeQuestionBackgroundColor_whenCreateUserQuestion_thenThrows() {
        // given
        val adminRole = createAndSaveUserRole(ADMIN, "tester1", "tester1@woomulwoomul.com")
        val userRole = createAndSaveUserRole(USER, "tester2", "tester2@woomulwoomul.com")
        val categories = listOf(
            createAndSaveCategory(adminRole.user, "카테고리1"),
            createAndSaveCategory(adminRole.user, "카테고리2"),
            createAndSaveCategory(adminRole.user, "카테고리3"),
        )
        val categoryIds = categories.stream()
            .map { it.id!! }
            .toList()

        val request = createValidQuestionUserCreateServiceRequest(categoryIds)
        request.questionBackgroundColor = "0F0F0FF"

        // when & then
        assertThatThrownBy { questionService.createUserQuestion(userRole.user.id!!, request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .message()
            .asString()
            .contains(QUESTION_BACKGROUND_COLOR_SIZE_INVALID.message)
    }

    private fun createValidQuestionUserCreateServiceRequest(categoryIds: List<Long>): QuestionUserCreateServiceRequest {
        return QuestionUserCreateServiceRequest("질문", "0F0F0F", categoryIds)
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
    ): QuestionCategoryEntity {
        val question = questionRepository.save(QuestionEntity(user = user, text = text, backgroundColor = backgroundColor))
        val category = categoryRepository.save(CategoryEntity(name = "카테고리", admin = user))

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
            imageUrl = "https://t1.kakaocdn.net/account_images/default_profile.jpeg.twg.thumb.R640x640"
        )
        )

        return userRoleRepository.save(UserRoleEntity(user = user, role = role))
    }
}