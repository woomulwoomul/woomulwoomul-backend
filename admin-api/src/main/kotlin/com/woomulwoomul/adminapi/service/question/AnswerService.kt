package com.woomulwoomul.adminapi.service.question

import com.woomulwoomul.adminapi.service.question.response.QuestionAnswerFindAllResponse
import com.woomulwoomul.core.common.constant.ExceptionCode.ANSWER_NOT_FOUND
import com.woomulwoomul.core.common.request.PageOffsetRequest
import com.woomulwoomul.core.common.response.CustomException
import com.woomulwoomul.core.common.response.PageData
import com.woomulwoomul.core.domain.question.QuestionAnswerRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated

@Service
@Validated
@Transactional(readOnly = true)
class AnswerService(
    private val questionAnswerRepository: QuestionAnswerRepository
) {

    /**
     * 질문 답변 전체 조회
     * @param questionId 질문 ID
     * @param pageOffsetRequest 페이징 오프셋 요청
     * @return 질문 답변 전체 조회 응답
     */
    fun getAllQuestionAnswers(questionId: Long, pageOffsetRequest: PageOffsetRequest):
            PageData<QuestionAnswerFindAllResponse> {
        val questionAnswers = questionAnswerRepository.findAllByQuestionId(questionId, pageOffsetRequest)

        return PageData(questionAnswers.data.map { QuestionAnswerFindAllResponse(it, questionId) }, questionAnswers.total)
    }

    /**
     * 답변 삭제
     * @param answerId 답변 ID
     * @throws ANSWER_NOT_FOUND 404
     */
    @Transactional
    fun delete(answerId: Long) {
        val questionAnswer = questionAnswerRepository.findByAnswerId(answerId) ?: throw CustomException(ANSWER_NOT_FOUND)

        questionAnswer.deleteByAdmin()
    }
}
