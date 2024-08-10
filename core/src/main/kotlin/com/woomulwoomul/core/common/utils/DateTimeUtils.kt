package com.woomulwoomul.core.common.utils

import java.time.Duration
import java.time.LocalDateTime

class DateTimeUtils {

    companion object {

        /**
         * 시간 차이 계산
         * @param dateTime 대상 시간
         * @param now 현재 시간
         * @return 시간 차이
         */
        fun getDurationDifference(dateTime: LocalDateTime, now: LocalDateTime): String {
            val duration = Duration.between(dateTime, now)

            return when {
                duration.seconds < 60 -> "${duration.seconds}초"
                duration.toMinutes() < 60 -> "${duration.toMinutes()}분"
                duration.toHours() < 24 -> "${duration.toHours()}시간"
                else -> "${duration.toDays()}일"
            }
        }
    }
}