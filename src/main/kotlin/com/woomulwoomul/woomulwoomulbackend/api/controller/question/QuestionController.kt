package com.woomulwoomul.woomulwoomulbackend.api.controller.question

import com.woomulwoomul.woomulwoomulbackend.api.service.question.QuestionService
import com.woomulwoomul.woomulwoomulbackend.api.service.question.response.QuestionFindResponse
import com.woomulwoomul.woomulwoomulbackend.common.constant.SuccessCode
import com.woomulwoomul.woomulwoomulbackend.common.response.DefaultListResponse
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
class QuestionController(
    private val questionService: QuestionService,
) {

    @GetMapping("/api/questions")
    fun getDefaultQuestions(@RequestParam(required = false, name = "question-ids") questionIds: List<Long> = listOf()):
            ResponseEntity<DefaultListResponse<QuestionFindResponse>> {
        val responses = questionService.getDefaultQuestions(questionIds)

        return DefaultListResponse.toResponseEntity(SuccessCode.DEFAULT_QUESTIONS_FOUND, responses)
    }
}