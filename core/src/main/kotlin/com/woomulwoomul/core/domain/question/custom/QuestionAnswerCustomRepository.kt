package com.woomulwoomul.core.domain.question.custom

import com.woomulwoomul.core.common.request.PageRequest
import com.woomulwoomul.core.common.response.PageData
import com.woomulwoomul.core.common.vo.AnsweredUserCntVo
import com.woomulwoomul.core.domain.question.QuestionAnswerEntity

interface QuestionAnswerCustomRepository {

    fun findAllAnswered(userId: Long, pageRequest: PageRequest): PageData<QuestionAnswerEntity>

    fun findAnsweredByUserIdAndAnswerId(userId: Long, answerId: Long): QuestionAnswerEntity?

    fun findAnsweredByUserIdAndQuestionId(userId: Long, questionId: Long): QuestionAnswerEntity?

    fun findRandomAnsweredUserImageUrls(questionId: Long, limit: Long): List<String>

    fun countAnsweredUser(questionId: Long): Long

    fun countAnsweredUsers(questionIds: List<Long>): List<AnsweredUserCntVo>
}