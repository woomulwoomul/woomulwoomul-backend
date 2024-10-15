package com.woomulwoomul.core.domain.question

import com.woomulwoomul.core.domain.base.BaseDetailEntity
import com.woomulwoomul.core.domain.base.DetailServiceStatus.*
import com.woomulwoomul.core.domain.base.ServiceStatus
import com.woomulwoomul.core.domain.user.UserEntity
import jakarta.persistence.*

@Table(name = "question_answer")
@Entity
class QuestionAnswerEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_answer_id")
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    val receiver: UserEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    val sender: UserEntity?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    val question: QuestionEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_id")
    var answer: AnswerEntity? = null,
) : BaseDetailEntity(COMPLETE) {

    /**
     * 회원이 삭제
     */
    fun deleteByUser() {
        status = USER_DEL
        answer?.status = ServiceStatus.USER_DEL
    }

    fun deleteByAdmin() {
        status = ADMIN_DEL
        answer?.status = ServiceStatus.ADMIN_DEL
    }
}