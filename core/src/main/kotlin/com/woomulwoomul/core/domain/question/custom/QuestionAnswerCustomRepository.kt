package com.woomulwoomul.core.domain.question.custom

import com.woomulwoomul.core.common.request.PageCursorRequest
import com.woomulwoomul.core.common.request.PageOffsetRequest
import com.woomulwoomul.core.common.response.PageData
import com.woomulwoomul.core.common.vo.AnsweredUserCntVo
import com.woomulwoomul.core.domain.question.QuestionAnswerEntity

interface QuestionAnswerCustomRepository {

    fun findAllByQuestionId(questionId: Long, pageOffsetRequest: PageOffsetRequest): PageData<QuestionAnswerEntity>

    fun findAllAnswered(userId: Long, pageCursorRequest: PageCursorRequest): PageData<QuestionAnswerEntity>

    fun findByAnswerId(answerId: Long): QuestionAnswerEntity?

    fun findAnsweredByUserIdAndAnswerId(userId: Long, answerId: Long): QuestionAnswerEntity?

    fun findAnsweredByUserIdAndQuestionId(userId: Long, questionId: Long): QuestionAnswerEntity?

    fun findRandomAnsweredUserImageUrls(questionId: Long, limit: Long): List<String>

    fun countAnsweredUser(questionId: Long): Long

    fun countAnsweredUsers(questionIds: List<Long>): List<AnsweredUserCntVo>

    fun exists(receiverUserId: Long, questionId: Long): Boolean
}