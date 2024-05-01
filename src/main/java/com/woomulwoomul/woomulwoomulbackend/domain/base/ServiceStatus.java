package com.woomulwoomul.woomulwoomulbackend.domain.base;

import lombok.Getter;

@Getter
public enum ServiceStatus {
    ACTIVE("활성화"),
    USER_DEL("사용자 삭제"),
    ADMIN_DEL("관리자 삭제"),
    ;

    private final String text;

    ServiceStatus(String text) {
        this.text = text;
    }
}
