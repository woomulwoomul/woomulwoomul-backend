package com.woomulwoomul.core.domain.user

import com.woomulwoomul.core.common.constant.ExceptionCode.ROLE_TYPE_INVALID
import com.woomulwoomul.core.common.response.CustomException
import org.springframework.security.core.authority.SimpleGrantedAuthority

enum class Role {
    USER,
    ADMIN,
    MASTER
    ;

    companion object {

        fun of (role: String): Role {
            return when(role.uppercase()) {
                USER.name -> USER
                ADMIN.name -> ADMIN
                MASTER.name -> MASTER
                else -> throw CustomException(ROLE_TYPE_INVALID)
            }
        }

        fun getSimpleGrantedAuthorities(userRoleEntities: List<UserRoleEntity>): List<SimpleGrantedAuthority>  {
            return userRoleEntities.stream()
                .map { SimpleGrantedAuthority(it.role.name) }
                .toList()
        }
    }
}