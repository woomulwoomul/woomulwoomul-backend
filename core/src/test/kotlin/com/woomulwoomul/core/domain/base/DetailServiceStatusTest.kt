package com.woomulwoomul.core.domain.base

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

class DetailServiceStatusTest {

    @DisplayName("상세 상태 조회가 정상 작동한다")
    @Test
    fun givenValid_whenGetEntries_thenReturn() {
        // when
        val entries = DetailServiceStatus.entries

        // then
        assertThat(entries).containsExactly(
            DetailServiceStatus.COMPLETE,
            DetailServiceStatus.INCOMPLETE,
            DetailServiceStatus.USER_DEL,
            DetailServiceStatus.ADMIN_DEL
        )
    }

    @DisplayName("상세 상태 값 조회가 정상 작동한다")
    @Test
    fun givenValid_whenValues_thenReturn() {
        // when
        val values = DetailServiceStatus.values()

        // then
        assertThat(values).containsExactly(
            DetailServiceStatus.COMPLETE,
            DetailServiceStatus.INCOMPLETE,
            DetailServiceStatus.USER_DEL,
            DetailServiceStatus.ADMIN_DEL
        )
    }

    @ParameterizedTest(name = "[{index}] {0}을 상세 상태 타입으로 변환한다")
    @EnumSource(DetailServiceStatus::class)
    @DisplayName("상세 상테 타입 변환이 정상 작동한다")
    fun givenValid_whenValueOf_thenReturn(detailServiceStatus: DetailServiceStatus) {
        // when
        val result = DetailServiceStatus.valueOf(detailServiceStatus.name)

        // then
        assertThat(result).isEqualTo(detailServiceStatus)
    }
}