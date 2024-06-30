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
    var nickname: String,
    @Column(nullable = false, length = 100, unique = true)
    val email: String,
    @Column(nullable = false, length = 500)
    var imageUrl: String,
    @Column(length = 30)
    var introduction: String? = null,
) : BasePermanentEntity() {

    /**
     * 회원 프로필 업데이트
     */
    fun updateProfile(nickname: String, imageUrl: String, introduction: String?) {
        this.nickname = nickname
        this.imageUrl = imageUrl
        this.introduction = introduction
    }
}