package com.woomulwoomul.woomulwoomulbackend.domain.question.custom

import com.woomulwoomul.woomulwoomulbackend.common.request.PageRequest
import com.woomulwoomul.woomulwoomulbackend.common.response.PageData
import com.woomulwoomul.woomulwoomulbackend.domain.question.QuestionAnswerEntity

interface QuestionAnswerCustomRepository {

    fun findAllAnswered(userId: Long, pageRequest: PageRequest): PageData<QuestionAnswerEntity>
}