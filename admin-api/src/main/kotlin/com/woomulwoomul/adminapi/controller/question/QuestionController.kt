package com.woomulwoomul.adminapi.controller.question

import com.woomulwoomul.adminapi.controller.question.request.CategoryCreateRequest
import com.woomulwoomul.adminapi.controller.question.request.CategoryUpdateRequest
import com.woomulwoomul.adminapi.controller.question.request.QuestionUpdateRequest
import com.woomulwoomul.adminapi.service.question.QuestionService
import com.woomulwoomul.core.common.request.PageOffsetRequest
import com.woomulwoomul.core.common.utils.ModelUtils
import com.woomulwoomul.core.common.utils.UserUtils
import jakarta.validation.Valid
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import java.security.Principal

@Validated
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

    @GetMapping("/categories/new")
    fun getCreateCategoryForm(): String {
        return "question/category-new"
    }

    @GetMapping("/categories/{categoryId}")
    fun getCategory(@PathVariable categoryId: Long, model: Model): String {
        val response = questionService.getCategory(categoryId)

        ModelUtils.addAttribute(response, model)

        return "question/category"
    }

    @PostMapping("/categories")
    fun createCategory(principal: Principal, @Valid request: CategoryCreateRequest): String {
        questionService.createCategory(UserUtils.getUserId(principal), request.toServiceRequest())

        return "redirect:/categories"
    }

    @PatchMapping("/categories/{categoryId}")
    fun updateCategory(@PathVariable categoryId: Long,
                       @Valid request: CategoryUpdateRequest): String {
        questionService.updateCategory(categoryId, request.toServiceRequest())

        return "redirect:/categories"
    }

    @DeleteMapping("/categories/{categoryId}")
    fun deleteCategory(@PathVariable categoryId: Long): String {
        questionService.deleteCategory(categoryId)

        return "redirect:/categories"
    }

    @GetMapping("/questions")
    fun getAllQuestions(@RequestParam(name = "page-from", required = false) pageFrom: Long?,
                        @RequestParam(name = "page-size", required = false) pageSize: Long?,
                        model: Model): String {
        val pageOffsetRequest = PageOffsetRequest.of(pageFrom, pageSize)

        val response = questionService.getAllQuestions(pageOffsetRequest)

        ModelUtils.addPageDataAttribute(pageOffsetRequest, response, model)

        return "question/questions"
    }

    @GetMapping("/questions/{questionId}")
    fun getQuestion(@PathVariable questionId: Long, model: Model): String {
        val response = questionService.getQuestion(questionId)

        ModelUtils.addAttribute(response, model)

        return "question/question"
    }

    @PatchMapping("/questions/{questionId}")
    fun updateQuestion(@PathVariable questionId: Long,
                       @Valid request: QuestionUpdateRequest
    ): String {
        questionService.updateQuestion(questionId, request.toServiceRequest())

        return "redirect:/questions"
    }

}