package com.woomulwoomul.core.domain.question.custom

import com.querydsl.jpa.impl.JPAQueryFactory
import com.woomulwoomul.core.domain.base.ServiceStatus.ACTIVE
import com.woomulwoomul.core.domain.question.QCategoryEntity.categoryEntity
import com.woomulwoomul.core.domain.question.QQuestionCategoryEntity.questionCategoryEntity
import com.woomulwoomul.core.domain.question.QQuestionEntity.questionEntity
import com.woomulwoomul.core.domain.question.QuestionCategoryEntity
import com.woomulwoomul.core.domain.user.QUserEntity.userEntity
import com.woomulwoomul.core.domain.user.QUserRoleEntity.userRoleEntity
import com.woomulwoomul.core.domain.user.Role.ADMIN
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class QuestionCategoryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : QuestionCategoryCustomRepository {

    override fun findByQuestionIds(questionIds: List<Long>): List<QuestionCategoryEntity> {
        return queryFactory
            .select(questionCategoryEntity)
            .from(questionCategoryEntity)
            .innerJoin(questionEntity)
            .on(questionEntity.id.eq(questionCategoryEntity.question.id)
                .and(questionEntity.status.eq(ACTIVE))
                .and(questionEntity.id.`in`(questionIds)))
            .fetchJoin()
            .innerJoin(categoryEntity)
            .on(categoryEntity.id.eq(questionCategoryEntity.category.id)
                .and(categoryEntity.status.eq(ACTIVE)))
            .fetchJoin()
            .innerJoin(userEntity)
            .on(userEntity.id.eq(questionEntity.user.id)
                .and(userEntity.status.eq(ACTIVE)))
            .where(
                questionCategoryEntity.status.eq(ACTIVE)
            ).orderBy(questionEntity.id.desc())
            .orderBy(categoryEntity.id.asc())
            .fetch()
    }

    override fun findByQuestionId(questionId: Long): List<QuestionCategoryEntity> {
        return queryFactory
            .select(questionCategoryEntity)
            .from(questionCategoryEntity)
            .innerJoin(questionEntity)
            .on(questionEntity.id.eq(questionCategoryEntity.question.id)
                .and(questionEntity.status.eq(ACTIVE))
                .and(questionEntity.id.eq(questionId)))
            .fetchJoin()
            .innerJoin(categoryEntity)
            .on(categoryEntity.id.eq(questionCategoryEntity.category.id)
                .and(categoryEntity.status.eq(ACTIVE)))
            .fetchJoin()
            .innerJoin(userEntity)
            .on(userEntity.id.eq(questionEntity.user.id)
                .and(userEntity.status.eq(ACTIVE)))
            .where(questionCategoryEntity.status.eq(ACTIVE))
            .orderBy(categoryEntity.id.asc())
            .fetch()
    }

    override fun findAdmin(now: LocalDateTime): List<QuestionCategoryEntity> {
        return queryFactory
            .select(questionCategoryEntity)
            .from(questionCategoryEntity)
            .innerJoin(questionEntity)
            .on(questionEntity.id.eq(questionCategoryEntity.question.id)
                .and(questionEntity.status.eq(ACTIVE))
                .and(questionEntity.startDateTime.loe(now))
                .and(questionEntity.endDateTime.goe(now)))
            .fetchJoin()
            .innerJoin(categoryEntity)
            .on(categoryEntity.id.eq(questionCategoryEntity.category.id)
                .and(categoryEntity.status.eq(ACTIVE)))
            .fetchJoin()
            .innerJoin(userEntity)
            .on(userEntity.id.eq(questionEntity.user.id)
                .and(userEntity.status.eq(ACTIVE)))
            .innerJoin(userRoleEntity)
            .on(userRoleEntity.user.id.eq(userEntity.id)
                .and(userRoleEntity.status.eq(ACTIVE))
                .and(userRoleEntity.role.eq(ADMIN)))
            .where(questionCategoryEntity.status.eq(ACTIVE))
            .orderBy(categoryEntity.id.asc())
            .fetch()
    }
}