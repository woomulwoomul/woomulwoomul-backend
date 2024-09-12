package com.woomulwoomul.adminapi.controller.question

import com.woomulwoomul.adminapi.service.question.QuestionService
import com.woomulwoomul.core.common.request.PageOffsetRequest
import com.woomulwoomul.core.common.utils.ModelUtils
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class QuestionController(
    private val questionService: QuestionService
) {

    @GetMapping("/categories")
    fun getAllCategories(@RequestParam(name = "page-from", required = false) pageFrom: Long?,
                         @RequestParam(name = "page-size", required = false) pageSize: Long?,
                         model: Model): String {
        val pageOffsetRequest = PageOffsetRequest.of(pageFrom, pageSize)

        val response = questionService.getAllCategories(pageOffsetRequest)

        ModelUtils.addPageDataAttribute(pageOffsetRequest, response, model)

        return "question/categories"
    }
}