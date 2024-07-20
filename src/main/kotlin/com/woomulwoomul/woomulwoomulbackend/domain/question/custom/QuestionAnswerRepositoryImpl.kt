package com.woomulwoomul.woomulwoomulbackend.domain.question.custom

import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQueryFactory
import com.woomulwoomul.woomulwoomulbackend.common.request.PageRequest
import com.woomulwoomul.woomulwoomulbackend.common.response.PageData
import com.woomulwoomul.woomulwoomulbackend.common.vo.AnsweredUserCntVo
import com.woomulwoomul.woomulwoomulbackend.domain.base.DetailServiceStatus.COMPLETE
import com.woomulwoomul.woomulwoomulbackend.domain.base.ServiceStatus.ACTIVE
import com.woomulwoomul.woomulwoomulbackend.domain.question.QAnswerEntity.answerEntity
import com.woomulwoomul.woomulwoomulbackend.domain.question.QQuestionAnswerEntity.questionAnswerEntity
import com.woomulwoomul.woomulwoomulbackend.domain.question.QQuestionEntity.questionEntity
import com.woomulwoomul.woomulwoomulbackend.domain.question.QuestionAnswerEntity
import com.woomulwoomul.woomulwoomulbackend.domain.user.QUserEntity.userEntity
import org.springframework.stereotype.Repository

@Repository
class QuestionAnswerRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : QuestionAnswerCustomRepository {

    override fun findAllAnswered(userId: Long, pageRequest: PageRequest): PageData<QuestionAnswerEntity> {
        val total = queryFactory
            .select(questionAnswerEntity.id.count())
            .from(questionAnswerEntity)
            .innerJoin(questionEntity)
            .on(questionEntity.id.eq(questionAnswerEntity.question.id)
                .and(questionEntity.status.eq(ACTIVE)))
            .fetchJoin()
            .where(
                questionAnswerEntity.status.eq(COMPLETE),
                questionAnswerEntity.receiver.id.eq(userId)
            ).fetchFirst() ?: 0L

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
                .and(answerEntity.id.loe(pageRequest.from)))
            .where(
                questionAnswerEntity.status.eq(COMPLETE),
                questionAnswerEntity.receiver.id.eq(userId)
            ).orderBy(answerEntity.id.desc())
            .limit(pageRequest.size)
            .fetch()

        return PageData(data, total)
    }

    override fun findAnswered(userId: Long, answerId: Long): QuestionAnswerEntity? {
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
        return queryFactory
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
            .fetchFirst() ?: 0L
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
}