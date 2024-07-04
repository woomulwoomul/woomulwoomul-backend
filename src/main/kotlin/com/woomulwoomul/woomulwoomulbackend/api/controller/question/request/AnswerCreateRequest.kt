package com.woomulwoomul.woomulwoomulbackend.api.controller.question.request

import com.woomulwoomul.woomulwoomulbackend.api.service.question.request.AnswerCreateServiceRequest
import com.woomulwoomul.woomulwoomulbackend.common.constant.ExceptionCode.ANSWER_FIELD_REQUIRED
import com.woomulwoomul.woomulwoomulbackend.common.response.CustomException
import jodd.util.StringUtil

data class AnswerCreateRequest(
    var answerText: String?,
    var answerImageUrl: String?
) {
    fun toServiceRequest(): AnswerCreateServiceRequest {
        if (StringUtil.isBlank(answerText) && StringUtil.isBlank(answerImageUrl))
            throw CustomException(ANSWER_FIELD_REQUIRED)

        return AnswerCreateServiceRequest(answerText ?: "", answerImageUrl ?: "")
    }
}
