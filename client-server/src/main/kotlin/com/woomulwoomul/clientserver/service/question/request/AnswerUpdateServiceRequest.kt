package com.woomulwoomul.clientserver.service.question.request

import jakarta.validation.constraints.Size

class AnswerUpdateServiceRequest(
    @field:Size(max = 280, message = "답변 내용은 0자 ~ 280자 이내로 입력해 주세요.")
    var answerText: String,
    @field:Size(max = 500, message = "답변 이미지 URL은 0자 ~500자 이내로 입력해 주세요.")
    var answerImageUrl: String,
)
