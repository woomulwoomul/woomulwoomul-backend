package com.woomulwoomul.woomulwoomulbackend.domain.base

enum class NotificationServiceStatus(
    val text: String,
) {
    UNREAD("미확인"),
    READ("확인"),
    USER_DEL("사용자 삭제"),
    ADMIN_DEL("관리자 삭제"),
}