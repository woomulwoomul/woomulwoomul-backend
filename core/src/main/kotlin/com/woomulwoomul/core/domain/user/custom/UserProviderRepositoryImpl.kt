package com.woomulwoomul.core.domain.user.custom

import com.querydsl.jpa.impl.JPAQueryFactory
import com.woomulwoomul.core.domain.base.ServiceStatus
import com.woomulwoomul.core.domain.user.QUserEntity.userEntity
import com.woomulwoomul.core.domain.user.QUserProviderEntity.userProviderEntity
import com.woomulwoomul.core.domain.user.UserProviderEntity
import org.springframework.stereotype.Repository

@Repository
class UserProviderRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : UserProviderCustomRepository {

    override fun findInnerFetchJoinUser(providerId: String): UserProviderEntity? {
        return queryFactory
            .select(userProviderEntity)
            .from(userProviderEntity)
            .innerJoin(userEntity)
            .on(userEntity.id.eq(userProviderEntity.user.id)
                .and(userEntity.status.eq(ServiceStatus.ACTIVE)))
            .fetchJoin()
            .where(userProviderEntity.providerId.eq(providerId))
            .fetchOne()
    }
}