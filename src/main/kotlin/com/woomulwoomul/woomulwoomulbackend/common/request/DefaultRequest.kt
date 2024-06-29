package com.woomulwoomul.woomulwoomulbackend.common.request

class PageRequest(
    val from: Long = 0L,
    val size: Long = 0L,
) {

    companion object {
        fun of(from: Long, size: Long): PageRequest {
            return PageRequest(from * size, size)
        }
    }
}