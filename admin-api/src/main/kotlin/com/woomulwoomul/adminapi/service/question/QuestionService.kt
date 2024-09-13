package com.woomulwoomul.adminapi.service.question

import com.woomulwoomul.adminapi.service.question.response.QuestionFindAllCategoryResponse
import com.woomulwoomul.adminapi.service.question.response.QuestionFindAllResponse
import com.woomulwoomul.core.common.constant.ExceptionCode.SERVER_ERROR
import com.woomulwoomul.core.common.request.PageOffsetRequest
import com.woomulwoomul.core.common.response.CustomException
import com.woomulwoomul.core.common.response.PageData
import com.woomulwoomul.core.domain.question.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class QuestionService(
    private val categoryRepository: CategoryRepository,
    private val questionRepository: QuestionRepository,
    private val questionCategoryRepository: QuestionCategoryRepository,
) {

    /**
     * 카테고리 전체 조회
     * @param pageOffsetRequest 페이징 오프셋 요청
     * @return 카테고리 전체 응답
     */
    fun getAllCategories(pageOffsetRequest: PageOffsetRequest): PageData<QuestionFindAllCategoryResponse> {
        val categories = categoryRepository.findAll(pageOffsetRequest)
        return PageData(categories.data.map { QuestionFindAllCategoryResponse(it) }, categories.total)
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