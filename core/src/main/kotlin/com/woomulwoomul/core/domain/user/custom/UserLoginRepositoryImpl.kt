package com.woomulwoomul.core.domain.user.custom

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class UserLoginRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : UserLoginCustomRepository {
}