package com.woomulwoomul.woomulwoomulbackend.domain.base

import jakarta.persistence.Column
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.MappedSuperclass
import java.time.LocalDateTime

@MappedSuperclass
abstract class BaseDetailEntity(

    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    var status: DetailServiceStatus,

    createDateTime: LocalDateTime? = null,
    updateDateTime: LocalDateTime? = null,
) : BaseEntity(createDateTime, updateDateTime)