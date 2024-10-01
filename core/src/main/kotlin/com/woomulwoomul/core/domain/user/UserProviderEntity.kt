package com.woomulwoomul.core.domain.user

import com.woomulwoomul.core.domain.base.BasePermanentEntity
import jakarta.persistence.*

@Table(name = "user_provider")
@Entity
class UserProviderEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_provider_id")
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserEntity,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    val provider: ProviderType,

    @Column(nullable = false, length = 100)
    val providerId: String,
) : BasePermanentEntity()