package com.woomulwoomul.woomulwoomulbackend.api.service.question

import com.woomulwoomul.woomulwoomulbackend.api.service.question.response.QuestionFindResponse
import com.woomulwoomul.woomulwoomulbackend.domain.question.QuestionCategoryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.CollectionUtils
import org.springframework.validation.annotation.Validated

@Service
@Validated
@Transactional(readOnly = true)
class QuestionService(
    private val questionCategoryRepository: QuestionCategoryRepository
) {

    /**
     * 기본 질문들 조회
     * @param questionIds 질문 ID들
     * @return 기본 질문
     */
    fun getDefaultQuestions(questionIds: List<Long>): List<QuestionFindResponse> {
        val questionCategories = if (CollectionUtils.isEmpty(questionIds)) questionCategoryRepository.findRandom()
        else questionCategoryRepository.findByQuestionIds(questionIds)

        val questionCategoryMap = questionCategories.groupBy { it.question }
            .mapValues { (it, categories) -> categories.map { it.category }.toSet() }

        return questionCategoryMap.entries.map { (question, categories) ->
            QuestionFindResponse(question, categories)
        }
    }
}
