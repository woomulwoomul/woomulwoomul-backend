package com.woomulwoomul.woomulwoomulbackend.common.utils

import io.netty.util.internal.ThreadLocalRandom
import java.security.Principal

class UserUtils {

    companion object {

        private val threadLocalRandom = ThreadLocalRandom.current()

        fun generateRandomNickname(email: String): String {
            return email.split("@")[0].plus(threadLocalRandom.nextInt(1, 1000))
        }

        fun getUserId(principal: Principal): Long {
            return principal.name.toLong()
        }
    }
}