package com.woomulwoomul.woomulwoomulbackend.domain.question

import com.woomulwoomul.woomulwoomulbackend.domain.base.BaseDetailPermanentEntity
import com.woomulwoomul.woomulwoomulbackend.domain.base.DetailServiceStatus.INCOMPLETE
import com.woomulwoomul.woomulwoomulbackend.domain.user.UserEntity
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
    @JoinColumn(name = "sender_id", nullable = false)
    val sender: UserEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    val question: QuestionEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_id")
    val answer: AnswerEntity,
) : BaseDetailPermanentEntity(INCOMPLETE)