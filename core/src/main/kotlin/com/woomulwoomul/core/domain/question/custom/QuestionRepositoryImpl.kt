package com.woomulwoomul.core.domain.question.custom

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQueryFactory
import com.woomulwoomul.core.common.request.PageOffsetRequest
import com.woomulwoomul.core.common.response.PageData
import com.woomulwoomul.core.common.utils.DatabaseUtils
import com.woomulwoomul.core.domain.base.ServiceStatus
import com.woomulwoomul.core.domain.base.ServiceStatus.ACTIVE
import com.woomulwoomul.core.domain.question.QQuestionEntity.questionEntity
import com.woomulwoomul.core.domain.question.QuestionEntity
import com.woomulwoomul.core.domain.user.QUserEntity.userEntity
import com.woomulwoomul.core.domain.user.QUserRoleEntity.userRoleEntity
import com.woomulwoomul.core.domain.user.Role.ADMIN
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class QuestionRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : QuestionCustomRepository {

    override fun findRandomAdminQuestionId(): Long? {
        return queryFactory
            .select(questionEntity.id)
            .from(questionEntity)
            .innerJoin(userEntity)
            .on(userEntity.id.eq(questionEntity.user.id)
                .and(userEntity.status.eq(ACTIVE)))
            .innerJoin(userRoleEntity)
            .on(userRoleEntity.user.id.eq(userEntity.id)
                .and(userRoleEntity.status.eq(ACTIVE))
                .and(userRoleEntity.role.eq(ADMIN)))
            .where(questionEntity.status.eq(ACTIVE))
            .orderBy(Expressions.numberTemplate(Double::class.java, "RAND()").asc())
            .fetchFirst()
    }

    override fun findAdminQuestionId(now: LocalDateTime): Long? {
        return queryFactory
            .select(questionEntity.id)
            .from(questionEntity)
            .innerJoin(userEntity)
            .on(userEntity.id.eq(questionEntity.user.id)
                .and(userEntity.status.eq(ACTIVE)))
            .innerJoin(userRoleEntity)
            .on(userRoleEntity.user.id.eq(userEntity.id)
                .and(userRoleEntity.status.eq(ACTIVE))
                .and(userRoleEntity.role.eq(ADMIN)))
            .where(
                questionEntity.status.eq(ACTIVE),
                questionEntity.startDateTime.loe(now),
                questionEntity.endDateTime.goe(now)
            ).fetchFirst()
    }

    override fun findAll(pageOffsetRequest: PageOffsetRequest): PageData<QuestionEntity> {
        val total = DatabaseUtils.count(queryFactory
            .select(questionEntity.id.count())
            .from(questionEntity)
            .innerJoin(userEntity)
            .on(userEntity.id.eq(questionEntity.user.id)
                .and(userEntity.status.eq(ACTIVE)))
            .fetchJoin()
            .fetchFirst())

        if (total == 0L) return PageData(emptyList(), total)

        val data = queryFactory
            .select(questionEntity)
            .from(questionEntity)
            .innerJoin(userEntity)
            .on(userEntity.id.eq(questionEntity.user.id)
                .and(userEntity.status.eq(ACTIVE)))
            .fetchJoin()
            .offset(pageOffsetRequest.from)
            .limit(pageOffsetRequest.size)
            .orderBy(questionEntity.id.desc())
            .fetch()

        return PageData(data, total)
    }

    override fun find(questionId: Long, statuses: List<ServiceStatus>): QuestionEntity? {
        return queryFactory
            .selectFrom(questionEntity)
            .where(
                inStatuses(statuses),
                questionEntity.id.eq(questionId)
            ).fetchFirst()
    }

    private fun inStatuses(statuses: List<ServiceStatus>): BooleanExpression? {
        return statuses.takeIf { it.isNotEmpty() }?.let { questionEntity.status.`in`(it) }
    }
}