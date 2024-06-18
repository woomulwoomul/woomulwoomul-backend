package com.woomulwoomul.woomulwoomulbackend.common.utils

import io.netty.util.internal.ThreadLocalRandom

class UserUtils {

    companion object {

        private val threadLocalRandom = ThreadLocalRandom.current()

        fun generateRandomUsername(email: String): String {
            return email.split("@")[0].plus(threadLocalRandom.nextInt(1, 1000))
        }
    }
}