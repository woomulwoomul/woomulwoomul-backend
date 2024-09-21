package com.woomulwoomul.adminapi.service.question

import com.woomulwoomul.adminapi.service.question.request.CategoryCreateServiceRequest
import com.woomulwoomul.adminapi.service.question.request.CategoryUpdateServiceRequest
import com.woomulwoomul.adminapi.service.question.request.QuestionCreateServiceRequest
import com.woomulwoomul.adminapi.service.question.request.QuestionUpdateServiceRequest
import com.woomulwoomul.adminapi.service.question.response.CategoryFindResponse
import com.woomulwoomul.adminapi.service.question.response.QuestionFindAllCategoryResponse
import com.woomulwoomul.adminapi.service.question.response.QuestionFindAllResponse
import com.woomulwoomul.adminapi.service.question.response.QuestionFindResponse
import com.woomulwoomul.core.common.constant.ExceptionCode.*
import com.woomulwoomul.core.common.request.PageOffsetRequest
import com.woomulwoomul.core.common.response.CustomException
import com.woomulwoomul.core.common.response.PageData
import com.woomulwoomul.core.domain.base.ServiceStatus
import com.woomulwoomul.core.domain.base.ServiceStatus.ACTIVE
import com.woomulwoomul.core.domain.base.ServiceStatus.ADMIN_DEL
import com.woomulwoomul.core.domain.question.*
import com.woomulwoomul.core.domain.user.UserRepository
import jakarta.validation.Valid
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated

