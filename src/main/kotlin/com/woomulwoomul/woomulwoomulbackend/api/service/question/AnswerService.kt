package com.woomulwoomul.woomulwoomulbackend.api.service.question

import com.woomulwoomul.woomulwoomulbackend.api.service.question.response.AnswerFindAllResponse
import com.woomulwoomul.woomulwoomulbackend.common.request.PageRequest
import com.woomulwoomul.woomulwoomulbackend.common.response.PageData
import com.woomulwoomul.woomulwoomulbackend.domain.question.QuestionAnswerRepository
import com.woomulwoomul.woomulwoomulbackend.domain.question.QuestionCategoryRepository
import com.woomulwoomul.woomulwoomulbackend.domain.user.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated

@Service
@Validated
@Transactional(readOnly = true)
class AnswerService(
    private val questionAnswerRepository: QuestionAnswerRepository,
    private val questionCategoryRepository: QuestionCategoryRepository,
    private val userRepository: UserRepository,
) {

    /**
     * 답변 전체 조회
     * @param userId 회원 ID
     * @param pageRequest 페이징 요청
     * @return 답변 전체 응답
     */
    fun getAllAnswers(userId: Long, pageRequest: PageRequest): PageData<AnswerFindAllResponse> {
        val questionAnswers = questionAnswerRepository.findAllAnswered(userId, pageRequest)

        val questionCategoryMap = questionCategoryRepository.findByQuestionIds(
            questionAnswers.data.mapNotNull { it.question.id }
        ).groupBy { it.question.id!! }
            .mapValues { it.value.map { questionCategory -> questionCategory.category } }

        val responses = questionAnswers.data.map {
            AnswerFindAllResponse(it, questionCategoryMap[it.question.id] ?: emptyList())
        }

        return PageData(responses, questionAnswers.total)
    }
}