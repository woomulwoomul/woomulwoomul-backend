package com.woomulwoomul.adminapi.service.question

import com.woomulwoomul.adminapi.service.question.request.CategoryUpdateServiceRequest
import com.woomulwoomul.adminapi.service.question.response.CategoryFindResponse
import com.woomulwoomul.adminapi.service.question.response.QuestionFindAllCategoryResponse
import com.woomulwoomul.adminapi.service.question.response.QuestionFindAllResponse
import com.woomulwoomul.core.common.constant.ExceptionCode.CATEGORY_NOT_FOUND
import com.woomulwoomul.core.common.request.PageOffsetRequest
import com.woomulwoomul.core.common.response.CustomException
import com.woomulwoomul.core.common.response.PageData
import com.woomulwoomul.core.domain.question.CategoryRepository
import com.woomulwoomul.core.domain.question.QuestionCategoryRepository
import com.woomulwoomul.core.domain.question.QuestionRepository
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
) {

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
        val category = categoryRepository.find(categoryId) ?: throw CustomException(CATEGORY_NOT_FOUND)

        return CategoryFindResponse(category)
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
        categoryRepository.find(categoryId)?.apply {
            update(request.categoryName)
        } ?: throw CustomException(CATEGORY_NOT_FOUND)
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
}