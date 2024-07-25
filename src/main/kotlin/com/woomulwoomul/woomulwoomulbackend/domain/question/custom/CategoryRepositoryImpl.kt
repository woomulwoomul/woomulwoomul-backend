package com.woomulwoomul.woomulwoomulbackend.domain.question.custom

import com.querydsl.jpa.impl.JPAQueryFactory
import com.woomulwoomul.woomulwoomulbackend.common.request.PageRequest
import com.woomulwoomul.woomulwoomulbackend.common.response.PageData
import com.woomulwoomul.woomulwoomulbackend.common.utils.DatabaseUtils
import com.woomulwoomul.woomulwoomulbackend.domain.base.ServiceStatus.ACTIVE
import com.woomulwoomul.woomulwoomulbackend.domain.question.CategoryEntity
import com.woomulwoomul.woomulwoomulbackend.domain.question.QCategoryEntity.categoryEntity
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