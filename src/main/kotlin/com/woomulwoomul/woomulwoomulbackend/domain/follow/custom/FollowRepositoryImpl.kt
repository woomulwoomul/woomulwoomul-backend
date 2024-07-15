package com.woomulwoomul.woomulwoomulbackend.domain.follow.custom

import com.querydsl.jpa.impl.JPAQueryFactory
import com.woomulwoomul.woomulwoomulbackend.common.request.PageRequest
import com.woomulwoomul.woomulwoomulbackend.common.response.PageData
import com.woomulwoomul.woomulwoomulbackend.domain.base.ServiceStatus.ACTIVE
import com.woomulwoomul.woomulwoomulbackend.domain.follow.FollowEntity
import com.woomulwoomul.woomulwoomulbackend.domain.follow.QFollowEntity.followEntity
import com.woomulwoomul.woomulwoomulbackend.domain.user.QUserEntity
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

    override fun findAllByFollower(userId: Long, pageRequest: PageRequest): PageData<FollowEntity> {
        val followerUserEntity = QUserEntity("followerUserEntity")
        val userEntity = QUserEntity("userEntity")

        val total = queryFactory
            .select(followEntity.id.count())
            .from(followEntity)
            .innerJoin(userEntity)
            .on(userEntity.id.eq(followEntity.user.id)
                .and(userEntity.status.eq(ACTIVE)))
            .fetchJoin()
            .innerJoin(followerUserEntity)
            .on(followerUserEntity.id.eq(followEntity.followerUser.id)
                .and(followerUserEntity.id.eq(userId))
                .and(followerUserEntity.status.eq(ACTIVE)))
            .where(
                followEntity.status.eq(ACTIVE)
            ).fetchFirst() ?: 0L

        if (total == 0L) return PageData(emptyList(), total)

        val data = queryFactory
            .selectFrom(followEntity)
            .innerJoin(userEntity)
            .on(userEntity.id.eq(followEntity.user.id)
                .and(userEntity.status.eq(ACTIVE)))
            .fetchJoin()
            .innerJoin(followerUserEntity)
            .on(followerUserEntity.id.eq(followEntity.followerUser.id)
                .and(followerUserEntity.id.eq(userId))
                .and(followerUserEntity.status.eq(ACTIVE)))
            .where(
                followEntity.id.loe(pageRequest.from),
                followEntity.status.eq(ACTIVE)
            ).orderBy(followEntity.id.desc())
            .limit(pageRequest.size)
            .fetch()

        return PageData(data, total)
    }
}