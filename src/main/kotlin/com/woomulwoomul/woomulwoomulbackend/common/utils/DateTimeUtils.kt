package com.woomulwoomul.woomulwoomulbackend.common.utils

import java.time.Duration
import java.time.LocalDateTime

class DateTimeUtils {

    companion object {

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