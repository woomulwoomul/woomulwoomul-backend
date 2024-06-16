package com.woomulwoomul.woomulwoomulbackend.api.controller.question

import com.woomulwoomul.woomulwoomulbackend.api.service.question.QuestionService
import com.woomulwoomul.woomulwoomulbackend.api.service.question.response.QuestionFindAllCategoryResponse
import com.woomulwoomul.woomulwoomulbackend.api.service.question.response.QuestionFindResponse
import com.woomulwoomul.woomulwoomulbackend.common.constant.SuccessCode.ALL_CATEGORIES_FOUND
import com.woomulwoomul.woomulwoomulbackend.common.constant.SuccessCode.DEFAULT_QUESTIONS_FOUND
import com.woomulwoomul.woomulwoomulbackend.common.response.DefaultListResponse
import com.woomulwoomul.woomulwoomulbackend.common.response.DefaultPageResponse
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

        return DefaultListResponse.toResponseEntity(DEFAULT_QUESTIONS_FOUND, responses)
    }

    @GetMapping("/api/categories")
    fun getAllCategories(
        @RequestParam(name = "page-from", defaultValue = "0") pageFrom: Long,
        @RequestParam(name = "page-size", defaultValue = "20") pageSize: Long,
    ): ResponseEntity<DefaultPageResponse<QuestionFindAllCategoryResponse>> {
        val response = questionService.getAllCategories(pageFrom, pageSize)

        return DefaultPageResponse.toResponseEntity(ALL_CATEGORIES_FOUND, response)
    }
}