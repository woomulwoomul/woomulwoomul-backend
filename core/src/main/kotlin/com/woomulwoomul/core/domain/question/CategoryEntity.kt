package com.woomulwoomul.core.domain.question

import com.woomulwoomul.core.domain.base.BasePermanentEntity
import com.woomulwoomul.core.domain.user.UserEntity
import jakarta.persistence.*

@Table(name = "category")
@Entity
class CategoryEntity (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    val admin: UserEntity,

    @Column(nullable = false, length = 10)
    val name: String,
) : BasePermanentEntity()