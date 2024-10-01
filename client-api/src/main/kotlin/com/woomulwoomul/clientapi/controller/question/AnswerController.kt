package com.woomulwoomul.clientapi.controller.question

import com.woomulwoomul.clientapi.controller.question.request.AnswerCreateRequest
import com.woomulwoomul.clientapi.controller.question.request.AnswerUpdateRequest
import com.woomulwoomul.clientapi.service.question.AnswerService
import com.woomulwoomul.clientapi.service.question.response.AnswerFindAllResponse
import com.woomulwoomul.core.common.constant.SuccessCode.*
import com.woomulwoomul.core.common.request.PageCursorRequest
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
        val response = answerService.getAllAnswers(UserUtils.getUserId(principal), userId, PageCursorRequest.of(pageFrom, pageSize))

        return DefaultPageResponse.toResponseEntity(FOUND_USER_ANSWERS, response)
    }

    @GetMapping("/api/users/{userId}/answers/{answerId}")
    fun getAnswerByUserIdAndAnswerId(@PathVariable userId: Long,
                  @PathVariable answerId: Long):
            ResponseEntity<DefaultSingleResponse> {
        val response = answerService.getAnswerByUserIdAndAnswerId(userId, answerId)

        return DefaultSingleResponse.toResponseEntity(FOUND_USER_ANSWER, response)
    }

    @GetMapping("/api/users/{userId}/questions/{questionId}/answers")
    fun getAnswerByUserIdAndQuestionId(principal: Principal,
                                       @PathVariable userId: Long,
                                       @PathVariable questionId: Long): ResponseEntity<DefaultSingleResponse> {
        val response = answerService.getAnswerByUserIdAndQuestionId(userId, questionId)

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
    fun updateAnswer(principal: Principal,
                     @PathVariable answerId: Long,
                     @RequestBody @Valid request: AnswerUpdateRequest): ResponseEntity<DefaultSingleResponse> {
        val response = answerService.updateAnswer(UserUtils.getUserId(principal), answerId, request.toServiceRequest())

        return DefaultSingleResponse.toResponseEntity(ANSWER_UPDATED, response)
    }

    @DeleteMapping("/api/answers/{answerId}")
    fun deleteAnswer(principal: Principal, @PathVariable answerId: Long): ResponseEntity<DefaultResponse> {
        answerService.deleteAnswer(UserUtils.getUserId(principal), answerId)

        return DefaultResponse.toResponseEntity(ANSWER_DELETED)
    }

    @PostMapping("/api/questions/{questionId}/answers/image",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE])
    fun uploadImage(principal: Principal,
                    @PathVariable questionId: Long,
                    @RequestPart(required = false) file: MultipartFile?): ResponseEntity<DefaultSingleResponse> {
        val response = answerService.uploadImage(UserUtils.getUserId(principal), questionId, file)

        return DefaultSingleResponse.toResponseEntity(ANSWER_IMAGE_UPLOADED, response)
    }

    @GetMapping("/api/questions/{questionId}/answers")
    fun isExistingAnswer(principal: Principal,
                         @PathVariable questionId: Long): ResponseEntity<DefaultResponse> {
        val response = answerService.isExistingAnswer(UserUtils.getUserId(principal), questionId)

        return DefaultResponse.toResponseEntity(if (response) ANSWER_EXISTS else ANSWER_NON_EXISTS)
    }
}