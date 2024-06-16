package com.woomulwoomul.woomulwoomulbackend.domain.question.custom

import com.querydsl.jpa.impl.JPAQueryFactory
import com.woomulwoomul.woomulwoomulbackend.common.response.PageData
import com.woomulwoomul.woomulwoomulbackend.domain.base.ServiceStatus.ACTIVE
import com.woomulwoomul.woomulwoomulbackend.domain.question.CategoryEntity
import com.woomulwoomul.woomulwoomulbackend.domain.question.QCategoryEntity.categoryEntity
import org.springframework.stereotype.Repository

@Repository
class CategoryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : CategoryCustomRepository {

    override fun findAll(pageFrom: Long, pageSize: Long): PageData<CategoryEntity> {
        val total = queryFactory
            .select(categoryEntity.id.count())
            .from(categoryEntity)
            .where(categoryEntity.status.eq(ACTIVE))
            .fetchFirst() ?: 0L

        if (total == 0L) return PageData(listOf(), total)

        val data = queryFactory
            .selectFrom(categoryEntity)
            .where(categoryEntity.status.eq(ACTIVE))
            .orderBy(categoryEntity.id.asc())
            .offset(pageFrom)
            .limit(pageSize)
            .fetch()

        return PageData<CategoryEntity>(data, total)
    }
}