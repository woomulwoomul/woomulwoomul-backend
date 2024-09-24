package com.woomulwoomul.adminapi.service.question

import com.woomulwoomul.adminapi.service.question.request.CategoryCreateServiceRequest
import com.woomulwoomul.adminapi.service.question.request.CategoryUpdateServiceRequest
import com.woomulwoomul.adminapi.service.question.request.QuestionCreateServiceRequest
import com.woomulwoomul.adminapi.service.question.request.QuestionUpdateServiceRequest
import com.woomulwoomul.core.common.constant.BackgroundColor
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
    fun `질문 생성 폼 조회가 정상 작동한다`() {
        // given
        val adminRole = createAndSaveUserRole(Role.ADMIN)
        val categories = listOf(
            createAndSaveCategory(adminRole.user, "카테고리1"),
            createAndSaveCategory(adminRole.user, "카테고리2"),
            createAndSaveCategory(adminRole.user, "카테고리3"),
            createAndSaveCategory(adminRole.user, "카테고리4"),
            createAndSaveCategory(adminRole.user, "카테고리5")
        )

        // when
        val result = questionService.getCreateQuestionForm()

        // then
        assertThat(result)
            .extracting("availableCategoryNames", "availableBackgroundColor")
            .containsExactly(categories.map(CategoryEntity::name), BackgroundColor.entries)
    }

    @Test
    fun `전체 카테고리 조회가 정상 작동한다`() {
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

    @Test
    fun `카테고리 조회가 정상 작동한다`() {
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

    @Test
    fun `존재하지 않는 카테고리 ID로 카테고리 조회를 하면 예외가 발생한다`() {
        // given
        val categoryId = Long.MAX_VALUE

        // when & then
        assertThatThrownBy { questionService.getCategory(categoryId) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(CATEGORY_NOT_FOUND)
    }

    @Test
    fun `카테고리 생성이 정상 작동한다`() {
        // given
        val adminRole = createAndSaveUserRole(Role.ADMIN)
        val request = createValidCategoryCreateServiceRequest("카테고리")

        // when
        val categoryId = questionService.createCategory(adminRole.user.id!!, request)

        // then
        assertThat(categoryRepository.existsById(categoryId)).isTrue()
    }

    @Test
    fun `1자 미만인 카테고리명으로 카테고리 생성을 하면 예외가 발생한다`() {
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

    @Test
    fun `10자 초과인 카테고리명으로 카테고리 생성을 하면 예외가 발생한다`() {
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

    @Test
    fun `존재하지 않은 관리자로 카테고리 생성을 하면 예외가 발생한다`() {
        // given
        val adminId = 1L
        val request = createValidCategoryCreateServiceRequest("카테고리")

        // when & then
        assertThatThrownBy { questionService.createCategory(adminId, request) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(ADMIN_NOT_FOUND)
    }

    @Test
    fun `존재하는 카테고리명으로 카테고리 생성을 하면 예외가 발생한다`() {
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

    @Test
    fun `카테고리 업데이트가 정상 작동한다`() {
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

    @Test
    fun `1자 미만인 카테고리명으로 카테고리 업데이트를 하면 예외가 발생한다`() {
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

    @Test
    fun `10자 초과인 카테고리명으로 카테고리 업데이트를 하면 예외가 발생한다`() {
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

    @Test
    fun `잘못된 상태 포맷으로 카테고리 업데이트를 하면 예외가 발생한다`() {
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

    @Test
    fun `존재하지 않는 카테고리 ID로 카테고리 업데이트를 하면 예외가 발생한다`() {
        // given
        val categoryId = Long.MAX_VALUE
        val request = createValidCategoryUpdateServiceRequest("카테고리")

        // when & then
        assertThatThrownBy { questionService.updateCategory(categoryId, request) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(CATEGORY_NOT_FOUND)
    }

    @Test
    fun `카테고리 삭제가 정상 작동한다`() {
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

    @Test
    fun `존재하지 않는 카테고리 ID로 카테고리 삭제를 하면 예외가 발생한다`() {
        // given
        val categoryId = Long.MAX_VALUE

        // when & then
        assertThatThrownBy { questionService.deleteCategory(categoryId) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(CATEGORY_NOT_FOUND)
    }

    @Test
    fun `전체 질문 조회가 정상 작동한다`() {
        // given
        val now = LocalDateTime.now()
        val adminRole = createAndSaveUserRole(Role.ADMIN)
        val questionCategories = listOf(
            createAndSaveQuestionCategory(
                adminRole.user,
                "질문1",
                BackgroundColor.entries[0],
                now.withHour(0).withMinute(0).withSecond(0),
                now.withHour(23).withMinute(59).withSecond(59),
                "카테고리1"
            ),
            createAndSaveQuestionCategory(
                adminRole.user,
                "질문2",
                BackgroundColor.entries[1],
                null,
                null,
                "카테고리2"
            ),
            createAndSaveQuestionCategory(
                adminRole.user,
                "질문3",
                BackgroundColor.entries[2],
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

    @Test
    fun `질문 조회가 정상 작동한다`() {
        // given
        val now = LocalDateTime.now()
        val adminRole = createAndSaveUserRole(Role.ADMIN)
        val questionCategory = createAndSaveQuestionCategory(
            adminRole.user,
            "질문",
            BackgroundColor.entries[0],
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
                "endDateTime", "status", "createDateTime", "updateDateTime", "availableCategoryNames",
                "availableBackgroundColors", "availableStatuses")
            .containsExactly(questionCategory.question.id, questionCategory.question.text,
                questionCategory.question.backgroundColor, questionCategory.question.user.nickname,
                listOf(questionCategory.category.name), questionCategory.question.startDateTime,
                questionCategory.question.endDateTime, questionCategory.question.status,
                questionCategory.question.createDateTime, questionCategory.question.updateDateTime,
                categories.map(CategoryEntity::name), BackgroundColor.entries, ServiceStatus.entries)
    }

    @Test
    fun `존재하지 않는 질문 ID로 질문 조회를 하면 예외가 발생한다`() {
        // given
        val questionId = Long.MAX_VALUE

        // when & then
        assertThatThrownBy { questionService.getQuestion(questionId) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(QUESTION_NOT_FOUND)
    }

    @Test
    fun `질문 생성이 정상 작동한다`() {
        // given
        val adminRole = createAndSaveUserRole(Role.ADMIN)
        val categories = listOf(
            createAndSaveCategory(adminRole.user, "카테고리1"),
            createAndSaveCategory(adminRole.user, "카테고리2")
        )

        val request = createValidQuestionCreateServiceRequest(categories.map(CategoryEntity::name))

        // when
        val questionId = questionService.createQuestion(adminRole.user.id!!, request)

        // then
        val questionCategories = questionCategoryRepository.findByQuestionId(questionId)
        val question = questionCategories.first().question

        assertAll(
            {
                assertThat(question)
                    .extracting("text", "backgroundColor", "startDateTime", "endDateTime")
                    .containsExactly(request.questionText, BackgroundColor.of(request.questionBackgroundColor),
                        request.questionStartDateTime, request.questionEndDateTime)
            }, {
                assertThat(questionCategories)
                    .extracting("category")
                    .extracting("name")
                    .containsExactlyInAnyOrder(categories[0].name, categories[1].name)
            }
        )
    }

    @Test
    fun `질문 내용 1자 미만으로 회원 질문 생성을 하면 예외가 발생한다`() {
        // given
        val userId = Long.MAX_VALUE

        val request = createValidQuestionCreateServiceRequest(listOf("카테고리"))
        request.questionText = ""

        // when & then
        assertThatThrownBy { questionService.createQuestion(userId, request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .message()
            .asString()
            .contains(QUESTION_TEXT_SIZE_INVALID.message)
    }

    @Test
    fun `질문 내용 60자 초과로 회원 질문 생성을 하면 예외가 발생한다`() {
        // given
        val userId = Long.MAX_VALUE

        val request = createValidQuestionCreateServiceRequest(listOf("카테고리"))
        request.questionText = "가".repeat(61)

        // when & then
        assertThatThrownBy { questionService.createQuestion(userId, request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .message()
            .asString()
            .contains(QUESTION_TEXT_SIZE_INVALID.message)
    }

    @Test
    fun `잘못된 포맷인 질문 배경 색상으로 회원 질문 생성을 하면 예외가 발생한다`() {
        // given
        val userId = Long.MAX_VALUE

        val request = createValidQuestionCreateServiceRequest(listOf("카테고리"))
        request.questionBackgroundColor = "XXXXXX"

        // when & then
        assertThatThrownBy { questionService.createQuestion(userId, request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .message()
            .asString()
            .contains(QUESTION_BACKGROUND_COLOR_PATTERN_INVALID.message)
    }

    @Test
    fun `1개 미만의 카테고리로 회원 질문 생성을 하면 예외가 발생한다`() {
        // given
        val userId = Long.MAX_VALUE

        val request = createValidQuestionCreateServiceRequest(listOf("카테고리"))
        request.categoryNames = emptyList()

        // when & then
        assertThatThrownBy { questionService.createQuestion(userId, request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .message()
            .asString()
            .contains(CATEGORY_NAMES_SIZE_INVALID.message)
    }

    @Test
    fun `3개 초과의 카테고리로 회원 질문 생성을 하면 예외가 발생한다`() {
        // given
        val userId = Long.MAX_VALUE

        val request = createValidQuestionCreateServiceRequest(listOf("카테고리"))
        request.categoryNames = listOf("카테고리1", "카테고리2", "카테고리3", "카테고리4")

        // when & then
        assertThatThrownBy { questionService.createQuestion(userId, request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .message()
            .asString()
            .contains(CATEGORY_NAMES_SIZE_INVALID.message)
    }

    @Test
    fun `존재하지 않은 회원 ID로 질문 생성을 하면 예외가 발생한다`() {
        // given
        val userId = Long.MAX_VALUE

        val request = createValidQuestionCreateServiceRequest(listOf("카테고리"))

        // when & then
        assertThatThrownBy { questionService.createQuestion(userId, request) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(USER_NOT_FOUND)
    }

    @Test
    fun `존재하지 않은 카테고리명으로 질문 생성을 하면 예외가 발생한다`() {
        // given
        val adminRole = createAndSaveUserRole(Role.ADMIN)

        val request = createValidQuestionCreateServiceRequest(listOf("카테고리 없음"))

        // when & then
        assertThatThrownBy { questionService.createQuestion(adminRole.user.id!!, request) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(CATEGORY_NOT_FOUND)
    }

    @Test
    fun `질문 업데이트가 정상 작동한다`() {
        // given
        val now = LocalDateTime.now()
        val adminRole = createAndSaveUserRole(Role.ADMIN)
        val questionCategory = createAndSaveQuestionCategory(
            adminRole.user,
            "질문",
            BackgroundColor.entries[0],
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
                    .containsExactly(request.questionText, BackgroundColor.of(request.questionBackgroundColor),
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

    @Test
    fun `내용이 1자 미만일 경우 질문 업데이트를 하면 예외가 발생한다`() {
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

    @Test
    fun `내용이 60자 초과일 경우 질문 업데이트를 하면 예외가 발생한다`() {
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

    @Test
    fun `잘못된 포맷인 배경 색상으로 질문 업데이트를 하면 예외가 발생한다`() {
        // given
        val questionId = 1L
        val request = createValidQuestionUpdateServiceRequest(listOf("카테고리"))
        request.questionBackgroundColor = "XXXXXX"

        // when & then
        assertThatThrownBy { questionService.updateQuestion(questionId, request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .message()
            .asString()
            .contains(QUESTION_BACKGROUND_COLOR_PATTERN_INVALID.message)
    }

    @Test
    fun `카테고리가 1개 미만일 경우 질문 업데이트를 하면 예외가 발생한다`() {
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

    @Test
    fun `카테고리가 3개 초과일 경우 질문 업데이트를 하면 예외가 발생한다`() {
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

    @Test
    fun `잘못된 포맷인 질문 상태로 질문 업데이트를 하면 예외가 발생한다`() {
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

    @Test
    fun `존재하지 않은 질문 ID로 질문 업데이트를 하면 예외가 발생한다`() {
        // given
        val questionId = Long.MAX_VALUE
        val request = createValidQuestionUpdateServiceRequest(listOf("카테고리"))

        // when & then
        assertThatThrownBy { questionService.updateQuestion(questionId, request) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(QUESTION_NOT_FOUND)
    }

    @Test
    fun `존재하지 않은 카테고리 ID로 질문 업데이트를 하면 예외가 발생한다`() {
        // given
        val now = LocalDateTime.now()
        val adminRole = createAndSaveUserRole(Role.ADMIN)
        val questionCategory = createAndSaveQuestionCategory(
            adminRole.user,
            "질문",
            BackgroundColor.entries[0],
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

    @Test
    fun `질문 삭제가 정상 동작한다`() {
        // given
        val now = LocalDateTime.now()
        val adminRole = createAndSaveUserRole(Role.ADMIN)
        val questionCategory = createAndSaveQuestionCategory(
            adminRole.user,
            "질문",
            BackgroundColor.entries[0],
            now.withHour(0).withMinute(0).withSecond(0),
            now.withHour(23).withMinute(59).withSecond(59),
            "카테고리"
        )

        // when
        questionService.deleteQuestion(questionCategory.question.id!!)

        // then
        assertThat(questionCategory.question)
            .extracting("status")
            .isEqualTo(ADMIN_DEL)
    }

    @Test
    fun `존재하지 않은 질문 ID로 질문 삭제를 하면 예외가 발생한다`() {
        // given
        val questionId = Long.MAX_VALUE

        // when & then
        assertThatThrownBy { questionService.deleteQuestion(questionId) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(QUESTION_NOT_FOUND)
    }

    private fun createValidQuestionCreateServiceRequest(categoryNames: List<String>)
            : QuestionCreateServiceRequest {
        return QuestionCreateServiceRequest(
            "질문",
            "FFACA8",
            categoryNames,
            null,
            null
        )
    }

    private fun createValidQuestionUpdateServiceRequest(categoryNames: List<String>): QuestionUpdateServiceRequest {
        return QuestionUpdateServiceRequest(
            "질문 업데이트",
            "FFACA8",
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
        backgroundColor: BackgroundColor = BackgroundColor.WHITE,
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
