package com.woomulwoomul.woomulwoomulbackend.api.service.question

import com.woomulwoomul.woomulwoomulbackend.api.service.question.response.AnswerFindAllResponse
import com.woomulwoomul.woomulwoomulbackend.api.service.question.response.AnswerFindResponse
import com.woomulwoomul.woomulwoomulbackend.common.constant.ExceptionCode
import com.woomulwoomul.woomulwoomulbackend.common.constant.ExceptionCode.ANSWER_NOT_FOUND
import com.woomulwoomul.woomulwoomulbackend.common.constant.ExceptionCode.USER_NOT_FOUND
import com.woomulwoomul.woomulwoomulbackend.common.request.PageRequest
import com.woomulwoomul.woomulwoomulbackend.common.response.CustomException
import com.woomulwoomul.woomulwoomulbackend.common.response.PageData
import com.woomulwoomul.woomulwoomulbackend.domain.question.QuestionAnswerRepository
import com.woomulwoomul.woomulwoomulbackend.domain.question.QuestionCategoryRepository
import com.woomulwoomul.woomulwoomulbackend.domain.user.UserRepository
import com.woomulwoomul.woomulwoomulbackend.domain.user.UserVisitEntity
import com.woomulwoomul.woomulwoomulbackend.domain.user.UserVisitRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated

@Service
@Validated
@Transactional(readOnly = true)
class AnswerService(
    private val questionAnswerRepository: QuestionAnswerRepository,
    private val questionCategoryRepository: QuestionCategoryRepository,
    private val userVisitRepository: UserVisitRepository,
    private val userRepository: UserRepository,
) {

    private val ANSWERED_USER_CONST = 3L

    /**
     * 답변 전체 조회
     * @param visitorUserId 방문자 회원 ID
     * @param userId 회원 ID
     * @param pageRequest 페이징 요청
     * @throws USER_NOT_FOUND 404
     * @return 답변 전체 응답
     */
    fun getAllAnswers(visitorUserId: Long, userId: Long, pageRequest: PageRequest): PageData<AnswerFindAllResponse> {
        val user = userRepository.find(userId) ?: throw CustomException(USER_NOT_FOUND)
        if (visitorUserId != userId) {
            val visitorUser = userRepository.find(visitorUserId) ?: throw CustomException(USER_NOT_FOUND)
            userVisitRepository.save(UserVisitEntity(user = user, visitorUser = visitorUser))
        }

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

    /**
     * 답변 조회
     * @param userId 회원 ID
     * @param answerId 답변 ID
     * @throws ANSWER_NOT_FOUND 404
     * @return 답변 응답
     */
    fun getAnswer(userId: Long, answerId: Long): AnswerFindResponse {
        val questionAnswer = questionAnswerRepository.findAnswered(userId, answerId)
            ?: throw CustomException(ANSWER_NOT_FOUND)
        val questionCategories = questionCategoryRepository.findByQuestionIds(listOf(questionAnswer.question.id!!))

        val randomAnsweredImageUrls = questionAnswerRepository.findRandomAnsweredUserImageUrls(
            questionAnswer.question.id,
            ANSWERED_USER_CONST
        )
        val answeredUserCnt = questionAnswerRepository.countAnsweredUser(questionAnswer.question.id)

        return AnswerFindResponse(
            questionAnswer,
            answeredUserCnt,
            randomAnsweredImageUrls,
            questionCategories.map { it.category }
        )
    }
}