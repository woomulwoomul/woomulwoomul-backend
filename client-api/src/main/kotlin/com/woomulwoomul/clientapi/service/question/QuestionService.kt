package com.woomulwoomul.clientapi.service.question

import com.woomulwoomul.clientapi.service.question.request.QuestionUserCreateServiceRequest
import com.woomulwoomul.clientapi.service.question.response.QuestionFindAllCategoryResponse
import com.woomulwoomul.clientapi.service.question.response.QuestionFindResponse
import com.woomulwoomul.clientapi.service.question.response.QuestionUserCreateResponse
import com.woomulwoomul.core.common.constant.ExceptionCode.*
import com.woomulwoomul.core.common.request.PageCursorRequest
import com.woomulwoomul.core.common.response.CustomException
import com.woomulwoomul.core.common.response.PageData
import com.woomulwoomul.core.domain.question.CategoryRepository
import com.woomulwoomul.core.domain.question.QuestionCategoryRepository
import com.woomulwoomul.core.domain.question.QuestionRepository
import com.woomulwoomul.core.domain.user.UserRepository
import jakarta.validation.Valid
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated
import java.time.LocalDateTime

@Service
@Validated
@Transactional(readOnly = true)
class QuestionService(
    private val questionRepository: QuestionRepository,
    private val questionCategoryRepository: QuestionCategoryRepository,
    private val categoryRepository: CategoryRepository,
    private val userRepository: UserRepository,
) {

    /**
     * 기본 질문 조회
     * @param questionId 질문 ID
     * @throws QUESTION_NOT_FOUND 404
     * @return 기본 질문 응답
     */
    fun getDefaultQuestion(questionId: Long?, now: LocalDateTime): QuestionFindResponse {
        val questionCategories = questionId?.let {
            questionCategoryRepository.findByQuestionIds(listOf(it))
        } ?: run {
            val qId = questionRepository.findAdminQuestionId(now)
                ?: questionRepository.findRandomAdminQuestionId()
                ?: throw CustomException(QUESTION_NOT_FOUND)
            questionCategoryRepository.findByQuestionId(qId)
        }

        val (question, categories) = questionCategories
            .groupBy { it.question }
            .mapValues { (_, category) -> category.map { it.category }.toSet() }
            .entries.first()

        return QuestionFindResponse(question, categories)
    }

    /**
     * 카테고리 전체 조회
     * @param pageCursorRequest 페이징 커서 요청
     * @return 카테고리 전체 응답
     */
    fun getAllCategories(pageCursorRequest: PageCursorRequest): PageData<QuestionFindAllCategoryResponse> {
        val categories = categoryRepository.findAll(pageCursorRequest)
        return PageData(categories.data.map { QuestionFindAllCategoryResponse(it) }, categories.total)
    }

    /**
     * 회원 질문 생성
     * @param userId 회원 ID
     * @param request 회원 질문 생성 요청
     * @throws QUESTION_TEXT_SIZE_INVALID 400
     * @throws QUESTION_BACKGROUND_COLOR_PATTERN_INVALID 400
     * @throws CATEGORY_IDS_SIZE_INVALID 400
     * @throws USER_NOT_FOUND 404
     * @throws CATEGORY_NOT_FOUND 404
     * @return 회원 질문 생성 응답
     */
    @Transactional
    fun createUserQuestion(userId: Long, @Valid request: QuestionUserCreateServiceRequest): QuestionUserCreateResponse {
        val user = userRepository.findByUserId(userId) ?: throw CustomException(USER_NOT_FOUND)
        val categories = categoryRepository.findByIds(request.categoryIds)
            .takeIf { it.isNotEmpty() } ?: throw CustomException(CATEGORY_NOT_FOUND)

        val question = questionRepository.save(request.toQuestionEntity(user))
        questionCategoryRepository.saveAll(request.toQuestionCategories(question, categories))

        return QuestionUserCreateResponse(question, categories)
    }
}
