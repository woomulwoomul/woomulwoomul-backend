package com.woomulwoomul.woomulwoomulbackend.api.service.user.resposne

data class UserLoginResponse(
    val userId: Long,
    val username: String,
    val email: String,
    val imageUrl: String,
) {

    constructor(attributes: Map<String, Any>): this(
        attributes["userId"] as Long,
        attributes["username"].toString(),
        attributes["email"].toString(),
        attributes["imageUrl"].toString()
    )
}