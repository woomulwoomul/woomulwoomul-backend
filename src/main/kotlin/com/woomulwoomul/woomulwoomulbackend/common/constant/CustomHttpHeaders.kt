package com.woomulwoomul.woomulwoomulbackend.common.constant

import org.springframework.http.HttpHeaders

class CustomHttpHeaders : HttpHeaders() {

    companion object {
        const val REFRESH_TOKEN: String = "Refresh-Token"
    }
}