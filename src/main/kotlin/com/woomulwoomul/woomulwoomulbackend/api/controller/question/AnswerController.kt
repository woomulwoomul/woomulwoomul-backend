package com.woomulwoomul.woomulwoomulbackend.api.controller.question

import com.woomulwoomul.woomulwoomulbackend.api.service.question.AnswerService
import com.woomulwoomul.woomulwoomulbackend.api.service.question.response.AnswerFindAllResponse
import com.woomulwoomul.woomulwoomulbackend.common.constant.SuccessCode.FOUND_USER_ANSWER
import com.woomulwoomul.woomulwoomulbackend.common.constant.SuccessCode.FOUND_USER_ANSWERS
import com.woomulwoomul.woomulwoomulbackend.common.request.PageRequest
import com.woomulwoomul.woomulwoomulbackend.common.response.DefaultPageResponse
import com.woomulwoomul.woomulwoomulbackend.common.response.DefaultSingleResponse
import com.woomulwoomul.woomulwoomulbackend.common.utils.UserUtils
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@Validated
@RestController
class AnswerController(
    private val answerService: AnswerService,
) {

    @GetMapping("/api/users/{user-id}/answers")
    fun getAllAnswers(@PathVariable(name = "user-id") userId: Long,
                      @RequestParam(required = false, name = "page-from", defaultValue = "0") pageFrom: Long,
                      @RequestParam(required = false, name = "page-size", defaultValue = "10") pageSize: Long,
                      principal: Principal):
            ResponseEntity<DefaultPageResponse<AnswerFindAllResponse>> {
        val response = answerService.getAllAnswers(UserUtils.getUserId(principal), userId, PageRequest.of(pageFrom, pageSize))

        return DefaultPageResponse.toResponseEntity(FOUND_USER_ANSWERS, response)
    }

    @GetMapping("/api/users/{user-id}/answers/{answer-id}")
    fun getAnswer(@PathVariable(name = "user-id") userId: Long,
                  @PathVariable(name = "answer-id") answerId: Long):
            ResponseEntity<DefaultSingleResponse> {
        val response = answerService.getAnswer(userId, answerId)

        return DefaultSingleResponse.toResponseEntity(FOUND_USER_ANSWER, response)
    }
}