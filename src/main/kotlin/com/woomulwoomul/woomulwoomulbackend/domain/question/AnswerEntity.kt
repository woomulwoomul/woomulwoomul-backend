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

    @Column(length = 280)
    var text: String,
    @Column(length = 500)
    var imageUrl: String,
) : BasePermanentEntity() {

    /**
     * 답변 수정
     */
    fun update(text: String, imageUrl: String) {
        this.text = text
        this.imageUrl = imageUrl
    }
}