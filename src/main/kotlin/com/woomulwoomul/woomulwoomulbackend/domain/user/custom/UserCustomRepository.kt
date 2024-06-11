package com.woomulwoomul.woomulwoomulbackend.domain.user.custom

interface UserCustomRepository {

    fun exists(username: String): Boolean
}