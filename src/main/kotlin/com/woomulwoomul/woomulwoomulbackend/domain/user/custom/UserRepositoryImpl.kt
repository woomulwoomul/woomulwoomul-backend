package com.woomulwoomul.woomulwoomulbackend.domain.user.custom

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import com.woomulwoomul.woomulwoomulbackend.domain.base.ServiceStatus.ACTIVE
import com.woomulwoomul.woomulwoomulbackend.domain.user.QUserEntity.userEntity
import com.woomulwoomul.woomulwoomulbackend.domain.user.UserEntity
import io.micrometer.common.util.StringUtils
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : UserCustomRepository {

    override fun exists(username: String?): Boolean {
        return queryFactory
            .select(userEntity.id)
            .from(userEntity)
            .where(eqUsername(username))
            .fetchFirst() != null
    }

    override fun find(id: Long?): UserEntity? {
        return queryFactory
            .selectFrom(userEntity)
            .where(
                userEntity.status.eq(ACTIVE),
                eqId(id)
            ).fetchFirst()
    }

    private fun eqUsername(username: String?): BooleanExpression? {
        return if (StringUtils.isNotBlank(username)) userEntity.username.eq(username)
        else null
    }

    private fun eqId(userId: Long?): BooleanExpression? {
        return if (userId != null) userEntity.id.eq(userId)
        else null
    }
}