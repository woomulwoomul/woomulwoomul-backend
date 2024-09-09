package com.woomulwoomul.core.domain.follow.custom

import com.querydsl.jpa.impl.JPAQueryFactory
import com.woomulwoomul.core.common.request.PageCursorRequest
import com.woomulwoomul.core.common.response.PageData
import com.woomulwoomul.core.common.utils.DatabaseUtils
import com.woomulwoomul.core.domain.base.ServiceStatus.ACTIVE
import com.woomulwoomul.core.domain.follow.FollowEntity
import com.woomulwoomul.core.domain.follow.QFollowEntity.followEntity
import com.woomulwoomul.core.domain.user.QUserEntity
import com.woomulwoomul.core.domain.user.QUserEntity.userEntity
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

    override fun findAllByFollower(userId: Long, pageCursorRequest: PageCursorRequest): PageData<FollowEntity> {
        val followerUserEntity = QUserEntity("followerUserEntity")
        val userEntity = QUserEntity("userEntity")

        val total = DatabaseUtils.count(queryFactory
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
            ).fetchFirst())

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
                followEntity.id.loe(pageCursorRequest.from),
                followEntity.status.eq(ACTIVE)
            ).orderBy(followEntity.id.desc())
            .limit(pageCursorRequest.size)
            .fetch()

        return PageData(data, total)
    }

    override fun findAll(userId: Long, followerUserId: Long): List<FollowEntity> {
        val followerUserEntity = QUserEntity("followerUserEntity")
        val userEntity = QUserEntity("userEntity")

        return queryFactory
            .selectFrom(followEntity)
            .innerJoin(userEntity)
            .on(userEntity.id.eq(followEntity.user.id)
                .and(userEntity.id.eq(userId)
                    .or(userEntity.id.eq(followerUserId)))
                .and(userEntity.status.eq(ACTIVE)))
            .innerJoin(followerUserEntity)
            .on(followerUserEntity.id.eq(followEntity.followerUser.id)
                .and(followerUserEntity.id.eq(userId)
                    .or(followerUserEntity.id.eq(followerUserId)))
                .and(followerUserEntity.status.eq(ACTIVE)))
            .where(followEntity.status.eq(ACTIVE))
            .fetch()
    }
}