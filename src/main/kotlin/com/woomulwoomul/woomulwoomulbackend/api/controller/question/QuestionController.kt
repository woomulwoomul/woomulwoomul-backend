package com.woomulwoomul.woomulwoomulbackend.api.controller.question

import com.woomulwoomul.woomulwoomulbackend.api.controller.question.request.QuestionUserCreateRequest
import com.woomulwoomul.woomulwoomulbackend.api.service.question.QuestionService
import com.woomulwoomul.woomulwoomulbackend.api.service.question.response.QuestionFindAllCategoryResponse
import com.woomulwoomul.woomulwoomulbackend.api.service.question.response.QuestionFindResponse
import com.woomulwoomul.woomulwoomulbackend.common.constant.SuccessCode.*
import com.woomulwoomul.woomulwoomulbackend.common.request.PageRequest
import com.woomulwoomul.woomulwoomulbackend.common.response.DefaultListResponse
import com.woomulwoomul.woomulwoomulbackend.common.response.DefaultPageResponse
import com.woomulwoomul.woomulwoomulbackend.common.response.DefaultResponse
import com.woomulwoomul.woomulwoomulbackend.common.response.DefaultSingleResponse
import com.woomulwoomul.woomulwoomulbackend.common.utils.UserUtils
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.security.Principal

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
        @RequestParam(name = "page-from", required = false) pageFrom: Long?,
        @RequestParam(name = "page-size", required = false) pageSize: Long?,
    ): ResponseEntity<DefaultPageResponse<QuestionFindAllCategoryResponse>> {
        val response = questionService.getAllCategories(PageRequest.of(pageFrom, pageSize))

        return DefaultPageResponse.toResponseEntity(ALL_CATEGORIES_FOUND, response)
    }

    @PostMapping("/api/questions")
    fun createUserQuestion(principal: Principal, @RequestBody @Valid request: QuestionUserCreateRequest)
    : ResponseEntity<DefaultSingleResponse> {
        val response = questionService.createUserQuestion(UserUtils.getUserId(principal), request.toServiceRequest())

        return DefaultSingleResponse.toResponseEntity(USER_QUESTION_CREATED, response)
    }
}