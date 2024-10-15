package com.woomulwoomul.core.domain.question.custom

import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQueryFactory
import com.woomulwoomul.core.common.request.PageCursorRequest
import com.woomulwoomul.core.common.request.PageOffsetRequest
import com.woomulwoomul.core.common.response.PageData
import com.woomulwoomul.core.common.utils.DatabaseUtils
import com.woomulwoomul.core.common.vo.AnsweredUserCntVo
import com.woomulwoomul.core.domain.base.DetailServiceStatus.COMPLETE
import com.woomulwoomul.core.domain.base.ServiceStatus.ACTIVE
import com.woomulwoomul.core.domain.question.QAnswerEntity.answerEntity
import com.woomulwoomul.core.domain.question.QQuestionAnswerEntity.questionAnswerEntity
import com.woomulwoomul.core.domain.question.QQuestionEntity.questionEntity
import com.woomulwoomul.core.domain.question.QuestionAnswerEntity
import com.woomulwoomul.core.domain.user.QUserEntity.userEntity
import org.springframework.stereotype.Repository

@Repository
class QuestionAnswerRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : QuestionAnswerCustomRepository {

    override fun findAllByQuestionId(questionId: Long, pageOffsetRequest: PageOffsetRequest): PageData<QuestionAnswerEntity> {
        val total = DatabaseUtils.count(queryFactory
            .select(questionAnswerEntity.id.count())
            .from(questionAnswerEntity)
            .innerJoin(questionEntity)
            .on(questionEntity.id.eq(questionAnswerEntity.question.id)
                .and(questionEntity.status.eq(ACTIVE)))
            .innerJoin(answerEntity)
            .on(answerEntity.id.eq(questionAnswerEntity.answer.id))
            .innerJoin(userEntity)
            .on(userEntity.id.eq(questionAnswerEntity.receiver.id)
                .and(userEntity.status.eq(ACTIVE)))
            .where(
                questionAnswerEntity.question.id.eq(questionId)
            ).fetchFirst())

        if (total == 0L) return PageData(emptyList(), total)

        val data = queryFactory
            .select(questionAnswerEntity)
            .from(questionAnswerEntity)
            .innerJoin(questionEntity)
            .on(questionEntity.id.eq(questionAnswerEntity.question.id)
                .and(questionEntity.status.eq(ACTIVE)))
            .fetchJoin()
            .innerJoin(answerEntity)
            .on(answerEntity.id.eq(questionAnswerEntity.answer.id))
            .fetchJoin()
            .innerJoin(userEntity)
            .on(userEntity.id.eq(questionAnswerEntity.receiver.id)
                .and(userEntity.status.eq(ACTIVE)))
            .fetchJoin()
            .where(
                questionAnswerEntity.question.id.eq(questionId)
            ).offset(pageOffsetRequest.from)
            .limit(pageOffsetRequest.size)
            .orderBy(questionAnswerEntity.id.desc())
            .fetch()

        return PageData(data, total)
    }

    override fun findAllAnswered(userId: Long, pageCursorRequest: PageCursorRequest): PageData<QuestionAnswerEntity> {
        val total = DatabaseUtils.count(queryFactory
            .select(questionAnswerEntity.id.count())
            .from(questionAnswerEntity)
            .innerJoin(questionEntity)
            .on(questionEntity.id.eq(questionAnswerEntity.question.id)
                .and(questionEntity.status.eq(ACTIVE)))
            .where(
                questionAnswerEntity.status.eq(COMPLETE),
                questionAnswerEntity.receiver.id.eq(userId)
            ).fetchFirst())

        if (total == 0L) return PageData(emptyList(), total)

        val data = queryFactory
            .select(questionAnswerEntity)
            .from(questionAnswerEntity)
            .innerJoin(questionEntity)
            .on(questionEntity.id.eq(questionAnswerEntity.question.id)
                .and(questionEntity.status.eq(ACTIVE)))
            .fetchJoin()
            .innerJoin(answerEntity)
            .on(answerEntity.id.eq(questionAnswerEntity.answer.id)
                .and(answerEntity.status.eq(ACTIVE))
                .and(answerEntity.id.loe(pageCursorRequest.from)))
            .where(
                questionAnswerEntity.status.eq(COMPLETE),
                questionAnswerEntity.receiver.id.eq(userId)
            ).orderBy(answerEntity.id.desc())
            .limit(pageCursorRequest.size)
            .fetch()

        return PageData(data, total)
    }

    override fun findByAnswerId(answerId: Long): QuestionAnswerEntity? {
        return queryFactory
            .selectFrom(questionAnswerEntity)
            .innerJoin(answerEntity)
            .on(answerEntity.id.eq(questionAnswerEntity.answer.id)
                .and(answerEntity.id.eq(answerId)))
            .fetchJoin()
            .fetchFirst()
    }

