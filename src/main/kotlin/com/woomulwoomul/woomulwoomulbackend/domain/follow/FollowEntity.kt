package com.woomulwoomul.woomulwoomulbackend.domain.follow

import com.woomulwoomul.woomulwoomulbackend.domain.base.BasePermanentEntity
import com.woomulwoomul.woomulwoomulbackend.domain.base.ServiceStatus
import com.woomulwoomul.woomulwoomulbackend.domain.base.ServiceStatus.*
import com.woomulwoomul.woomulwoomulbackend.domain.user.UserEntity
import jakarta.persistence.*

@Table(name = "follow")
@Entity
class FollowEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "follow_id")
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_user_id", nullable = false)
    val followerUser: UserEntity,
) : BasePermanentEntity() {

    /**
     * 언팔로우
     */
    fun unfollow() {
        status = USER_DEL
    }
}