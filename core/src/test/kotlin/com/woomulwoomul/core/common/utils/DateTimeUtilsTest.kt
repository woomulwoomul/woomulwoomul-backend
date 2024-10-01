package com.woomulwoomul.core.common.utils

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDateTime
import java.util.stream.Stream

class DateTimeUtilsTest {

    @ParameterizedTest(name = "[{index}] 대상 시간 {0}, 현재 시간 {1}, {2}를 반환한다")
    @MethodSource("providerGetDurationDifference")
    @DisplayName("시간 차이 계산 정상 작동한다")
    fun givenValid_whenGetDurationDifference_thenReturn(dateTime: LocalDateTime, now: LocalDateTime, expected: String) {
        // when
        val result = DateTimeUtils.getDurationDifference(dateTime, now)

        // then
        assertThat(result).isEqualTo(expected)
    }

    companion object {
        @JvmStatic
        fun providerGetDurationDifference(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(LocalDateTime.of(2000, 1, 1, 0, 0, 0),
                    LocalDateTime.of(2000, 1, 1, 0, 0, 1),
                    "1초"),
                Arguments.of(LocalDateTime.of(2000, 1, 1, 0, 0, 0),
                    LocalDateTime.of(2000, 1, 1, 0, 0, 59),
                    "59초"),
                Arguments.of(LocalDateTime.of(2000, 1, 1, 0, 0, 0),
                    LocalDateTime.of(2000, 1, 1, 0, 1, 0),
                    "1분"),
                Arguments.of(LocalDateTime.of(2000, 1, 1, 0, 0, 0),
                    LocalDateTime.of(2000, 1, 1, 0, 59, 59),
                    "59분"),
                Arguments.of(LocalDateTime.of(2000, 1, 1, 0, 0, 0),
                    LocalDateTime.of(2000, 1, 1, 1, 0, 59),
                    "1시간"),
                Arguments.of(LocalDateTime.of(2000, 1, 1, 0, 0, 0),
                    LocalDateTime.of(2000, 1, 1, 23, 59, 59),
                    "23시간"),
                Arguments.of(LocalDateTime.of(2000, 1, 1, 0, 0, 0),
                    LocalDateTime.of(2000, 1, 2, 0, 0, 0, 0),
                    "1일"),
                Arguments.of(LocalDateTime.of(2000, 1, 1, 0, 0, 0),
                    LocalDateTime.of(2000, 2, 1, 23, 59, 59, 59),
                    "31일")
            )
        }
    }
}