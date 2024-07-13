package com.woomulwoomul.woomulwoomulbackend.common.utils

import io.netty.util.internal.ThreadLocalRandom
import java.security.Principal

class UserUtils {

    companion object {

        private val threadLocalRandom = ThreadLocalRandom.current()

        fun generateRandomNickname(nickname: String): String {
            println("generateRandomNickname")
            println("1|".plus(nickname.substring(0, 6)))
            println("2|".plus(nickname.substring(0, 6)).plus("_").plus(threadLocalRandom.nextInt(1, 1000)))

            return nickname.substring(0, 6).plus("_").plus(threadLocalRandom.nextInt(1, 1000))
        }

        fun getNicknameFromEmail(email: String): String {
            println("getNicknameFromEmail")
            println("1|".plus(email.split("@")[0]))
            println("2|".plus(email.split("@")[0].substring(0, 10)))
            return email.split("@")[0].substring(0, 10)
        }

        fun getUserId(principal: Principal): Long {
            return principal.name.toLong()
        }
    }
}