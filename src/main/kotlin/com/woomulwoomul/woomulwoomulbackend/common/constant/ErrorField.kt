package com.woomulwoomul.woomulwoomulbackend.common.constant

class ErrorField {

    companion object {
        const val NOT_NULL_CONST = "NOTNULL"
        const val NOT_BLANK_CONST = "NOTBLANK"
        const val SIZE_CONST = "SIZE"
        const val BYTE_SIZE_CONST = "BYTESIZE"
        const val PATTERN_CONST = "PATTERN"
        const val EMAIL_CONST = "EMAIL"

        val code = mutableMapOf(
            Pair(NOT_NULL_CONST, "_FIELD_REQUIRED"),
            Pair(NOT_BLANK_CONST, "_FIELD_REQUIRED"),
            Pair(SIZE_CONST, "_SIZE_INVALID"),
            Pair(BYTE_SIZE_CONST, "_BYTE_SIZE_INVALID"),
            Pair(PATTERN_CONST, "_PATTERN_INVALID"),
            Pair(EMAIL_CONST, "_FORMAT_INVALID")
        )
    }
}