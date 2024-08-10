package com.woomulwoomul.core.domain.question.custom

import com.querydsl.jpa.impl.JPAQueryFactory
import com.woomulwoomul.core.common.request.PageRequest
import com.woomulwoomul.core.common.response.PageData
import com.woomulwoomul.core.common.utils.DatabaseUtils
import com.woomulwoomul.core.domain.base.ServiceStatus.ACTIVE
import com.woomulwoomul.core.domain.question.CategoryEntity
import com.woomulwoomul.core.domain.question.QCategoryEntity.categoryEntity
import org.springframework.stereotype.Repository

@Repository
class CategoryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : CategoryCustomRepository {

    override fun findAll(pageRequest: PageRequest): PageData<CategoryEntity> {
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
                categoryEntity.id.loe(pageRequest.from)
            )
            .orderBy(categoryEntity.id.desc())
            .limit(pageRequest.size)
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