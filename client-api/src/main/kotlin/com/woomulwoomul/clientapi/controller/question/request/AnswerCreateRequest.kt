package com.woomulwoomul.clientapi.controller.question.request

import com.woomulwoomul.clientapi.service.question.request.AnswerCreateServiceRequest
import com.woomulwoomul.core.common.constant.ExceptionCode.ANSWER_FIELD_REQUIRED
import com.woomulwoomul.core.common.response.CustomException
import io.micrometer.common.util.StringUtils

data class AnswerCreateRequest(
    var answerText: String?,
    var answerImageUrl: String?
) {
    fun toServiceRequest(): AnswerCreateServiceRequest {
        if (StringUtils.isBlank(answerText) && StringUtils.isBlank(answerImageUrl))
            throw CustomException(ANSWER_FIELD_REQUIRED)

        return AnswerCreateServiceRequest(answerText ?: "", answerImageUrl ?: "")
    }
}
