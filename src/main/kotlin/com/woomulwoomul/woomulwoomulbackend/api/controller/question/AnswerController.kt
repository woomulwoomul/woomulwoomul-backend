package com.woomulwoomul.woomulwoomulbackend.api.controller.question

import com.woomulwoomul.woomulwoomulbackend.api.service.question.AnswerService
import com.woomulwoomul.woomulwoomulbackend.api.service.question.response.AnswerFindAllResponse
import com.woomulwoomul.woomulwoomulbackend.common.constant.SuccessCode
import com.woomulwoomul.woomulwoomulbackend.common.constant.SuccessCode.FOUND_USER_ANSWER
import com.woomulwoomul.woomulwoomulbackend.common.request.PageRequest
import com.woomulwoomul.woomulwoomulbackend.common.response.DefaultPageResponse
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
        val response = answerService.getAllAnswers(userId, PageRequest.of(pageFrom, pageSize))

        return DefaultPageResponse.toResponseEntity(FOUND_USER_ANSWER, response)
    }
}