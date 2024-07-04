package com.woomulwoomul.woomulwoomulbackend.common.constant

enum class NotificationConstants(
    private val message: String,
    private val link: String,
) {
    FOLLOW("{0}님과 친과가 되었어요.", "/users/{0}"),
    ANSWER("{0}님이 답변을 남겼어요.", "/users/{0}/answers/{1}"),
    ;

    fun toMessage(nickname: String): String {
        return message.replace("{0}", nickname)
    }

    fun toLink(ids: List<Long>): String {
        var resultLink = link
        ids.forEachIndexed { index, id ->
            resultLink = resultLink.replace("{$index}", id.toString())
        }
        return resultLink
    }
}