package com.woomulwoomul.clientapi.controller.question

import com.woomulwoomul.clientapi.controller.question.request.QuestionUserCreateRequest
import com.woomulwoomul.clientapi.service.question.QuestionService
import com.woomulwoomul.clientapi.service.question.response.QuestionFindAllCategoryResponse
import com.woomulwoomul.core.common.constant.SuccessCode.*
import com.woomulwoomul.core.common.request.PageRequest
import com.woomulwoomul.core.common.response.DefaultPageResponse
import com.woomulwoomul.core.common.response.DefaultSingleResponse
import com.woomulwoomul.core.common.utils.UserUtils
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
    fun getDefaultQuestion(@RequestParam(required = false, name = "question-id") questionId: Long?):
            ResponseEntity<DefaultSingleResponse> {
        val responses = questionService.getDefaultQuestion(questionId)

        return DefaultSingleResponse.toResponseEntity(DEFAULT_QUESTION_FOUND, responses)
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