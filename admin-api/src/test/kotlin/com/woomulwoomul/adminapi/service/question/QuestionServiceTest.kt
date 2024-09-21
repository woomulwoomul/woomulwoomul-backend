package com.woomulwoomul.adminapi.service.question

import com.woomulwoomul.adminapi.service.question.request.CategoryCreateServiceRequest
import com.woomulwoomul.adminapi.service.question.request.CategoryUpdateServiceRequest
import com.woomulwoomul.adminapi.service.question.request.QuestionUpdateServiceRequest
import com.woomulwoomul.core.common.constant.ExceptionCode.*
import com.woomulwoomul.core.common.request.PageOffsetRequest
import com.woomulwoomul.core.common.response.CustomException
import com.woomulwoomul.core.domain.base.ServiceStatus
import com.woomulwoomul.core.domain.base.ServiceStatus.ACTIVE
import com.woomulwoomul.core.domain.base.ServiceStatus.ADMIN_DEL
import com.woomulwoomul.core.domain.question.*
import com.woomulwoomul.core.domain.user.*
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
                assertThat(result.total).isEqualTo(categories.size.toLong())
            },
            {
                assertThat(result.data)
                    .extracting("id", "name", "status", "createDateTime", "updateDateTime")
                    .containsExactly(
                        tuple(categories[2].id, categories[2].name, categories[2].status, categories[2].createDateTime,
                            categories[2].updateDateTime),
                        tuple(categories[1].id, categories[1].name, categories[1].status, categories[1].createDateTime,
                            categories[1].updateDateTime),
                    )
            }
        )
    }

    @DisplayName("카테고리 조회가 정상 작동한다")
    @Test
    fun givenValid_whenFindCategory_thenReturn() {
        // given
        val adminRole = createAndSaveUserRole(Role.ADMIN)
        val category = createAndSaveCategory(adminRole.user, "카테고리")

        // when
        val result = questionService.getCategory(category.id!!)

        // then
        assertThat(result)
            .extracting("id", "name", "adminNickname", "status", "createDateTime", "updateDateTime")
            .containsExactly(result.id, result.name, result.adminNickname, result.status, result.createDateTime,
                result.updateDateTime)
    }

    @DisplayName("존재하지 않는 카테고리 ID로 카테고리 조회를 하면 예외가 발생한다")
    @Test
    fun givenNonExistingCategoryId_whenFindCategory_thenThrow() {
        // given
        val categoryId = Long.MAX_VALUE

        // when & then
        assertThatThrownBy { questionService.getCategory(categoryId) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(CATEGORY_NOT_FOUND)
    }

    @DisplayName("카테고리 생성이 정상 작동한다")
    @Test
    fun givenValid_whenCreateCategory_thenReturn() {
        // given
        val adminRole = createAndSaveUserRole(Role.ADMIN)
        val request = createValidCategoryCreateServiceRequest("카테고리")

        // when
        questionService.createCategory(adminRole.user.id!!, request)

        // then
        assertThat(categoryRepository.exists(request.categoryName)).isTrue()
    }

    @DisplayName("1자 미만인 카테고리명으로 카테고리 생성을 하면 예외가 발생한다")
    @Test
    fun givenLesserThan1SizeCategoryName_whenCreateCategory_thenThrow() {
        // given
        val adminRole = createAndSaveUserRole(Role.ADMIN)
        val request = createValidCategoryCreateServiceRequest("")

        // when & then
        assertThatThrownBy { questionService.createCategory(adminRole.user.id!!, request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .message()
            .asString()
            .contains(CATEGORY_NAME_SIZE_INVALID.message)
    }

    @DisplayName("10자 초과인 카테고리명으로 카테고리 생성을 하면 예외가 발생한다")
    @Test
    fun givenGreaterThan10SizeCategoryName_whenCreateCategory_thenThrow() {
        // given
        val adminRole = createAndSaveUserRole(Role.ADMIN)
        val request = createValidCategoryCreateServiceRequest("카".repeat(11))

        // when & then
        assertThatThrownBy { questionService.createCategory(adminRole.user.id!!, request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .message()
            .asString()
            .contains(CATEGORY_NAME_SIZE_INVALID.message)
    }

    @DisplayName("존재하지 않은 관리자로 카테고리 생성을 하면 예외가 발생한다")
    @Test
    fun givenNonExistingAdmin_whenCreateCategory_thenThrow() {
        // given
        val adminId = 1L
        val request = createValidCategoryCreateServiceRequest("카테고리")

        // when & then
        assertThatThrownBy { questionService.createCategory(adminId, request) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(ADMIN_NOT_FOUND)
    }

    @DisplayName("존재하는 카테고리명으로 카테고리 생성을 하면 예외가 발생한다")
    @Test
    fun givenExistingCategory_whenCreateCategory_thenThrow() {
        // given
        val adminRole = createAndSaveUserRole(Role.ADMIN)
        val category = createAndSaveCategory(adminRole.user, "카테고리")
        val request = createValidCategoryCreateServiceRequest(category.name)

        // when & then
        assertThatThrownBy { questionService.createCategory(adminRole.user.id!!, request) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(EXISTING_CATEGORY)
    }

    @DisplayName("카테고리 업데이트가 정상 작동한다")
    @Test
    fun givenValid_whenUpdateCategory_thenReturn() {
        // given
        val adminRole = createAndSaveUserRole(Role.ADMIN)
        val category = createAndSaveCategory(adminRole.user, "카테고리")
        val request = createValidCategoryUpdateServiceRequest()

        // when
        questionService.updateCategory(category.id!!, request)

        // then
        assertThat(category)
            .extracting("name")
            .isEqualTo(request.categoryName)
    }

    @DisplayName("1자 미만인 카테고리명으로 카테고리 업데이트를 하면 예외가 발생한다")
    @Test
    fun givenLesserThan1SizeCategoryName_whenUpdateCategory_thenThrow() {
        // given
        val adminRole = createAndSaveUserRole(Role.ADMIN)
        val category = createAndSaveCategory(adminRole.user, "카테고리")
        val request = createValidCategoryUpdateServiceRequest("")

        // when & then
        assertThatThrownBy { questionService.updateCategory(category.id!!, request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .message()
            .asString()
            .contains(CATEGORY_NAME_SIZE_INVALID.message)
    }

    @DisplayName("10자 초과인 카테고리명으로 카테고리 업데이트를 하면 예외가 발생한다")
    @Test
    fun givenGreaterThan10SizeCategoryName_whenUpdateCategory_thenThrow() {
        // given
        val adminRole = createAndSaveUserRole(Role.ADMIN)
        val category = createAndSaveCategory(adminRole.user, "카테고리")
        val request = createValidCategoryUpdateServiceRequest("카".repeat(11))

        // when & then
        assertThatThrownBy { questionService.updateCategory(category.id!!, request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .message()
            .asString()
            .contains(CATEGORY_NAME_SIZE_INVALID.message)
    }

    @DisplayName("잘못된 상태 포맷으로 카테고리 업데이트를 하면 예외가 발생한다")
    @Test
    fun givenPatternStatus_whenUpdateCategory_thenReturn() {
        // given
        val adminRole = createAndSaveUserRole(Role.ADMIN)
        val category = createAndSaveCategory(adminRole.user, "카테고리")
        val request = createValidCategoryUpdateServiceRequest("카테고리", "WRONG_STATUS")

        // when
        assertThatThrownBy { questionService.updateCategory(category.id!!, request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .message()
            .asString()
            .contains(CATEGORY_STATUS_PATTERN_INVALID.message)
    }

    @DisplayName("존재하지 않는 카테고리 ID로 카테고리 업데이트를 하면 예외가 발생한다")
    @Test
    fun givenNonExistingCategoryId_whenUpdateCategory_thenThrow() {
        // given
        val categoryId = Long.MAX_VALUE
        val request = createValidCategoryUpdateServiceRequest("카테고리")

        // when & then
        assertThatThrownBy { questionService.updateCategory(categoryId, request) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(CATEGORY_NOT_FOUND)
    }

    @DisplayName("카테고리 삭제가 정상 작동한다")
    @Test
    fun givenValid_whenDeleteCategory_thenReturn() {
        // given
        val adminRole = createAndSaveUserRole(Role.ADMIN)
        val category = createAndSaveCategory(adminRole.user, "카테고리")

        // when
        questionService.deleteCategory(category.id!!)

        // then
        assertThat(category)
            .extracting("status")
            .isEqualTo(ADMIN_DEL)
    }

    @DisplayName("존재하지 않는 카테고리 ID로 카테고리 삭제를 하면 예외가 발생한다")
    @Test
    fun givenNonExistingCategoryId_whenDeleteCategory_thenThrow() {
        // given
        val categoryId = Long.MAX_VALUE

        // when & then
        assertThatThrownBy { questionService.deleteCategory(categoryId) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(CATEGORY_NOT_FOUND)
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
                assertThat(result.total).isEqualTo(questionCategories.size.toLong())
            },
            {
                assertThat(result.data)
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

    @DisplayName("질문 조회가 정상 작동한다")
    @Test
    fun givenValid_whenFindQuestion_thenReturn() {
        // given
        val now = LocalDateTime.now()
        val adminRole = createAndSaveUserRole(Role.ADMIN)
        val questionCategory = createAndSaveQuestionCategory(
            adminRole.user,
            "질문",
            "000000",
            now.withHour(0).withMinute(0).withSecond(0),
            now.withHour(23).withMinute(59).withSecond(59),
            "카테고리1"
        )

        val categories = listOf(
            questionCategory.category,
            createAndSaveCategory(adminRole.user, "카테고리2"),
            createAndSaveCategory(adminRole.user, "카테고리3")
        )

        // when
        val result = questionService.getQuestion(questionCategory.question.id!!)

        // then
        assertThat(result)
            .extracting("id", "text", "backgroundColor", "userNickname", "categoryNames", "startDateTime",
                "endDateTime", "status", "createDateTime", "updateDateTime", "availableCategoryNames")
            .containsExactly(questionCategory.question.id, questionCategory.question.text,
                questionCategory.question.backgroundColor, questionCategory.question.user.nickname,
                listOf(questionCategory.category.name), questionCategory.question.startDateTime,
                questionCategory.question.endDateTime, questionCategory.question.status,
                questionCategory.question.createDateTime, questionCategory.question.updateDateTime,
                categories.map(CategoryEntity::name))
    }

    @DisplayName("존재하지 않는 질문 ID로 질문 조회를 하면 예외가 발생한다")
    @Test
    fun givenNonExistingQuestionId_whenFindQuestion_thenThrow() {
        // given
        val questionId = Long.MAX_VALUE

        // when & then
        assertThatThrownBy { questionService.getQuestion(questionId) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(QUESTION_NOT_FOUND)
    }

    @DisplayName("질문 업데이트가 정상 작동한다")
    @Test
    fun givenValid_whenUpdateQuestion_thenReturn() {
        // given
        val now = LocalDateTime.now()
        val adminRole = createAndSaveUserRole(Role.ADMIN)
        val questionCategory = createAndSaveQuestionCategory(
            adminRole.user,
            "질문",
            "000000",
            now.withHour(0).withMinute(0).withSecond(0),
            now.withHour(23).withMinute(59).withSecond(59),
            "카테고리1"
        )
        val categories = listOf(
            createAndSaveCategory(adminRole.user, "카테고리2"),
            createAndSaveCategory(adminRole.user, "카테고리3")
        )

        val request = createValidQuestionUpdateServiceRequest(categories.map(CategoryEntity::name))

        // when
        questionService.updateQuestion(questionCategory.question.id!!, request)

        // then
        val questionCategories = questionCategoryRepository.findByQuestionId(questionCategory.question.id!!)
        val question = questionCategories.first().question

        assertAll(
            {
                assertThat(question)
                    .extracting("text", "backgroundColor", "startDateTime", "endDateTime", "status")
                    .containsExactly(request.questionText, request.questionBackgroundColor,
                        request.questionStartDateTime, request.questionEndDateTime,
                        ServiceStatus.valueOf(request.questionStatus))
            }, {
                assertThat(questionCategories)
                    .extracting("category")
                    .extracting("name")
                    .containsExactlyInAnyOrder(categories[0].name, categories[1].name)
            }
        )
    }

    @DisplayName("내용이 1자 미만일 경우 질문 업데이트를 하면 예외가 발생한다")
    @Test
    fun givenLesserThan1SizeQuestionText_whenUpdateQuestion_thenThrow() {
        // given
        val questionId = 1L
        val request = createValidQuestionUpdateServiceRequest(listOf("카테고리"))
        request.questionText = ""

        // when & then
        assertThatThrownBy { questionService.updateQuestion(questionId, request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .message()
            .asString()
            .contains(QUESTION_TEXT_SIZE_INVALID.message)
    }

    @DisplayName("내용이 60자 초과일 경우 질문 업데이트를 하면 예외가 발생한다")
    @Test
    fun givenGreaterThan60SizeQuestionText_whenUpdateQuestion_thenThrow() {
        // given
        val questionId = 1L
        val request = createValidQuestionUpdateServiceRequest(listOf("카테고리"))
        request.questionText = "가".repeat(61)

        // when & then
        assertThatThrownBy { questionService.updateQuestion(questionId, request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .message()
            .asString()
            .contains(QUESTION_TEXT_SIZE_INVALID.message)
    }

    @DisplayName("배경 색상이 6자 미만일 경우 질문 업데이트를 하면 예외가 발생한다")
    @Test
    fun givenLesserThan6SizeQuestionBackgroundColor_whenUpdateQuestion_thenThrow() {
        // given
        val questionId = 1L
        val request = createValidQuestionUpdateServiceRequest(listOf("카테고리"))
        request.questionBackgroundColor = "0F0F0"

        // when & then
        assertThatThrownBy { questionService.updateQuestion(questionId, request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .message()
            .asString()
            .contains(QUESTION_BACKGROUND_COLOR_SIZE_INVALID.message)
    }

    @DisplayName("배경 색상이 6자 초과일 경우 질문 업데이트를 하면 예외가 발생한다")
    @Test
    fun givenGreaterThan6SizeQuestionBackgroundColor_whenUpdateQuestion_thenThrow() {
        // given
        val questionId = 1L
        val request = createValidQuestionUpdateServiceRequest(listOf("카테고리"))
        request.questionBackgroundColor = "0F0F0FF"

        // when & then
        assertThatThrownBy { questionService.updateQuestion(questionId, request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .message()
            .asString()
            .contains(QUESTION_BACKGROUND_COLOR_SIZE_INVALID.message)
    }

    @DisplayName("카테고리가 1개 미만일 경우 질문 업데이트를 하면 예외가 발생한다")
    @Test
    fun givenLesserThan1SizeCategoryNames_whenUpdateQuestion_thenThrow() {
        // given
        val questionId = 1L
        val request = createValidQuestionUpdateServiceRequest(emptyList())

        // when & then
        assertThatThrownBy { questionService.updateQuestion(questionId, request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .message()
            .asString()
            .contains(CATEGORY_NAMES_SIZE_INVALID.message)
    }

    @DisplayName("카테고리가 3개 초과일 경우 질문 업데이트를 하면 예외가 발생한다")
    @Test
    fun givenGreaterThan3SizeCategoryNames_whenUpdateQuestion_thenThrow() {
        // given
        val questionId = 1L
        val request = createValidQuestionUpdateServiceRequest(listOf("카테고리1", "카테고리2", "카테고리3", "카테고리4"))

        // when & then
        assertThatThrownBy { questionService.updateQuestion(questionId, request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .message()
            .asString()
            .contains(CATEGORY_NAMES_SIZE_INVALID.message)
    }

    @DisplayName("잘못된 포맷인 질문 상태로 질문 업데이트를 하면 예외가 발생한다")
    @Test
    fun givenPatternQuestionStatus_whenUpdateQuestion_thenThrow() {
        // given
        val questionId = 1L
        val request = createValidQuestionUpdateServiceRequest(listOf("카테고리"))
        request.questionStatus = "WRONG"

        // when & then
        assertThatThrownBy { questionService.updateQuestion(questionId, request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .message()
            .asString()
            .contains(QUESTION_STATUS_PATTERN_INVALID.message)
    }

    @DisplayName("존재하지 않은 질문 ID로 질문 업데이트를 하면 예외가 발생한다")
    @Test
    fun givenNonExistingQuestionId_whenUpdateQuestion_thenThrow() {
        // given
        val questionId = Long.MAX_VALUE
        val request = createValidQuestionUpdateServiceRequest(listOf("카테고리"))

        // when & then
        assertThatThrownBy { questionService.updateQuestion(questionId, request) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(QUESTION_NOT_FOUND)
    }

    @DisplayName("존재하지 않은 카테고리 ID로 질문 업데이트를 하면 예외가 발생한다")
    @Test
    fun givenNonExistingCategoryId_whenUpdateQuestion_thenThrow() {
        // given
        val now = LocalDateTime.now()
        val adminRole = createAndSaveUserRole(Role.ADMIN)
        val questionCategory = createAndSaveQuestionCategory(
            adminRole.user,
            "질문",
            "000000",
            now.withHour(0).withMinute(0).withSecond(0),
            now.withHour(23).withMinute(59).withSecond(59),
            "카테고리"
        )

        val request = createValidQuestionUpdateServiceRequest(listOf("카테고리 없음"))

        // when & then
        assertThatThrownBy { questionService.updateQuestion(questionCategory.question.id!!, request) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(CATEGORY_NOT_FOUND)
    }

    private fun createValidQuestionUpdateServiceRequest(categoryNames: List<String>): QuestionUpdateServiceRequest {
        return QuestionUpdateServiceRequest(
            "질문 업데이트",
            "XXXXXX",
            categoryNames,
            null,
            null,
            ACTIVE.toString()
        )
    }

    private fun createValidCategoryCreateServiceRequest(categoryName: String): CategoryCreateServiceRequest {
        return CategoryCreateServiceRequest(categoryName)
    }

    private fun createValidCategoryUpdateServiceRequest(categoryName: String = "카테고리 수정",
                                                        categoryStatus: String = ACTIVE.toString())
    : CategoryUpdateServiceRequest {
        return CategoryUpdateServiceRequest(categoryName, categoryStatus)
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
