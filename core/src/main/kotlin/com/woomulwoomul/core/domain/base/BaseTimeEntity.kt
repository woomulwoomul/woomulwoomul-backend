package com.woomulwoomul.core.domain.base

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseTimeEntity(

    @LastModifiedDate
    @Column(nullable = true)
    var updateDateTime: LocalDateTime? = null,

    createDateTime: LocalDateTime? = null
) : BaseEntity(createDateTime)