package com.woomulwoomul.clientapi.service.question

import com.woomulwoomul.clientapi.service.question.request.QuestionUserCreateServiceRequest
import com.woomulwoomul.core.domain.question.BackgroundColor
import com.woomulwoomul.core.common.constant.ExceptionCode.*
import com.woomulwoomul.core.common.request.PageCursorRequest
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

    @Test
    fun `현재 날짜 기준 질문이 있을시 기본 질문들 조회를 하면 정상 작동한다`() {
        // given
        val now = LocalDateTime.now()
        val adminRole = createAndSaveUserRole(ADMIN)
        val questionCategory = createAndSaveQuestionCategory(
            adminRole.user,
            "질문",
            backgroundColor = BackgroundColor.WHITE.value,
            now.withHour(0).withMinute(0).withSecond(0),
            now.withHour(23).withMinute(59).withSecond(59)
        )
        val questionId: Long? = null

        // when
        val result = questionService.getDefaultQuestion(questionId, now)

        // then
        assertAll(
            {
                assertThat(result)
                    .extracting("questionId", "questionText", "backgroundColor")
                    .containsExactly(questionCategory.question.id, questionCategory.question.text,
                        questionCategory.question.backgroundColor.value)
            },
            {
                assertThat(result.categories)
                    .extracting("categoryId", "categoryName")
                    .containsExactly(tuple(questionCategory.category.id, questionCategory.category.name))
            }
        )
    }

    @Test
    fun `현재 날짜 기준 질문이 없을시 기본 질문 조회를 하면 정상 작동한다`() {
        // given
        val now = LocalDateTime.now()
        val adminRole = createAndSaveUserRole(ADMIN)
        val questionCategory = createAndSaveQuestionCategory(
            adminRole.user,
            "질문",
            BackgroundColor.WHITE.value,
            now.minusDays(2),
            now.minusDays(1)
        )
        val questionId: Long? = null

        // when
        val result = questionService.getDefaultQuestion(questionId, now)

        // then
        assertAll(
            {
                assertThat(result)
                    .extracting("questionId", "questionText", "backgroundColor")
                    .containsExactly(questionCategory.question.id, questionCategory.question.text,
                            questionCategory.question.backgroundColor.value)
            },
            {
                assertThat(result.categories)
                    .extracting("categoryId", "categoryName")
                    .containsExactly(tuple(questionCategory.category.id, questionCategory.category.name))
            }
        )
    }

    @Test
    fun `질문 ID로 기본 질문들 조회를 하면 정상 작동한다`() {
        // given
        val now = LocalDateTime.now()
        val adminRole = createAndSaveUserRole(ADMIN)
        val questionCategory = createAndSaveQuestionCategory(adminRole.user, "질문1")

        // when
        val result = questionService.getDefaultQuestion(questionCategory.question.id!!, now)

        // then
        assertAll(
            {
                assertThat(result)
                    .extracting("questionId", "questionText", "backgroundColor")
                    .containsExactly(questionCategory.question.id, questionCategory.question.text,
                        questionCategory.question.backgroundColor.value)
            },
            {
                assertThat(result.categories)
                    .extracting("categoryId", "categoryName")
                    .containsExactly(tuple(questionCategory.category.id, questionCategory.category.name))
            }
        )
    }

    @Test
    fun `기본 질문이 존재하지 않는데 기본 질문들 조회를 하면 예외가 발생한다`() {
        // given
        val now = LocalDateTime.now()
        val questionId: Long? = null

        // when & then
        assertThatThrownBy { questionService.getDefaultQuestion(questionId, now) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(QUESTION_NOT_FOUND)
    }

    @Test
    fun `전체 카테고리 조회가 정상 작동한다`() {
        // given
        val adminRole = createAndSaveUserRole(ADMIN)
        val categories = listOf(
            createAndSaveCategory(adminRole.user, "카테고리1"),
            createAndSaveCategory(adminRole.user, "카테고리2"),
            createAndSaveCategory(adminRole.user, "카테고리3"),
            createAndSaveCategory(adminRole.user, "카테고리4"),
            createAndSaveCategory(adminRole.user, "카테고리5")
        )
        val pageCursorRequest = PageCursorRequest.of(categories[2].id, 2)

        // when
        val result = questionService.getAllCategories(pageCursorRequest)

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

    @Test
    fun `회원 질문 생성이 정상 작동한다`() {
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

    @Test
    fun `존재하지 않은 회원으로 회원 질문 생성을 하면 예외를 던진다`() {
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

    @Test
    fun `존재하지 않은 카테고리로 회원 질문 생성을 하면 예외를 던진다`() {
        // given
        val userRole = createAndSaveUserRole(USER, "tester", "tester@woomulwoomul.com")

        val request = createValidQuestionUserCreateServiceRequest(listOf(Long.MAX_VALUE))

        // when & then
        assertThatThrownBy { questionService.createUserQuestion(userRole.user.id!!, request) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(CATEGORY_NOT_FOUND)
    }

    @Test
    fun `질문 내용 1바이트 미만으로 회원 질문 생성을 하면 예외를 던진다`() {
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

    @Test
    fun `질문 내용 1자 미만으로 회원 질문 생성을 하면 예외를 던진다`() {
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

    @Test
    fun `질문 내용 60자 초과로 회원 질문 생성을 하면 예외를 던진다`() {
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

    @Test
    fun `잘못된 포맷인 질문 배경 색상으로 회원 질문 생성을 하면 예외를 던진다`() {
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
        request.questionBackgroundColor = "XXXXXX"

        // when & then
        assertThatThrownBy { questionService.createUserQuestion(userRole.user.id!!, request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .message()
            .asString()
            .contains(QUESTION_BACKGROUND_COLOR_PATTERN_INVALID.message)
    }

    @Test
    fun `질문 카테고리 1개 미만으로 회원 질문 생성을 하면 예외를 던진다`() {
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

    @Test
    fun `질문 카테고리 3개 초과로 회원 질문 생성을 하면 예외를 던진다`() {
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
        return QuestionUserCreateServiceRequest("질문", BackgroundColor.WHITE.value, categoryIds)
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
        backgroundColor: String = BackgroundColor.WHITE.value,
        startDateTime: LocalDateTime? = null,
        endDateTime: LocalDateTime? = null,
    ): QuestionCategoryEntity {
        val question = questionRepository.save(QuestionEntity(
            user = user,
            text = text,
            backgroundColor = BackgroundColor.of(backgroundColor),
            startDateTime = startDateTime,
            endDateTime = endDateTime
        ))
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
            imageUrl = "https://t1.kakaocdn.net/account_images/default_profile.jpeg"
        )
        )

        return userRoleRepository.save(UserRoleEntity(user = user, role = role))
    }
}