package com.woomulwoomul.woomulwoomulbackend.domain.user

import com.woomulwoomul.woomulwoomulbackend.domain.base.BasePermanentEntity
import jakarta.persistence.*

@Table(name = "users")
@Entity
class UserEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    val id: Long? = null,

    @Column(nullable = false, length = 30, unique = true)
    val username: String,
    @Column(nullable = false, length = 100, unique = true)
    val email: String,
    @Column(nullable = false, length = 500)
    val imageUrl: String,
) : BasePermanentEntity() {
}