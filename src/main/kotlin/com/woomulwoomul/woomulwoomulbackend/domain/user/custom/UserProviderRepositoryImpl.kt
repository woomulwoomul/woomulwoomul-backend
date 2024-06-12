package com.woomulwoomul.woomulwoomulbackend.domain.user.custom

import com.querydsl.jpa.impl.JPAQueryFactory
import com.woomulwoomul.woomulwoomulbackend.domain.base.ServiceStatus
import com.woomulwoomul.woomulwoomulbackend.domain.user.QUserEntity.userEntity
import com.woomulwoomul.woomulwoomulbackend.domain.user.QUserProviderEntity.userProviderEntity
import com.woomulwoomul.woomulwoomulbackend.domain.user.UserProviderEntity
import org.springframework.stereotype.Repository

@Repository
class UserProviderRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : UserProviderCustomRepository {

    override fun findFetchUser(providerId: String): UserProviderEntity? {
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