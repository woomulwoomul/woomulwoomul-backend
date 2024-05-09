package com.woomulwoomul.woomulwoomulbackend.domain.user.custom;

import com.woomulwoomul.woomulwoomulbackend.domain.user.UserRole;

import java.util.List;

public interface UserRoleCustomRepository {

    List<UserRole> findAllFetchUser(Long userId);
}
