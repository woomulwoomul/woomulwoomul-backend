package com.woomulwoomul.woomulwoomulbackend.common.constant

enum class ServiceConstants(
    val fields: List<String>
) {
    IMAGE_FILE_TYPES(listOf("png", "jpg", "jpeg")),
    UNAVAILABLE_NICKNAMES(listOf("관리자", "admin", "우물우물", "woomul")),
}