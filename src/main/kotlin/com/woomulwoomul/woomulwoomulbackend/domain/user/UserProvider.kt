package com.woomulwoomul.woomulwoomulbackend.domain.user

import com.woomulwoomul.woomulwoomulbackend.domain.base.BasePermanentEntity
import jakarta.persistence.*

@Entity
class UserProvider(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_provider_id")
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    val provider: ProviderType,

    @Column(nullable = false, length = 100)
    val providerId: String,
) : BasePermanentEntity()