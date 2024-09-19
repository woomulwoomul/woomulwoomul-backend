package com.woomulwoomul.adminapi.service.question

import com.woomulwoomul.adminapi.service.question.request.CategoryCreateServiceRequest
import com.woomulwoomul.adminapi.service.question.request.CategoryUpdateServiceRequest
import com.woomulwoomul.core.common.constant.ExceptionCode.*
import com.woomulwoomul.core.common.request.PageOffsetRequest
import com.woomulwoomul.core.common.response.CustomException
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
        val request = createValidCategoryUpdateServiceRequest("카테고리 수정")

        // when
        questionService.updateCategory(category.id!!, request)

        // then
        assertThat(categoryRepository.findById(category.id!!).orElseThrow { CustomException(CATEGORY_NOT_FOUND) })
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

    private fun createValidCategoryCreateServiceRequest(categoryName: String): CategoryCreateServiceRequest {
        return CategoryCreateServiceRequest(categoryName)
    }

    private fun createValidCategoryUpdateServiceRequest(categoryName: String): CategoryUpdateServiceRequest {
        return CategoryUpdateServiceRequest(categoryName)
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
