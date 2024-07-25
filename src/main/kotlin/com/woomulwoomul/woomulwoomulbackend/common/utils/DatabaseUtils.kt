package com.woomulwoomul.woomulwoomulbackend.common.utils

class DatabaseUtils {

    companion object {

        /**
         * 데이터 개수 계산
         * @param count 데이터 개수
         * @return 응답 개수
         */
        fun count(count: Long?): Long {
            return count?.coerceAtLeast(0L) ?: 0L
        }
    }
}