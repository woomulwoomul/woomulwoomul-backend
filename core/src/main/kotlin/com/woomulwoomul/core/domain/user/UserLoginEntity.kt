package com.woomulwoomul.core.domain.user

import com.woomulwoomul.core.domain.base.BaseEntity
import jakarta.persistence.*

@Table(name = "user_login")
@Entity
class UserLoginEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_login_id", nullable = false)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserEntity,
) : BaseEntity() {
}