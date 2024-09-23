package com.woomulwoomul.core.common.utils

import com.woomulwoomul.core.common.constant.ExceptionCode
import com.woomulwoomul.core.common.constant.ExceptionCode.DATE_TIME_FORMAT_INVALID
import com.woomulwoomul.core.common.response.CustomException
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

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

        /**
         * LocalDateTime 변환
         * @param strDateTime String 날짜
         * @return LocalDateTime 날짜
         */
        fun toLocalDateTime(strDateTime: String): LocalDateTime {
            return try {
                LocalDateTime.parse(strDateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            } catch (e: DateTimeParseException) {
                throw CustomException(DATE_TIME_FORMAT_INVALID, e.cause)
            }
        }
    }
}