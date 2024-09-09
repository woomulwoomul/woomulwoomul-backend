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
            val s = size?.takeIf { it >= 1 } ?: 10L
            return PageOffsetRequest(
                from?.takeIf { it > 0 }?.times(s) ?: 0L,
                size?.takeIf { it >= 1 } ?: 10L)
        }
    }
}
