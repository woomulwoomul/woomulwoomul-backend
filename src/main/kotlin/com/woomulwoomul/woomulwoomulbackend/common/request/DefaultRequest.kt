package com.woomulwoomul.woomulwoomulbackend.common.request

class PageRequest(
    val from: Long,
    val size: Long,
) {

    companion object {
        fun of(from: Long?, size: Long?): PageRequest {
            return PageRequest(from ?: Long.MAX_VALUE, size ?: 10)
        }
    }
}