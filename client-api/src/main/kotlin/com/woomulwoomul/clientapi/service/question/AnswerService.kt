package com.woomulwoomul.clientapi.service.question

import com.woomulwoomul.clientapi.service.question.request.AnswerCreateServiceRequest
import com.woomulwoomul.clientapi.service.question.request.AnswerUpdateServiceRequest
import com.woomulwoomul.clientapi.service.question.response.AnswerCreateResponse
import com.woomulwoomul.clientapi.service.question.response.AnswerFindAllResponse
import com.woomulwoomul.clientapi.service.question.response.AnswerFindResponse
import com.woomulwoomul.clientapi.service.s3.S3Service
import com.woomulwoomul.core.common.constant.ExceptionCode.*
import com.woomulwoomul.core.common.constant.NotificationConstants.ANSWER
import com.woomulwoomul.core.common.constant.NotificationConstants.FOLLOW
import com.woomulwoomul.core.common.request.PageRequest
import com.woomulwoomul.core.common.response.CustomException
import com.woomulwoomul.core.common.response.PageData
import com.woomulwoomul.core.domain.notification.NotificationRepository
import com.woomulwoomul.core.domain.notification.NotificationType
import com.woomulwoomul.core.domain.question.AnswerRepository
import com.woomulwoomul.core.domain.question.QuestionAnswerRepository
import com.woomulwoomul.core.domain.question.QuestionCategoryRepository
import com.woomulwoomul.core.domain.follow.FollowRepository
import com.woomulwoomul.core.domain.user.UserRepository
import com.woomulwoomul.core.domain.user.UserVisitEntity
import com.woomulwoomul.core.domain.user.UserVisitRepository
import jakarta.validation.Valid
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
@Validated
@Transactional(readOnly = true)
class AnswerService(
    private val s3Service: S3Service,
    private val questionAnswerRepository: QuestionAnswerRepository,
    private val questionCategoryRepository: QuestionCategoryRepository,
    private val userVisitRepository: UserVisitRepository,
    private val userRepository: UserRepository,
    private val answerRepository: AnswerRepository,
    private val followRepository: FollowRepository,
    private val notificationRepository: NotificationRepository,
) {

    private val ANSWERED_USER_CONST = 3L

    /**
     * 답변 전체 조회
     * @param visitorUserId 방문자 회원 ID
     * @param userId 회원 ID
     * @param pageRequest 페이징 요청
     * @throws USER_NOT_FOUND 404
     * @return 답변 전체 응답
     */
    @Transactional
    fun getAllAnswers(visitorUserId: Long, userId: Long, pageRequest: PageRequest): PageData<AnswerFindAllResponse> {
        val user = userRepository.findByUserId(userId) ?: throw CustomException(USER_NOT_FOUND)
                if (visitorUserId != userId) {
                    val visitorUser = userRepository.findByUserId(visitorUserId) ?: throw CustomException(USER_NOT_FOUND)
            userVisitRepository.save(UserVisitEntity(user = user, visitorUser = visitorUser))
        }

        val questionAnswers = questionAnswerRepository.findAllAnswered(userId, pageRequest)

        val questionCategoryMap = questionCategoryRepository.findByQuestionIds(
            questionAnswers.data.mapNotNull { it.question.id }
        ).groupBy { it.question.id!! }
            .mapValues { it.value.map { questionCategory -> questionCategory.category } }
        val answeredUserCntMap = questionAnswerRepository.countAnsweredUsers(questionCategoryMap.keys.toList())
            .associate { it.questionId to it.userCnt }

        val answeredUserImageUrlMap = questionCategoryMap.keys
            .associateWith { questionAnswerRepository.findRandomAnsweredUserImageUrls(it, ANSWERED_USER_CONST) }

        val responses = questionAnswers.data.map {
            AnswerFindAllResponse(it,
                answeredUserCntMap[it.question.id] ?: 0,
                answeredUserImageUrlMap[it.question.id] ?: emptyList(),
                questionCategoryMap[it.question.id] ?: emptyList())
        }

        return PageData(responses, questionAnswers.total)
    }

    /**
     * 답변 조회
     * @param userId 회원 ID
     * @param answerId 답변 ID
     * @throws ANSWER_NOT_FOUND 404
     * @return 답변 응답
     */
    fun getAnswerByUserIdAndAnswerId(userId: Long, answerId: Long): AnswerFindResponse {
        val questionAnswer = questionAnswerRepository.findAnsweredByUserIdAndAnswerId(userId, answerId)
            ?: throw CustomException(ANSWER_NOT_FOUND)
        val questionCategories = questionCategoryRepository.findByQuestionIds(listOf(questionAnswer.question.id!!))

        val randomAnsweredImageUrls = questionAnswerRepository.findRandomAnsweredUserImageUrls(
            questionAnswer.question.id!!,
            ANSWERED_USER_CONST
        )
        val answeredUserCnt = questionAnswerRepository.countAnsweredUser(questionAnswer.question.id!!)

        return AnswerFindResponse(
            questionAnswer,
            answeredUserCnt,
            randomAnsweredImageUrls,
            questionCategories.map { it.category }
        )
    }

    /**
     * 답변 조회
     * @param userId 회원 ID
     * @param questionId 질문 ID
     * @throws ANSWER_NOT_FOUND 404
     * @return 답변 응답
     */
    fun getAnswerByUserIdAndQuestionId(userId: Long, questionId: Long): AnswerFindResponse {
        val questionAnswer = questionAnswerRepository.findAnsweredByUserIdAndQuestionId(userId, questionId)
            ?: throw CustomException(ANSWER_NOT_FOUND)
        val questionCategories = questionCategoryRepository.findByQuestionIds(listOf(questionAnswer.question.id!!))

        val randomAnsweredImageUrls = questionAnswerRepository.findRandomAnsweredUserImageUrls(
            questionAnswer.question.id!!,
            ANSWERED_USER_CONST
        )
        val answeredUserCnt = questionAnswerRepository.countAnsweredUser(questionAnswer.question.id!!)

        return AnswerFindResponse(
            questionAnswer,
            answeredUserCnt,
            randomAnsweredImageUrls,
            questionCategories.map { it.category }
        )
    }

    /**
     * 답변 작성
     * @param receiverUserId 수신자 회원 ID
     * @param senderUserId 발신자 회원 ID
     * @param questionId 질문 ID
     * @param request 답변 작성 요청
     * @throws ANSWER_TEXT_SIZE_INVALID 400
     * @throws ANSWER_IMAGE_URL_SIZE_INVALID 400
     * @throws USER_NOT_FOUND 404
     * @throws QUESTION_NOT_FOUND 404
     * @throws EXISTING_ANSWER 409
     * @return 답변 작성 응답
     */
    @Transactional
    fun createAnswer(receiverUserId: Long,
                     senderUserId: Long,
                     questionId: Long,
                     @Valid request: AnswerCreateServiceRequest
                     ): AnswerCreateResponse {
        val receiver = userRepository.findByUserId(receiverUserId) ?: throw CustomException(USER_NOT_FOUND)
        val sender = userRepository.findByUserId(senderUserId) ?: throw CustomException(USER_NOT_FOUND)

        if (questionAnswerRepository.exists(receiverUserId, questionId)) throw CustomException(EXISTING_ANSWER)

        val questionCategories = questionCategoryRepository.findByQuestionId(questionId)
            .takeIf { it.isNotEmpty() } ?: throw CustomException(QUESTION_NOT_FOUND)
        val question = questionCategories.first().question
        val categories = questionCategories.mapTo(mutableSetOf()) { it.category }

        val answer = answerRepository.save(request.toAnswerEntity())
        questionAnswerRepository.save(request.toQuestionAnswerEntity(receiver, sender, question, answer))

        if (receiverUserId == senderUserId) return AnswerCreateResponse(receiver, question, categories)

        if (!followRepository.exists(receiverUserId, senderUserId)) {
            followRepository.save(request.toFollowEntity(sender, receiver))
            followRepository.save(request.toFollowEntity(receiver, sender))

            notificationRepository.save(
                request.toNotificationEntity(
                    receiver,
                    sender,
                    NotificationType.FOLLOW,
                    FOLLOW.toMessage(sender.nickname),
                    null,
                    FOLLOW.toLink(listOf(senderUserId))
                )
            )
            notificationRepository.save(
                request.toNotificationEntity(
                    sender,
                    receiver,
                    NotificationType.FOLLOW,
                    FOLLOW.toMessage(receiver.nickname),
                    null,
                    FOLLOW.toLink(listOf(receiverUserId))
                )
            )
        }

        notificationRepository.save(
            request.toNotificationEntity(
                sender,
                receiver,
                NotificationType.ANSWER,
                ANSWER.toMessage(receiver.nickname),
                question.text,
                ANSWER.toLink(listOf(receiverUserId, questionId))
            )
        )

        return AnswerCreateResponse(receiver, question, categories)
    }

    /**
     * 답변 업데이트
     * @param userId 회원 ID
     * @param answerId 답변 ID
     * @param request 답변 업데이트 요청
     * @throws ANSWER_TEXT_SIZE_INVALID 400
     * @throws ANSWER_IMAGE_URL_SIZE_INVALID 400
     * @throws ANSWER_NOT_FOUND 404
     * @return 답변 업데이트 응답
     */
    @Transactional
    fun updateAnswer(userId: Long, answerId: Long, @Valid request: AnswerUpdateServiceRequest): com.woomulwoomul.clientapi.service.question.response.AnswerUpdateResponse {
        val questionAnswer = questionAnswerRepository.findAnsweredByUserIdAndAnswerId(userId, answerId)
            ?: throw CustomException(ANSWER_NOT_FOUND)
        questionAnswer.answer!!.update(request.answerText, request.answerImageUrl)

        val questionCategories = questionCategoryRepository.findByQuestionIds(listOf(questionAnswer.question.id!!))

        val randomAnsweredImageUrls = questionAnswerRepository.findRandomAnsweredUserImageUrls(
            questionAnswer.question.id!!,
            ANSWERED_USER_CONST
        )
        val answeredUserCnt = questionAnswerRepository.countAnsweredUser(questionAnswer.question.id!!)

        return com.woomulwoomul.clientapi.service.question.response.AnswerUpdateResponse(
            questionAnswer,
            answeredUserCnt,
            randomAnsweredImageUrls,
            questionCategories.map { it.category }
        )
    }

    /**
     * 답변 삭제
     * @param userId 회원 ID
     * @param answerId 답변 ID
     * @throws ANSWER_NOT_FOUND 404
     */
    @Transactional
    fun deleteAnswer(userId: Long, answerId: Long) {
        questionAnswerRepository.findAnsweredByUserIdAndAnswerId(userId, answerId)?.apply {
            deleteByUser()
        } ?: throw CustomException(ANSWER_NOT_FOUND)
    }

    /**
     * 답변 이미지 업로드
     * @param userId 회원 ID
     * @param questionId 질문 ID
     * @param file 파일
     * @throws FILE_FIELD_REQUIRED 400
     * @throws IMAGE_TYPE_UNSUPPORTED 415
     * @throws SERVER_ERROR 500
     * @return 답변 이미지 업로드 응답
     *
     */
    fun uploadImage(userId: Long, questionId: Long, file: MultipartFile?): String {
        return s3Service.uploadFile(
            file,
            "questions/$questionId",
            userId.toString().plus(UUID.randomUUID().toString())
        )
    }

    /**
     * 답변 존재 여부 확인
     * @param userId 수신자 회원 ID
     * @param questionId 질문 회원 ID
     * @return 답변 존재 여부
     */
    fun isExistingAnswer(userId: Long, questionId: Long): Boolean {
        return questionAnswerRepository.exists(userId, questionId)
    }
}