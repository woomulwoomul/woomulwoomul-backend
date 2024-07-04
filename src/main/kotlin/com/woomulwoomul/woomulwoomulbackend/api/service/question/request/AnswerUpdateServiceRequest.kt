package com.woomulwoomul.woomulwoomulbackend.api.service.question.request

import jakarta.validation.constraints.Size

class AnswerUpdateServiceRequest(
    @field:Size(max = 280, message = "답변 내용은 0~280자만 가능합니다.")
    var answerText: String,
    @field:Size(max = 500, message = "답변 이미지 URL은 0~500자만 가능합니다.")
    var answerImageUrl: String,
)
