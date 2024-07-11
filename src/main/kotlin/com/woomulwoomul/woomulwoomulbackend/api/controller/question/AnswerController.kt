package com.woomulwoomul.woomulwoomulbackend.api.controller.question

import com.woomulwoomul.woomulwoomulbackend.api.controller.question.request.AnswerCreateRequest
import com.woomulwoomul.woomulwoomulbackend.api.controller.question.request.AnswerUpdateRequest
import com.woomulwoomul.woomulwoomulbackend.api.service.question.AnswerService
import com.woomulwoomul.woomulwoomulbackend.api.service.question.response.AnswerFindAllResponse
import com.woomulwoomul.woomulwoomulbackend.common.constant.SuccessCode.*
import com.woomulwoomul.woomulwoomulbackend.common.request.PageRequest
import com.woomulwoomul.woomulwoomulbackend.common.response.DefaultPageResponse
import com.woomulwoomul.woomulwoomulbackend.common.response.DefaultResponse
import com.woomulwoomul.woomulwoomulbackend.common.response.DefaultSingleResponse
import com.woomulwoomul.woomulwoomulbackend.common.utils.UserUtils
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.security.Principal

@Validated
@RestController
class AnswerController(
    private val answerService: AnswerService,
) {

    @GetMapping("/api/users/{user-id}/answers")
    fun getAllAnswers(@PathVariable(name = "user-id") userId: Long,
                      @RequestParam(name = "page-from", required = false) pageFrom: Long?,
                      @RequestParam(name = "page-size", required = false) pageSize: Long?,
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

    @PostMapping("/api/users/{user-id}/questions/{question-id}")
    fun createAnswer(principal: Principal,
                     @PathVariable(name = "user-id") userId: Long,
                     @PathVariable(name = "question-id") questionId: Long,
                     @RequestBody @Valid request: AnswerCreateRequest): ResponseEntity<DefaultSingleResponse> {
        val response = answerService.createAnswer(
            UserUtils.getUserId(principal),
            userId,
            questionId,
            request.toServiceRequest()
        )

        return DefaultSingleResponse.toResponseEntity(ANSWER_CREATED, response)
    }

    @PatchMapping("/api/answers/{answer-id}")
    fun updateAnswer(
        principal: Principal,
        @PathVariable(name = "answer-id") answerId: Long,
        @RequestBody @Valid request: AnswerUpdateRequest,
    ): ResponseEntity<DefaultSingleResponse> {
        val response = answerService.updateAnswer(UserUtils.getUserId(principal), answerId, request.toServiceRequest())

        return DefaultSingleResponse.toResponseEntity(ANSWER_UPDATED, response)
    }

    @PostMapping("/api/questions/{question-id}/answers/image", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun uploadImage(principal: Principal,
                    @PathVariable(name = "question-id") questionId: Long,
                    @RequestPart(required = false) file: MultipartFile?):
            ResponseEntity<DefaultSingleResponse> {
        val response = answerService.uploadImage(UserUtils.getUserId(principal), questionId, file)

        return DefaultSingleResponse.toResponseEntity(ANSWER_IMAGE_UPLOADED, response)
    }
}