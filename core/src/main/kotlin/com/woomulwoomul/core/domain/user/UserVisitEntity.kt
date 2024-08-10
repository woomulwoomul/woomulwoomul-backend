package com.woomulwoomul.core.domain.user

import com.woomulwoomul.core.domain.base.BasePermanentEntity
import jakarta.persistence.*

@Table(name = "user_visit")
@Entity
class UserVisitEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_visit_id")
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visitor_user_id", nullable = false)
    val visitorUser: UserEntity,
) : BasePermanentEntity() {
}