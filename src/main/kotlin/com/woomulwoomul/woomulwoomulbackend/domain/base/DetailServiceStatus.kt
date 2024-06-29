package com.woomulwoomul.woomulwoomulbackend.domain.base

enum class DetailServiceStatus(
    val text: String,
) {
    COMPLETE("완료"),
    INCOMPLETE("미완료"),
    USER_DEL("사용자 삭제"),
    ADMIN_DEL("관리자 삭제"),
}