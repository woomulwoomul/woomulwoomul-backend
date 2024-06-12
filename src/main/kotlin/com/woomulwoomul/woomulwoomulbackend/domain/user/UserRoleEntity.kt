package com.woomulwoomul.woomulwoomulbackend.domain.user

import com.woomulwoomul.woomulwoomulbackend.domain.base.BasePermanentEntity
import jakarta.persistence.*

@Table(name = "user_role")
@Entity
class UserRoleEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_role_id", nullable = false)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserEntity,

    @Column(nullable = false, length = 6)
    @Enumerated(EnumType.STRING)
    val role: Role,
) : BasePermanentEntity() {
}