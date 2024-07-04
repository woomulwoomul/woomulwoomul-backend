package com.woomulwoomul.woomulwoomulbackend.domain.user.custom

import com.querydsl.jpa.impl.JPAQueryFactory
import com.woomulwoomul.woomulwoomulbackend.domain.base.ServiceStatus.ACTIVE
import com.woomulwoomul.woomulwoomulbackend.domain.user.QFollowEntity.followEntity
import com.woomulwoomul.woomulwoomulbackend.domain.user.QUserEntity.userEntity
import org.springframework.stereotype.Repository

@Repository
class FollowRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : FollowCustomRepository {

    override fun exists(userId: Long, followerUserId: Long): Boolean {
        return queryFactory
            .select(followEntity.id)
            .from(followEntity)
            .innerJoin(userEntity)
            .on(userEntity.id.eq(followEntity.user.id)
                .and(userEntity.status.eq(ACTIVE)))
            .innerJoin(userEntity)
            .on(userEntity.id.eq(followEntity.followerUser.id)
                .and(userEntity.status.eq(ACTIVE)))
            .where(
                followEntity.status.eq(ACTIVE),
                followEntity.user.id.eq(userId),
                followEntity.followerUser.id.eq(followerUserId)
            ).fetchFirst() != null
    }
}