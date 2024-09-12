package com.woomulwoomul.adminapi.service.question

import com.woomulwoomul.adminapi.service.question.response.QuestionFindAllCategoryResponse
import com.woomulwoomul.core.common.request.PageOffsetRequest
import com.woomulwoomul.core.common.response.PageData
import com.woomulwoomul.core.domain.question.CategoryRepository
import com.woomulwoomul.core.domain.question.QuestionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class QuestionService(
    private val categoryRepository: CategoryRepository,
    private val questionRepository: QuestionRepository,
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
}