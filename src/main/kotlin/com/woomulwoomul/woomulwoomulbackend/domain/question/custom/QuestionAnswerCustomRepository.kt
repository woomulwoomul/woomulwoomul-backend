package com.woomulwoomul.woomulwoomulbackend.domain.question.custom

import com.woomulwoomul.woomulwoomulbackend.common.request.PageRequest
import com.woomulwoomul.woomulwoomulbackend.common.response.PageData
import com.woomulwoomul.woomulwoomulbackend.common.vo.AnsweredUserCntVo
import com.woomulwoomul.woomulwoomulbackend.domain.question.QuestionAnswerEntity

interface QuestionAnswerCustomRepository {

    fun findAllAnswered(userId: Long, pageRequest: PageRequest): PageData<QuestionAnswerEntity>

    fun findAnswered(userId: Long, answerId: Long): QuestionAnswerEntity?

    fun findRandomAnsweredUserImageUrls(questionId: Long, limit: Long): List<String>

    fun countAnsweredUser(questionId: Long): Long

    fun countAnsweredUsers(questionIds: List<Long>): List<AnsweredUserCntVo>
}