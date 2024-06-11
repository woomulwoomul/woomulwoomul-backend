package com.woomulwoomul.woomulwoomulbackend.domain.user

import org.springframework.security.core.authority.SimpleGrantedAuthority

enum class Role(
    val text: String
) {
    USER("회원"),
    ADMIN("관리자"),
    MASTER("마스터");

    companion object {
        fun getSimpleGrantedAuthorities(userRoleEntities: List<UserRoleEntity>): List<SimpleGrantedAuthority>  {
            return userRoleEntities.stream()
                .map { SimpleGrantedAuthority(it.role.name) }
                .toList()
        }
    }
}