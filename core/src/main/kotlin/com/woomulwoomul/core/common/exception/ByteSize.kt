package com.woomulwoomul.core.common.exception

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(
    AnnotationTarget.FIELD,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.TYPEALIAS
)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [ByteSizeValidator::class])
annotation class ByteSize(
    val message: String = "",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
    val min: Int = 0,
    val max: Int = Int.MAX_VALUE,
) {

    @Target(
        AnnotationTarget.FIELD,
        AnnotationTarget.PROPERTY_GETTER,
        AnnotationTarget.VALUE_PARAMETER,
        AnnotationTarget.ANNOTATION_CLASS,
        AnnotationTarget.CONSTRUCTOR,
        AnnotationTarget.TYPEALIAS
    )
    @Retention(AnnotationRetention.RUNTIME)
    annotation class List(val value: Array<ByteSize>)
}