    override fun findAnsweredByUserIdAndAnswerId(userId: Long, answerId: Long): QuestionAnswerEntity? {
        return queryFactory
            .selectFrom(questionAnswerEntity)
            .innerJoin(questionEntity)
            .on(questionEntity.id.eq(questionAnswerEntity.question.id)
                .and(questionEntity.status.eq(ACTIVE)))
            .fetchJoin()
            .innerJoin(answerEntity)
            .on(answerEntity.id.eq(questionAnswerEntity.answer.id)
                .and(answerEntity.id.eq(answerId))
                .and(answerEntity.status.eq(ACTIVE)))
            .fetchJoin()
            .where(
                questionAnswerEntity.status.eq(COMPLETE),
                questionAnswerEntity.receiver.id.eq(userId)
            ).fetchFirst()
    }

    override fun findAnsweredByUserIdAndQuestionId(userId: Long, questionId: Long): QuestionAnswerEntity? {
        return queryFactory
            .selectFrom(questionAnswerEntity)
            .innerJoin(questionEntity)
            .on(questionEntity.id.eq(questionAnswerEntity.question.id)
                .and(questionEntity.id.eq(questionId))
                .and(questionEntity.status.eq(ACTIVE)))
            .fetchJoin()
            .innerJoin(answerEntity)
            .on(answerEntity.id.eq(questionAnswerEntity.answer.id)
                .and(answerEntity.status.eq(ACTIVE)))
            .fetchJoin()
            .where(
                questionAnswerEntity.status.eq(COMPLETE),
                questionAnswerEntity.receiver.id.eq(userId)
            ).fetchFirst()
    }

    override fun findRandomAnsweredUserImageUrls(questionId: Long, limit: Long): List<String> {
        return queryFactory
            .select(questionAnswerEntity.receiver.imageUrl)
            .from(questionAnswerEntity)
            .innerJoin(questionEntity)
            .on(questionEntity.id.eq(questionAnswerEntity.question.id)
                .and(questionEntity.id.eq(questionId))
                .and(questionEntity.status.eq(ACTIVE)))
            .innerJoin(answerEntity)
            .on(answerEntity.id.eq(questionAnswerEntity.answer.id)
                .and(answerEntity.status.eq(ACTIVE)))
            .innerJoin(userEntity)
            .on(userEntity.id.eq(questionAnswerEntity.receiver.id)
                .and(userEntity.status.eq(ACTIVE)))
            .where(questionAnswerEntity.status.eq(COMPLETE))
            .orderBy(Expressions.numberTemplate(Double::class.java, "RAND()").asc())
            .limit(limit)
            .fetch()
    }

    override fun countAnsweredUser(questionId: Long): Long {
        return DatabaseUtils.count(queryFactory
            .select(questionAnswerEntity.id.count())
            .from(questionAnswerEntity)
            .innerJoin(questionEntity)
            .on(questionEntity.id.eq(questionAnswerEntity.question.id)
                .and(questionEntity.id.eq(questionId))
                .and(questionEntity.status.eq(ACTIVE)))
            .innerJoin(answerEntity)
            .on(answerEntity.id.eq(questionAnswerEntity.answer.id)
                .and(answerEntity.status.eq(ACTIVE)))
            .innerJoin(userEntity)
            .on(userEntity.id.eq(questionAnswerEntity.receiver.id)
                .and(userEntity.status.eq(ACTIVE)))
            .where(questionAnswerEntity.status.eq(COMPLETE))
            .fetchFirst())
    }

    override fun countAnsweredUsers(questionIds: List<Long>): List<AnsweredUserCntVo> {
        return queryFactory
            .select(Projections.constructor(AnsweredUserCntVo::class.java,
                questionEntity.id.`as`("questionId"),
                questionAnswerEntity.id.count().`as`("userCnt")
            )).from(questionAnswerEntity)
            .innerJoin(questionEntity)
            .on(questionEntity.id.eq(questionAnswerEntity.question.id)
                .and(questionEntity.id.`in`(questionIds))
                .and(questionEntity.status.eq(ACTIVE)))
            .innerJoin(answerEntity)
            .on(answerEntity.id.eq(questionAnswerEntity.answer.id)
                .and(answerEntity.status.eq(ACTIVE)))
            .innerJoin(userEntity)
            .on(userEntity.id.eq(questionAnswerEntity.receiver.id)
                .and(userEntity.status.eq(ACTIVE)))
            .where(questionAnswerEntity.status.eq(COMPLETE))
            .groupBy(questionEntity.id)
            .fetch()
    }

    override fun exists(receiverUserId: Long, questionId: Long): Boolean {
        return queryFactory
            .select(questionAnswerEntity.id)
            .from(questionAnswerEntity)
            .innerJoin(questionEntity)
            .on(questionEntity.id.eq(questionAnswerEntity.question.id)
                .and(questionEntity.id.eq(questionId))
                .and(questionEntity.status.eq(ACTIVE)))
            .innerJoin(userEntity)
            .on(userEntity.id.eq(questionAnswerEntity.receiver.id)
                .and(userEntity.id.eq(receiverUserId))
                .and(userEntity.status.eq(ACTIVE)))
            .fetchFirst() != null
    }
}