@Service
@Validated
@Transactional(readOnly = true)
class QuestionService(
    private val categoryRepository: CategoryRepository,
    private val questionRepository: QuestionRepository,
    private val questionCategoryRepository: QuestionCategoryRepository,
    private val userRepository: UserRepository,
) {

    /**
     * 카테고리명 전체 조회
     * @return 카테고리명 전체 조회 응답
     */
    fun getAllCategoryNames(): List<String> {
        return categoryRepository.findAll().map(CategoryEntity::name)
    }

    /**
     * 카테고리 전체 조회
     * @param pageOffsetRequest 페이징 오프셋 요청
     * @return 카테고리 전체 조회 응답
     */
    fun getAllCategories(pageOffsetRequest: PageOffsetRequest): PageData<QuestionFindAllCategoryResponse> {
        val categories = categoryRepository.findAll(pageOffsetRequest)
        return PageData(categories.data.map { QuestionFindAllCategoryResponse(it) }, categories.total)
    }

    /**
     * 카테고리 조회
     * @param categoryId 카테고리 ID
     * @throws CATEGORY_NOT_FOUND 404
     * @return 카테고리 조회 응답
     */
    fun getCategory(categoryId: Long): CategoryFindResponse {
        val category = categoryRepository.find(categoryId, ServiceStatus.entries) ?: throw CustomException(CATEGORY_NOT_FOUND)

        return CategoryFindResponse(category)
    }

    /**
     * 카테고리 생성
     * @param userId 회원 ID
     * @param request 카테고리 생성 요청
     * @throws CATEGORY_NAME_SIZE_INVALID 400
     * @throws ADMIN_NOT_FOUND 404
     * @throws EXISTING_CATEGORY 409
     */
    @Transactional
    fun createCategory(userId: Long, @Valid request: CategoryCreateServiceRequest): Long {
        val user = userRepository.findByUserId(userId) ?: throw CustomException(ADMIN_NOT_FOUND)

        if (categoryRepository.exists(request.categoryName)) throw CustomException(EXISTING_CATEGORY)

        return categoryRepository.save(request.toCategoryEntity(user)).id ?: 0
    }


    /**
     * 카티고리 업데이트
     * @param categoryId 카테고리 ID
     * @param request 카테고리 업데이트 요청
     * @throws CATEGORY_NAME_SIZE_INVALID 400
     * @throws CATEGORY_NOT_FOUND 404
     */
    @Transactional
    fun updateCategory(categoryId: Long, @Valid request: CategoryUpdateServiceRequest) {
        categoryRepository.find(categoryId, ServiceStatus.entries)?.apply {
            updateName(request.categoryName)
            updateStatus(ServiceStatus.valueOf(request.categoryStatus))
        } ?: throw CustomException(CATEGORY_NOT_FOUND)
    }

    /**
     * 카테고리 삭제
     * @param categoryId 카테고리 ID
     * @throws CATEGORY_NOT_FOUND 404
     */
    @Transactional
    fun deleteCategory(categoryId: Long) {
        categoryRepository.find(categoryId, ServiceStatus.entries)?.updateStatus(ADMIN_DEL)
            ?: throw CustomException(CATEGORY_NOT_FOUND)
    }

    /**
     * 질문 전체 조회
     * @param pageOffsetRequest 페이징 오프셋 요청
     * @return 질문 전체 응답
     */
    fun getAllQuestions(pageOffsetRequest: PageOffsetRequest): PageData<QuestionFindAllResponse> {
        val questions = questionRepository.findAll(pageOffsetRequest)
        val questionCategoryMap = questionCategoryRepository.findByQuestionIds(questions.data.mapNotNull { it.id })
            .groupBy({ it.question.id ?: 0L},
                {it.category})

        return PageData(questions.data.map { QuestionFindAllResponse(it, questionCategoryMap[it.id ?: 0] ?: emptyList()) },
            questions.total)
    }

    /**
     * 질문 조회
     * @param questionId 질문 ID
     * @throws QUESTION_NOT_FOUND 404
     * @return 질문 조회 응답
     */
    fun getQuestion(questionId: Long): QuestionFindResponse {
        val questionCategories = questionCategoryRepository.findByQuestionId(questionId, ServiceStatus.entries)
            .takeIf { it.isNotEmpty() } ?: throw CustomException(QUESTION_NOT_FOUND)
        val question = questionCategories.first().question

        val availableCategories = categoryRepository.findAll()

        return QuestionFindResponse(question,
            question.user,
            questionCategories.map(QuestionCategoryEntity::category),
            availableCategories)
    }

    /**
     * 질문 생성
     * @param userId 회원 ID
     * @param request 질문 생성 요청
     * @throws USER_NOT_FOUND 404
     * @throws CATEGORY_NOT_FOUND 404
     */
    @Transactional
    fun createQuestion(userId: Long, @Valid request: QuestionCreateServiceRequest): Long {
        val user = userRepository.findByUserId(userId) ?: throw CustomException(USER_NOT_FOUND)

        val question = questionRepository.save(request.toQuestionEntity(user))

        request.categoryNames.forEach {
            val category = categoryRepository.find(it) ?: throw CustomException(CATEGORY_NOT_FOUND)
            questionCategoryRepository.save(request.toQuestionCategoryEntity(question, category))
        }

        return question.id ?: 0
    }

    /**
     * 질문 업데이트
     * @param questionId 질문 ID
     * @param request 질문 업데이트 요청
     * @throws QUESTION_NOT_FOUND 404
     * @throws CATEGORY_NOT_FOUND 404
     */
    @Transactional
    fun updateQuestion(questionId: Long, @Valid request: QuestionUpdateServiceRequest) {
        val questionCategories = questionCategoryRepository.findByQuestionId(questionId, ServiceStatus.entries)
            .takeIf { it.isNotEmpty() } ?: throw CustomException(QUESTION_NOT_FOUND)

        val question = questionCategories.first().question
        question.update(
            request.questionText,
            request.questionBackgroundColor,
            request.questionStartDateTime,
            request.questionEndDateTime,
            ServiceStatus.valueOf(request.questionStatus)
        )

        val questionCategoryMap = questionCategories.associateBy { it.category.name }

        // 질문 카테고리 생성 및 활성화
        request.categoryNames.forEach { categoryName ->
            val questionCategory = questionCategoryMap[categoryName]
            if (questionCategory != null) {
                questionCategory.updateStatus(ACTIVE)
            } else {
                val category = categoryRepository.find(categoryName)
                    ?: throw CustomException(CATEGORY_NOT_FOUND)
                questionCategoryRepository.save(request.toQuestionCategoryEntity(question, category))
            }
        }

        // 질문 카테고리 삭제
        questionCategoryMap.values.filter { it.category.name !in request.categoryNames }
            .forEach { it.updateStatus(ADMIN_DEL) }
    }

    /**
     * 질문 삭제
     * @param questionId 질문 ID
     * @throws QUESTION_NOT_FOUND 404
     */
    @Transactional
    fun deleteQuestion(questionId: Long) {
        questionRepository.find(questionId, ServiceStatus.entries)?.updateStatus(ADMIN_DEL)
            ?: throw CustomException(QUESTION_NOT_FOUND)
    }
}