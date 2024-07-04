package com.woomulwoomul.woomulwoomulbackend.api.controller.question.request

import com.woomulwoomul.woomulwoomulbackend.api.service.question.request.AnswerUpdateServiceRequest
import com.woomulwoomul.woomulwoomulbackend.common.constant.ExceptionCode
import com.woomulwoomul.woomulwoomulbackend.common.response.CustomException
import jodd.util.StringUtil

class AnswerUpdateRequest(
    var answerText: String?,
    var answerImageUrl: String?
) {

    fun toServiceRequest(): AnswerUpdateServiceRequest {
        if (StringUtil.isBlank(answerText) && StringUtil.isBlank(answerImageUrl))
            throw CustomException(ExceptionCode.ANSWER_FIELD_REQUIRED)

        return AnswerUpdateServiceRequest(answerText ?: "", answerImageUrl ?: "")
    }
}