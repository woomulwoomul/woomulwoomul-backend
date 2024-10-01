package com.woomulwoomul.core.domain.question

import com.woomulwoomul.core.domain.base.BasePermanentEntity
import com.woomulwoomul.core.domain.base.ServiceStatus
import com.woomulwoomul.core.domain.base.ServiceStatus.ADMIN_DEL
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
    var name: String,
) : BasePermanentEntity() {

    /**
     * 카테고리명 업데이트
     * @param name 카티고리명
     */
    fun updateName(name: String) {
        this.name = name
    }

    /**
     * 카테고리 상태 업데이트
     * @param status 상태
     */
    fun updateStatus(status: ServiceStatus) {
        this.status = status
    }
}