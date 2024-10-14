package com.woomulwoomul.clientapi.service.question.request

import com.woomulwoomul.core.domain.notification.NotificationEntity
import com.woomulwoomul.core.domain.notification.NotificationType
import com.woomulwoomul.core.domain.question.AnswerEntity
import com.woomulwoomul.core.domain.question.QuestionAnswerEntity
import com.woomulwoomul.core.domain.question.QuestionEntity
import com.woomulwoomul.core.domain.follow.FollowEntity
import com.woomulwoomul.core.domain.user.UserEntity
import jakarta.validation.constraints.Size

data class AnswerCreateServiceRequest(
    @field:Size(max = 280, message = "답변 내용은 0자 ~ 280자 이내로 입력해 주세요.")
    var answerText: String,
    @field:Size(max = 500, message = "답변 이미지 URL은 0자 ~500자 이내로 입력해 주세요.")
    var answerImageUrl: String,
) {
    fun toAnswerEntity(): AnswerEntity {
        return AnswerEntity(text = answerText, imageUrl = answerImageUrl)
    }

    fun toQuestionAnswerEntity(
        receiver: UserEntity,
        sender: UserEntity?,
        question: QuestionEntity,
        answer: AnswerEntity,
    ): QuestionAnswerEntity {
        return QuestionAnswerEntity(receiver = receiver, sender = sender, question = question, answer = answer)
    }

    fun toFollowEntity(user: UserEntity, followerUser: UserEntity): FollowEntity {
        return FollowEntity(user = user, followerUser = followerUser)
    }

    fun toNotificationEntity(
        receiver: UserEntity,
        senderUser: UserEntity,
        type: NotificationType,
        title: String,
        context: String?,
        link: String,
    )
    : NotificationEntity {
        return NotificationEntity(
            receiver = receiver,
            senderUser = senderUser,
            type = type,
            title = title,
            context = context ?: "",
            link = link
        )
    }
}
