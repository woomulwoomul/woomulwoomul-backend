package com.woomulwoomul.core.domain.question

import com.woomulwoomul.core.common.constant.BackgroundColor
import com.woomulwoomul.core.common.converter.BackgroundColorConverter
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
    var text: String,

    @Convert(converter = BackgroundColorConverter::class)
    @Column(nullable = false, length = 6)
    var backgroundColor: BackgroundColor,

    var startDateTime: LocalDateTime? = null,
    var endDateTime: LocalDateTime? = null,
) : BasePermanentEntity() {

    /**
     * 질문 업데이트
     * @param text 내용
     * @param backgroundColor 배경 색상
     * @param startDateTime 시작일
     * @param endDateTime 종료일
     * @param status 상태
     */
    fun update(
        text: String,
        backgroundColor: BackgroundColor,
        startDateTime: LocalDateTime?,
        endDateTime: LocalDateTime?,
        status: ServiceStatus
    ) {
        this.text = text
        this.backgroundColor = backgroundColor
        this.startDateTime = startDateTime
        this.endDateTime = endDateTime
        this.status = status
    }

    /**
     * 질문 상태 업데이트
     * @param status 상태
     */
    fun updateStatus(status: ServiceStatus) {
        this.status = status
    }
}