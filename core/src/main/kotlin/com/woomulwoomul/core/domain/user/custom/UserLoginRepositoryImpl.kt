package com.woomulwoomul.core.domain.user.custom

import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import com.woomulwoomul.core.common.request.PageOffsetRequest
import com.woomulwoomul.core.common.response.PageData
import com.woomulwoomul.core.common.utils.DatabaseUtils
import com.woomulwoomul.core.domain.base.ServiceStatus.ACTIVE
import com.woomulwoomul.core.domain.user.QUserEntity.userEntity
import com.woomulwoomul.core.domain.user.QUserLoginEntity.userLoginEntity
import com.woomulwoomul.core.domain.user.UserLoginEntity
import org.springframework.stereotype.Repository

@Repository
class UserLoginRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : UserLoginCustomRepository {

    override fun findAll(pageOffsetRequest: PageOffsetRequest): PageData<UserLoginEntity> {
        val total = DatabaseUtils.count(queryFactory
            .select(userLoginEntity.user.id.countDistinct())
            .from(userLoginEntity)
            .innerJoin(userEntity)
            .on(userEntity.id.eq(userLoginEntity.user.id)
                .and(userEntity.status.eq(ACTIVE)))
            .fetchFirst())

        if (total == 0L) return PageData(emptyList(), total)

        val data = queryFactory
            .selectFrom(userLoginEntity)
            .innerJoin(userEntity)
            .on(userEntity.id.eq(userLoginEntity.user.id)
                .and(userEntity.status.eq(ACTIVE)))
            .fetchJoin()
            .where(userLoginEntity.id.eq(JPAExpressions
                .select(userLoginEntity.id.max())
                .from(userLoginEntity)
                .where(userLoginEntity.user.id.eq(userEntity.id))))
            .offset(pageOffsetRequest.from)
            .limit(pageOffsetRequest.size)
            .orderBy(userLoginEntity.user.id.desc())
            .fetch()

        return PageData(data, total)
    }
}