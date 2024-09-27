package com.woomulwoomul.clientapi.controller.question.request

import com.woomulwoomul.clientapi.service.question.request.AnswerUpdateServiceRequest
import com.woomulwoomul.core.common.constant.ExceptionCode
import com.woomulwoomul.core.common.response.CustomException
import io.micrometer.common.util.StringUtils

class AnswerUpdateRequest(
    var answerText: String?,
    var answerImageUrl: String?
) {

    fun toServiceRequest(): AnswerUpdateServiceRequest {
        if (StringUtils.isBlank(answerText) && StringUtils.isBlank(answerImageUrl))
            throw CustomException(ExceptionCode.ANSWER_FIELD_REQUIRED)

        return AnswerUpdateServiceRequest(answerText ?: "", answerImageUrl ?: "")
    }
}