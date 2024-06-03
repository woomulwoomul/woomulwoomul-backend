package com.woomulwoomul.woomulwoomulbackend.config.auth

import com.woomulwoomul.woomulwoomulbackend.domain.user.ProviderType

class ProviderUserInfo(
    val providerType: ProviderType,
    val id: String,
    val email: String,
    val pictureUrl: String,
) {
}