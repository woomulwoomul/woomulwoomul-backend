package com.woomulwoomul.core.domain.user.custom

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import com.woomulwoomul.core.domain.base.ServiceStatus.ACTIVE
import com.woomulwoomul.core.domain.user.QUserEntity.userEntity
import com.woomulwoomul.core.domain.user.UserEntity
import io.micrometer.common.util.StringUtils
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : UserCustomRepository {

    override fun existsByNickname(nickname: String): Boolean {
        return queryFactory
            .selectOne()
            .from(userEntity)
            .where(
                userEntity.status.eq(ACTIVE),
                userEntity.nickname.eq(nickname),
            )
            .fetchFirst() != null
    }

    override fun findByUserId(userId: Long): UserEntity? {
        return queryFactory
            .selectFrom(userEntity)
            .where(
                userEntity.id.eq(userId),
                userEntity.status.eq(ACTIVE),
            ).fetchFirst()
    }

    override fun findByNickname(nickname: String): UserEntity? {
        return queryFactory
            .selectFrom(userEntity)
            .where(
                userEntity.status.eq(ACTIVE),
                userEntity.nickname.eq(nickname)
            ).fetchFirst()
    }
}