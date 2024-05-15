package com.woomulwoomul.woomulwoomulbackend.domain.user.custom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.woomulwoomul.woomulwoomulbackend.domain.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.woomulwoomul.woomulwoomulbackend.domain.user.QUser.user;
import static com.woomulwoomul.woomulwoomulbackend.domain.user.QUserRole.userRole;

@Repository
@RequiredArgsConstructor
public class UserRoleCustomRepositoryImpl implements UserRoleCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<UserRole> findAllFetchUser(Long userId) {
        return queryFactory
                .selectFrom(userRole)
                .innerJoin(userRole.user, user)
                .fetchJoin()
                .where(user.id.eq(userId))
                .fetch();
    }
}
