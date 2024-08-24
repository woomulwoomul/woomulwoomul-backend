package com.woomulwoomul.clientserver.service.question

import com.woomulwoomul.clientserver.service.question.request.QuestionUserCreateServiceRequest
import com.woomulwoomul.core.common.constant.ExceptionCode.*
import com.woomulwoomul.core.common.request.PageRequest
import com.woomulwoomul.core.common.response.CustomException
import com.woomulwoomul.core.domain.question.*
import com.woomulwoomul.core.domain.user.*
import com.woomulwoomul.core.domain.user.Role.ADMIN
import com.woomulwoomul.core.domain.user.Role.USER
import jakarta.validation.ConstraintViolationException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
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
class QuestionServiceTest(
    @Autowired private val questionService: QuestionService,
    @Autowired private val questionRepository: QuestionRepository,
    @Autowired private val questionCategoryRepository: QuestionCategoryRepository,
    @Autowired private val categoryRepository: CategoryRepository,
    @Autowired private val userRoleRepository: UserRoleRepository,
    @Autowired private val userRepository: UserRepository,
) {

    @DisplayName("기본 질문들 조회를 하면 정상 작동한다")
    @Test
    fun givenValid_whenGetDefaultQuestion_thenReturn() {
        // given
        val adminRole = createAndSaveUserRole(ADMIN)
        val questionCategory = createAndSaveQuestionCategory(adminRole.user, "질문", backgroundColor = "000000")
        val questionId: Long? = null

        // when
        val result = questionService.getDefaultQuestion(questionId)

        // then
        assertAll(
            {
                assertThat(result)
                    .extracting("questionId", "questionText", "backgroundColor")
                    .containsExactly(questionCategory.question.id, questionCategory.question.text,
                            questionCategory.question.backgroundColor)
            },
            {
                assertThat(result.categories)
                    .extracting("categoryId", "categoryName")
                    .containsExactly(tuple(questionCategory.category.id, questionCategory.category.name))
            }
        )
    }

    @DisplayName("질문 ID로 기본 질문들 조회를 하면 정상 작동한다")
    @Test
    fun givenQuestionIds_whenGetDefaultQuestion_thenReturn() {
        // given
        val adminRole = createAndSaveUserRole(ADMIN)
        val questionCategory = createAndSaveQuestionCategory(adminRole.user, "질문1", backgroundColor = "000001")

        // when
        val result = questionService.getDefaultQuestion(questionCategory.question.id!!)

        // then
        assertAll(
            {
                assertThat(result)
                    .extracting("questionId", "questionText", "backgroundColor")
                    .containsExactly(questionCategory.question.id, questionCategory.question.text,
                        questionCategory.question.backgroundColor)
            },
            {
                assertThat(result.categories)
                    .extracting("categoryId", "categoryName")
                    .containsExactly(tuple(questionCategory.category.id, questionCategory.category.name))
            }
        )
    }

    @DisplayName("기본 질문이 존재하지 않는데 기본 질문들 조회를 하면 예외가 발생한다")
    @Test
    fun givenNonExistingQuestion_whenGetDefaultQuestion_thenReturn() {
        // given
        val questionId: Long? = null

        // when & then
        assertThatThrownBy { questionService.getDefaultQuestion(questionId) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(QUESTION_NOT_FOUND)
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
        val pageRequest = PageRequest.of(categories[2].id, 2)

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
                        tuple(categories[2].id, categories[2].name),
                        tuple(categories[1].id, categories[1].name),
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
            .contains(QUESTION_TEXT_SIZE_INVALID.message)
    }

    @DisplayName("질문 내용 1자 미만으로 회원 질문 생성을 하면 예외를 던진다")
    @Test
    fun givenLesserThan1SizeQuestionText_whenCreateUserQuestion_thenThrows() {
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
            .contains(QUESTION_TEXT_SIZE_INVALID.message)
    }

    @DisplayName("질문 내용 60자 초과로 회원 질문 생성을 하면 예외를 던진다")
    @Test
    fun givenGreaterThan60SizeQuestionText_whenCreateUserQuestion_thenThrows() {
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
        request.questionText = "가".repeat(61)

        // when & then
        assertThatThrownBy { questionService.createUserQuestion(userRole.user.id!!, request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .message()
            .asString()
            .contains(QUESTION_TEXT_SIZE_INVALID.message)
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

    @DisplayName("질문 카테고리 1개 미만으로 회원 질문 생성을 하면 예외를 던진다")
    @Test
    fun givenLesserThan1SizeCategoryIds_whenCreateUserQuestion_thenThrows() {
        // given
        val adminRole = createAndSaveUserRole(ADMIN, "tester1", "tester1@woomulwoomul.com")
        val userRole = createAndSaveUserRole(USER, "tester2", "tester2@woomulwoomul.com")
        val categoryIds = emptyList<Long>()

        val request = createValidQuestionUserCreateServiceRequest(categoryIds)

        // when & then
        assertThatThrownBy { questionService.createUserQuestion(userRole.user.id!!, request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .message()
            .asString()
            .contains(CATEGORY_IDS_SIZE_INVALID.message)
    }

    @DisplayName("질문 카테고리 3개 초과로 회원 질문 생성을 하면 예외를 던진다")
    @Test
    fun givenGreaterThan3SizeCategoryIds_whenCreateUserQuestion_thenThrows() {
        // given
        val adminRole = createAndSaveUserRole(ADMIN, "tester1", "tester1@woomulwoomul.com")
        val userRole = createAndSaveUserRole(USER, "tester2", "tester2@woomulwoomul.com")
        val categories = listOf(
            createAndSaveCategory(adminRole.user, "카테고리1"),
            createAndSaveCategory(adminRole.user, "카테고리2"),
            createAndSaveCategory(adminRole.user, "카테고리3"),
            createAndSaveCategory(adminRole.user, "카테고리4"),
        )
        val categoryIds = categories.stream()
            .map { it.id!! }
            .toList()

        val request = createValidQuestionUserCreateServiceRequest(categoryIds)

        // when & then
        assertThatThrownBy { questionService.createUserQuestion(userRole.user.id!!, request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .message()
            .asString()
            .contains(CATEGORY_IDS_SIZE_INVALID.message)
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