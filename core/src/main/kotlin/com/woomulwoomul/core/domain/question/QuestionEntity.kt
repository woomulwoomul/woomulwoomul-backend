package com.woomulwoomul.core.domain.question

import com.woomulwoomul.core.domain.base.BasePermanentEntity
import com.woomulwoomul.core.domain.base.ServiceStatus
import com.woomulwoomul.core.domain.user.UserEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Table(name = "question")
@Entity
class QuestionEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserEntity,

    @Column(nullable = false, length = 60)
    val text: String,
    @Column(nullable = false, length = 6)
    val backgroundColor: String,

    val startDateTime: LocalDateTime? = null,
    val endDateTime: LocalDateTime? = null,
) : BasePermanentEntity() {

    /**
     * 질문 상태 업데이트
     * @param status 상태
     */
    fun updateStatus(status: ServiceStatus) {
        this.status = status
    }
}