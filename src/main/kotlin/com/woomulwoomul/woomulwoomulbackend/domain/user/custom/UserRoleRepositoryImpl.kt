package com.woomulwoomul.woomulwoomulbackend.domain.user.custom

import com.querydsl.jpa.impl.JPAQueryFactory
import com.woomulwoomul.woomulwoomulbackend.domain.user.QUser.user
import com.woomulwoomul.woomulwoomulbackend.domain.user.QUserRole.userRole
import com.woomulwoomul.woomulwoomulbackend.domain.user.UserRole
import org.springframework.stereotype.Repository

@Repository
class UserRoleRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : UserRoleCustomRepository {

    override fun findAllFetchUser(userId: Long): List<UserRole> {
        return queryFactory
            .selectFrom(userRole)
            .innerJoin(user)
            .on(user.id.eq(userRole.user.id))
            .fetchJoin()
            .where(user.id.eq(userId))
            .fetch()
    }
}