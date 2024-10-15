package com.woomulwoomul.adminapi.service.question.response

import com.woomulwoomul.core.domain.base.DetailServiceStatus
import com.woomulwoomul.core.domain.question.QuestionAnswerEntity
import java.time.LocalDateTime

data class QuestionAnswerFindAllResponse(
    val id: Long,
    val text: String,
    val imageUrl: String,
    val receiverId: Long,
    val receiverNickname: String,
    val senderId: Long?,
    val senderNickname: String?,
    val status: DetailServiceStatus,
    val createDateTime: LocalDateTime,
    val updateDateTime: LocalDateTime,

    val questionId: Long?
) {

    constructor(questionAnswer: QuestionAnswerEntity, questionId: Long): this(
        questionAnswer.answer?.id ?: 0,
        questionAnswer.answer!!.text,
        questionAnswer.answer!!.imageUrl,
        questionAnswer.receiver.id ?: 0,
        questionAnswer.receiver.nickname,
        questionAnswer.sender?.id,
        questionAnswer.sender?.nickname,
        questionAnswer.status,
        questionAnswer.createDateTime!!,
        questionAnswer.updateDateTime!!,
        questionId
    )
}
