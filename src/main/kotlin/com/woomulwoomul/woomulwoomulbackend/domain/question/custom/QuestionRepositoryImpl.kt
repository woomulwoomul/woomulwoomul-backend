package com.woomulwoomul.woomulwoomulbackend.domain.question.custom

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class QuestionRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) {
}