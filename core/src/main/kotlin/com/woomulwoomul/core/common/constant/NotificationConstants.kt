package com.woomulwoomul.core.common.constant

enum class NotificationConstants(
    private val message: String,
    private val link: String,
) {
    FOLLOW("{0}님과 친구가 되었어요.", "/users/{0}"),
    ANSWER("{0}님이 답변을 남겼어요.", "/users/{0}/answers/{1}"),
    ADMIN_UNANSWERED("회원님의 질문이 아직 답변을 못 받았어요. 다시 친구들에게 공유해보세요!", "/")
    ;

    fun toMessage(nickname: String? = null): String {
        return if (nickname.isNullOrBlank()) message
         else message.replace("{0}", nickname)
    }

    fun toLink(ids: List<Long> = emptyList()): String {
        if (ids.isEmpty()) return link

        val regex = "\\{\\d+}".toRegex()
        val stringBuilder = StringBuilder(link)

        regex.findAll(link).forEachIndexed { index, matchResult ->
            if (index < ids.size) {
                val placeholder = matchResult.value
                val start = stringBuilder.indexOf(placeholder)
                if (start != -1) {
                    stringBuilder.replace(start, start + placeholder.length, ids[index].toString())
                }
            }
        }

        return stringBuilder.toString()
    }
}