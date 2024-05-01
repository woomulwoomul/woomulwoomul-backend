package com.woomulwoomul.woomulwoomulbackend.domain.user;

import lombok.Getter;

@Getter
public enum ProviderType {
    KAKAO("카카오"),
    ;

    private final String text;

    ProviderType(String text) {
        this.text = text;
    }
}
