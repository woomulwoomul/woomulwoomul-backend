package com.woomulwoomul.core.domain.user.custom

import com.querydsl.jpa.impl.JPAQueryFactory
import com.woomulwoomul.core.domain.base.ServiceStatus.ACTIVE
import com.woomulwoomul.core.domain.user.QUserEntity.userEntity
import com.woomulwoomul.core.domain.user.QUserRoleEntity.userRoleEntity
import com.woomulwoomul.core.domain.user.UserRoleEntity
import org.springframework.stereotype.Repository

@Repository
class UserRoleRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : UserRoleCustomRepository {

    override fun findAllFetchUser(userId: Long): List<UserRoleEntity> {
        return queryFactory
            .selectFrom(userRoleEntity)
            .innerJoin(userEntity)
            .on(userEntity.id.eq(userRoleEntity.user.id)
                .and(userEntity.id.eq(userId))
                .and(userEntity.status.eq(ACTIVE)))
            .fetchJoin()
            .fetch()
    }
}