package com.woomulwoomul.woomulwoomulbackend.api.service.question

import com.woomulwoomul.woomulwoomulbackend.api.service.question.request.QuestionUserCreateServiceRequest
import com.woomulwoomul.woomulwoomulbackend.api.service.question.response.QuestionFindAllCategoryResponse
import com.woomulwoomul.woomulwoomulbackend.api.service.question.response.QuestionFindResponse
import com.woomulwoomul.woomulwoomulbackend.api.service.question.response.QuestionUserCreateResponse
import com.woomulwoomul.woomulwoomulbackend.common.constant.ExceptionCode.CATEGORY_NOT_FOUND
import com.woomulwoomul.woomulwoomulbackend.common.constant.ExceptionCode.USER_NOT_FOUND
import com.woomulwoomul.woomulwoomulbackend.common.request.PageRequest
import com.woomulwoomul.woomulwoomulbackend.common.response.CustomException
import com.woomulwoomul.woomulwoomulbackend.common.response.PageData
import com.woomulwoomul.woomulwoomulbackend.domain.question.CategoryRepository
import com.woomulwoomul.woomulwoomulbackend.domain.question.QuestionCategoryRepository
import com.woomulwoomul.woomulwoomulbackend.domain.question.QuestionRepository
import com.woomulwoomul.woomulwoomulbackend.domain.user.UserRepository
import jakarta.validation.Valid
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.CollectionUtils
import org.springframework.validation.annotation.Validated

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
     * 기본 질문들 조회
     * @param questionIds 질문 ID들
     * @return 기본 질문 응답
     */
    fun getDefaultQuestions(questionIds: List<Long>): List<QuestionFindResponse> {
        val questionCategories = if (CollectionUtils.isEmpty(questionIds)) questionCategoryRepository.findRandom()
        else questionCategoryRepository.findByQuestionIds(questionIds)

        val questionCategoryMap = questionCategories.groupBy { it.question }
            .mapValues { (it, categories) -> categories.map { it.category }.toSet() }

        return questionCategoryMap.entries.map { (question, categories) ->
            QuestionFindResponse(question, categories)
        }
    }

    /**
     * 카테고리 전체 조회
     * @param pageRequest 페이징 요청
     * @return 카테고리 전체 응답
     */
    fun getAllCategories(pageRequest: PageRequest): PageData<QuestionFindAllCategoryResponse> {
        val categories = categoryRepository.findAll(pageRequest)
        return PageData(categories.data.map { QuestionFindAllCategoryResponse(it) }, categories.total)
    }

    /**
     * 회원 질문 생성
     * @param userId 회원 ID
     * @param request 회원 질문 생성 요청
     * @throws USER_NOT_FOUND 404
     * @throws CATEGORY_NOT_FOUND 404
     * @return 회원 질문 생성 응답
     */
    fun createUserQuestion(userId: Long, @Valid request: QuestionUserCreateServiceRequest): QuestionUserCreateResponse {
        val user = userRepository.find(userId) ?: throw CustomException(USER_NOT_FOUND)
        val categories = categoryRepository.findByIds(request.categoryIds)
            .takeIf { it.isNotEmpty() } ?: throw CustomException(CATEGORY_NOT_FOUND)

        val question = questionRepository.save(request.toQuestionEntity(user))
        questionCategoryRepository.saveAll(request.toQuestionCategories(question, categories))

        return QuestionUserCreateResponse(question, categories)
    }
}
