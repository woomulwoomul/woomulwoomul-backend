package com.woomulwoomul.clientserver.controller.question.request

import com.woomulwoomul.clientserver.service.question.request.AnswerUpdateServiceRequest
import com.woomulwoomul.core.common.constant.ExceptionCode
import com.woomulwoomul.core.common.response.CustomException
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