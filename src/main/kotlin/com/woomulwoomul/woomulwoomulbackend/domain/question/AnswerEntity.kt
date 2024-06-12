package com.woomulwoomul.woomulwoomulbackend.domain.question

import com.woomulwoomul.woomulwoomulbackend.domain.base.BasePermanentEntity
import jakarta.persistence.*

@Table(name = "answer")
@Entity
class AnswerEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    val id: Long? = null,

    @Column(nullable = false, length = 200)
    val context: String,
    @Column(nullable = false, length = 500)
    val imageUrl: String,
) : BasePermanentEntity()