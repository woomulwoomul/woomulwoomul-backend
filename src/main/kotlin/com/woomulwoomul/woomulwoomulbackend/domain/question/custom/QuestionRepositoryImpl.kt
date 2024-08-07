package com.woomulwoomul.woomulwoomulbackend.domain.question.custom

import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQueryFactory
import com.woomulwoomul.woomulwoomulbackend.domain.base.ServiceStatus.ACTIVE
import com.woomulwoomul.woomulwoomulbackend.domain.question.QQuestionEntity.questionEntity
import com.woomulwoomul.woomulwoomulbackend.domain.user.QUserEntity.userEntity
import com.woomulwoomul.woomulwoomulbackend.domain.user.QUserRoleEntity.userRoleEntity
import com.woomulwoomul.woomulwoomulbackend.domain.user.Role
import org.springframework.stereotype.Repository

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
                .and(userRoleEntity.role.eq(Role.ADMIN)))
            .where(questionEntity.status.eq(ACTIVE))
            .orderBy(Expressions.numberTemplate(Double::class.java, "RAND()").asc())
            .fetchFirst()
    }
}