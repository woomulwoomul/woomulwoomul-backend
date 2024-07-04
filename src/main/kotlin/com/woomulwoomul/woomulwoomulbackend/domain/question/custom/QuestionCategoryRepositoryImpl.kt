package com.woomulwoomul.woomulwoomulbackend.domain.question.custom

import com.amazonaws.util.CollectionUtils
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQueryFactory
import com.woomulwoomul.woomulwoomulbackend.domain.base.ServiceStatus.ACTIVE
import com.woomulwoomul.woomulwoomulbackend.domain.question.QCategoryEntity.categoryEntity
import com.woomulwoomul.woomulwoomulbackend.domain.question.QQuestionCategoryEntity.questionCategoryEntity
import com.woomulwoomul.woomulwoomulbackend.domain.question.QQuestionEntity.questionEntity
import com.woomulwoomul.woomulwoomulbackend.domain.question.QuestionCategoryEntity
import com.woomulwoomul.woomulwoomulbackend.domain.question.QuestionEntity
import com.woomulwoomul.woomulwoomulbackend.domain.user.QUserEntity.userEntity
import com.woomulwoomul.woomulwoomulbackend.domain.user.QUserRoleEntity.userRoleEntity
import com.woomulwoomul.woomulwoomulbackend.domain.user.Role.ADMIN
import org.springframework.stereotype.Repository

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
                .and(questionEntity.status.eq(ACTIVE)))
            .fetchJoin()
            .innerJoin(categoryEntity)
            .on(categoryEntity.id.eq(questionCategoryEntity.category.id)
                .and(categoryEntity.status.eq(ACTIVE)))
            .fetchJoin()
            .where(
                questionCategoryEntity.status.eq(ACTIVE),
                inQuestionId(questionIds)
            ).fetch()
    }

    override fun findByQuestionId(questionId: Long): List<QuestionCategoryEntity> {
        return queryFactory
            .select(questionCategoryEntity)
            .from(questionCategoryEntity)
            .innerJoin(questionEntity)
            .on(questionEntity.id.eq(questionCategoryEntity.question.id)
                .and(questionEntity.status.eq(ACTIVE)))
            .fetchJoin()
            .innerJoin(categoryEntity)
            .on(categoryEntity.id.eq(questionCategoryEntity.category.id)
                .and(categoryEntity.status.eq(ACTIVE)))
            .fetchJoin()
            .where(
                questionCategoryEntity.question.id.eq(questionId),
                questionCategoryEntity.status.eq(ACTIVE)
            ).fetch()
    }

    override fun findRandom(limit: Long): List<QuestionCategoryEntity> {
        return queryFactory
            .select(questionCategoryEntity)
            .from(questionCategoryEntity)
            .innerJoin(questionEntity)
            .on(questionEntity.id.eq(questionCategoryEntity.question.id)
                .and(questionEntity.status.eq(ACTIVE)))
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
            .where(
                questionCategoryEntity.status.eq(ACTIVE)
            ).orderBy(Expressions.numberTemplate(Double::class.java, "RAND()").asc())
            .limit(limit)
            .fetch()
    }

    private fun inQuestionId(questionIds: List<Long>): BooleanExpression? {
        if (CollectionUtils.isNullOrEmpty(questionIds)) return questionEntity.id.`in`(questionIds)
        return null
    }
}