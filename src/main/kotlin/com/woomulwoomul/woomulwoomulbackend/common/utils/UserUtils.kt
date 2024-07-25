package com.woomulwoomul.woomulwoomulbackend.common.utils

import io.netty.util.internal.ThreadLocalRandom
import java.security.Principal

class UserUtils {

    companion object {

        private val threadLocalRandom = ThreadLocalRandom.current()

        /**
         * 랜덤 닉네임 생성
         * @param nickname 닉네임
         * @return 닉네임
         */
        fun generateRandomNickname(nickname: String): String {
            return nickname.take(6).plus("_").plus(threadLocalRandom.nextInt(1, 1000))
        }

        /**
         * 이메일 ID로 닉네임 추출
         * @param email 이메일
         * @return 닉네임
         */
        fun getNicknameFromEmail(email: String): String {
            return email.split("@")[0].take(10)
        }

        /**
         * 회원 ID 추출
         * @param principal 회원 데이터
         * @return 회원 ID
         */
        fun getUserId(principal: Principal): Long {
            return principal.name.toLong()
        }
    }
}