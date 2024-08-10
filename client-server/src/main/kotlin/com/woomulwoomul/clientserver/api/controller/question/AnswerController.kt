package com.woomulwoomul.clientserver.api.controller.question

import com.woomulwoomul.clientserver.api.controller.question.request.AnswerCreateRequest
import com.woomulwoomul.clientserver.api.controller.question.request.AnswerUpdateRequest
import com.woomulwoomul.clientserver.api.service.question.AnswerService
import com.woomulwoomul.clientserver.api.service.question.response.AnswerFindAllResponse
import com.woomulwoomul.core.common.constant.SuccessCode.*
import com.woomulwoomul.core.common.request.PageRequest
import com.woomulwoomul.core.common.response.DefaultPageResponse
import com.woomulwoomul.core.common.response.DefaultResponse
import com.woomulwoomul.core.common.response.DefaultSingleResponse
import com.woomulwoomul.core.common.utils.UserUtils
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

    @GetMapping("/api/users/{userId}/answers")
    fun getAllAnswers(@PathVariable userId: Long,
                      @RequestParam(name = "page-from", required = false) pageFrom: Long?,
                      @RequestParam(name = "page-size", required = false) pageSize: Long?,
                      principal: Principal):
            ResponseEntity<DefaultPageResponse<AnswerFindAllResponse>> {
        val response = answerService.getAllAnswers(UserUtils.getUserId(principal), userId, PageRequest.of(pageFrom, pageSize))

        return DefaultPageResponse.toResponseEntity(FOUND_USER_ANSWERS, response)
    }

    @GetMapping("/api/users/{userId}/answers/{answerId}")
    fun getAnswer(@PathVariable userId: Long,
                  @PathVariable answerId: Long):
            ResponseEntity<DefaultSingleResponse> {
        val response = answerService.getAnswer(userId, answerId)

        return DefaultSingleResponse.toResponseEntity(FOUND_USER_ANSWER, response)
    }

    @PostMapping("/api/users/{userId}/questions/{questionId}")
    fun createAnswer(principal: Principal,
                     @PathVariable userId: Long,
                     @PathVariable questionId: Long,
                     @RequestBody @Valid request: AnswerCreateRequest): ResponseEntity<DefaultSingleResponse> {
        val response = answerService.createAnswer(
            UserUtils.getUserId(principal),
            userId,
            questionId,
            request.toServiceRequest()
        )

        return DefaultSingleResponse.toResponseEntity(ANSWER_CREATED, response)
    }

    @PatchMapping("/api/answers/{answerId}")
    fun updateAnswer(
        principal: Principal,
        @PathVariable answerId: Long,
        @RequestBody @Valid request: AnswerUpdateRequest,
    ): ResponseEntity<DefaultSingleResponse> {
        val response = answerService.updateAnswer(UserUtils.getUserId(principal), answerId, request.toServiceRequest())

        return DefaultSingleResponse.toResponseEntity(ANSWER_UPDATED, response)
    }

    @DeleteMapping("/api/answers/{answerId}")
    fun deleteAnswer(principal: Principal, @PathVariable answerId: Long): ResponseEntity<DefaultResponse> {
        answerService.deleteAnswer(UserUtils.getUserId(principal), answerId)

        return DefaultResponse.toResponseEntity(ANSWER_DELETED)
    }

    @PostMapping("/api/questions/{questionId}/answers/image", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun uploadImage(principal: Principal,
                    @PathVariable questionId: Long,
                    @RequestPart(required = false) file: MultipartFile?):
            ResponseEntity<DefaultSingleResponse> {
        val response = answerService.uploadImage(UserUtils.getUserId(principal), questionId, file)

        return DefaultSingleResponse.toResponseEntity(ANSWER_IMAGE_UPLOADED, response)
    }
}