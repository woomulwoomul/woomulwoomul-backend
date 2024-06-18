package com.woomulwoomul.woomulwoomulbackend.common.exception

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class ByteSizeValidator : ConstraintValidator<ByteSize, String> {

    private var min: Int = 0
    private var max: Int = Int.MAX_VALUE

    override fun initialize(constraintAnnotation: ByteSize) {
        this.min = constraintAnnotation.min
        this.max = constraintAnnotation.max
    }

    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        if (value == null) return true

        val byteSize = value.sumOf {
            if (it.toString().toByteArray(Charsets.UTF_8).size > 1) 2 else 1 as Int
        }

        return byteSize in min..max
    }
}