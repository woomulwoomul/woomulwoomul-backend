package com.woomulwoomul.adminapi.controller.question

import com.woomulwoomul.adminapi.service.question.AnswerService
import com.woomulwoomul.core.common.request.PageOffsetRequest
import com.woomulwoomul.core.common.utils.ModelUtils
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

@Validated
@Controller
class AnswerController(
    private val answerService: AnswerService
) {

    @GetMapping("/questions/{questionId}/answers")
    fun getAllQuestionAnswers(@PathVariable questionId: Long,
                              @RequestParam(name = "page-from", required = false) pageFrom: Long?,
                              @RequestParam(name = "page-size", required = false) pageSize: Long?,
                              model: Model,
    ): String {
        val pageOffsetRequest = PageOffsetRequest.of(pageFrom, pageSize)

        val response = answerService.getAllQuestionAnswers(questionId, pageOffsetRequest)

        ModelUtils.addPageDataAttribute(pageOffsetRequest, response, model)
        model.addAttribute("questionId", questionId)

        return "question/question-answers"
    }

    @DeleteMapping("/questions/{questionId}/answers/{answerId}")
    fun deleteQuestionAnswer(@PathVariable questionId: Long, @PathVariable answerId: Long): String {
        answerService.delete(answerId)

        return "redirect:/questions/${questionId}/answers"
    }
}