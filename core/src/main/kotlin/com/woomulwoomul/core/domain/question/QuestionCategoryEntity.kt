package com.woomulwoomul.core.domain.question

import com.woomulwoomul.core.domain.base.BasePermanentEntity
import jakarta.persistence.*

@Table(name = "question_category")
@Entity
class QuestionCategoryEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_category_id")
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    val question: QuestionEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    val category: CategoryEntity,
) : BasePermanentEntity()