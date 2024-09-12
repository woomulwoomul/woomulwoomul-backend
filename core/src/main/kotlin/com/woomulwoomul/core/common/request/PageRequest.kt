package com.woomulwoomul.core.common.request

class PageCursorRequest(
    val from: Long,
    val size: Long,
) {

    companion object {
        fun of(from: Long?, size: Long?): PageCursorRequest {
            return PageCursorRequest(from ?: Long.MAX_VALUE, size ?: 10)
        }
    }
}

class PageOffsetRequest(
    val from: Long,
    val size: Long,
) {

    companion object {

        fun of(from: Long?, size: Long?): PageOffsetRequest {
            val validSize = size?.takeIf { it >= 1 } ?: 20L
            val validFrom = (from?.takeIf { it > 1 }?.minus(1) ?: 0) * validSize

            return PageOffsetRequest(validFrom, validSize)
        }
    }
}
