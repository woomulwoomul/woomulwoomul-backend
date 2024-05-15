package com.woomulwoomul.woomulwoomulbackend.domain.user;

import com.woomulwoomul.woomulwoomulbackend.domain.user.custom.UserRoleCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, Long>, UserRoleCustomRepository {
}
