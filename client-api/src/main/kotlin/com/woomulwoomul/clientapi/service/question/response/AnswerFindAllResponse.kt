package com.woomulwoomul.clientapi.service.question.response

import com.fasterxml.jackson.annotation.JsonFormat
import com.woomulwoomul.core.domain.question.CategoryEntity
import com.woomulwoomul.core.domain.question.QuestionAnswerEntity
import java.time.LocalDateTime

data class AnswerFindAllResponse(
    val answerId: Long,
    val answerText: String,
    val answerImageUrl: String,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    val answerUpdateDateTime: LocalDateTime,
    val answeredUserCnt: Long,
    val answeredUserImageUrls: List<String>,

    val questionId: Long,
    val questionText: String,
    val questionBackgroundColor: String,
    val categories: List<AnswerFindAllCategoryResponse>
) {
    constructor(
        questionAnswer: QuestionAnswerEntity,
        answeredUserCnt: Long,
        answeredUserImageUrls: List<String>,
        categories: List<CategoryEntity>,
    ): this(
        questionAnswer.answer!!.id!!,
        questionAnswer.answer!!.text,
        questionAnswer.answer!!.imageUrl,
        questionAnswer.answer!!.updateDateTime!!,
        answeredUserCnt,
        answeredUserImageUrls,
        questionAnswer.question.id!!,
        questionAnswer.question.text,
        questionAnswer.question.backgroundColor,
        categories.map { AnswerFindAllCategoryResponse(it) }
    )
}

data class AnswerFindAllCategoryResponse(
    val categoryId: Long,
    val name: String,
) {
    constructor(category: CategoryEntity): this(category.id!!, category.name)
}
