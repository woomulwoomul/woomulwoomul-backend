package com.woomulwoomul.woomulwoomulbackend.common.utils

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class DatabaseUtilsTest {

    @ParameterizedTest(name = "[{index}] {0} 데이터 개수는 {1}로 반환한다")
    @MethodSource("providerCount")
    @DisplayName("데이터 개수 계산이 정상 작동한다")
    fun givenValid_whenCount_thenReturn(count: Long?, expected: Long?) {
        // when
        val result = DatabaseUtils.count(count)

        // then
        assertThat(result).isEqualTo(expected)
    }

    companion object {
        @JvmStatic
        fun providerCount(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(Long.MAX_VALUE, Long.MAX_VALUE),
                Arguments.of(Long.MIN_VALUE, 0L),
                Arguments.of(0L, 0L),
                Arguments.of(null, 0L)
            )
        }
    }
}