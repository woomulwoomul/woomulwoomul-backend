package com.woomulwoomul.core.domain.question.custom

import com.querydsl.jpa.impl.JPAQueryFactory
import com.woomulwoomul.core.common.request.PageCursorRequest
import com.woomulwoomul.core.common.request.PageOffsetRequest
import com.woomulwoomul.core.common.response.PageData
import com.woomulwoomul.core.common.utils.DatabaseUtils
import com.woomulwoomul.core.domain.base.ServiceStatus.ACTIVE
import com.woomulwoomul.core.domain.question.CategoryEntity
import com.woomulwoomul.core.domain.question.QCategoryEntity.categoryEntity
import com.woomulwoomul.core.domain.user.QUserEntity.userEntity
import org.springframework.stereotype.Repository

@Repository
class CategoryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : CategoryCustomRepository {

    override fun findAll(pageCursorRequest: PageCursorRequest): PageData<CategoryEntity> {
        val total = DatabaseUtils.count(queryFactory
            .select(categoryEntity.id.count())
            .from(categoryEntity)
            .where(categoryEntity.status.eq(ACTIVE))
            .fetchFirst())

        if (total == 0L) return PageData(emptyList(), total)

        val data = queryFactory
            .selectFrom(categoryEntity)
            .where(
                categoryEntity.status.eq(ACTIVE),
                categoryEntity.id.loe(pageCursorRequest.from)
            )
            .orderBy(categoryEntity.id.desc())
            .limit(pageCursorRequest.size)
            .fetch()

        return PageData(data, total)
    }

    override fun findAll(pageOffsetRequest: PageOffsetRequest): PageData<CategoryEntity> {
        val total = DatabaseUtils.count(queryFactory
            .select(categoryEntity.id.count())
            .from(categoryEntity)
            .innerJoin(userEntity)
            .on(userEntity.id.eq(categoryEntity.admin.id)
                .and(userEntity.status.eq(ACTIVE)))
            .where(categoryEntity.status.eq(ACTIVE))
            .fetchFirst())

        if (total == 0L) return PageData(emptyList(), total)

        val data = queryFactory
            .selectFrom(categoryEntity)
            .innerJoin(userEntity)
            .on(userEntity.id.eq(categoryEntity.admin.id)
                .and(userEntity.status.eq(ACTIVE)))
            .fetchJoin()
            .where(categoryEntity.status.eq(ACTIVE))
            .offset(pageOffsetRequest.from)
            .limit(pageOffsetRequest.size)
            .orderBy(categoryEntity.id.desc())
            .fetch()

        return PageData(data, total)
    }

    override fun findByIds(ids: List<Long>): List<CategoryEntity> {
        return queryFactory
            .selectFrom(categoryEntity)
            .where(
                categoryEntity.status.eq(ACTIVE),
                categoryEntity.id.`in`(ids)
            ).fetch()
    }
}