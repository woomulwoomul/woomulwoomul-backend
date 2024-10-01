package com.woomulwoomul.core.domain.user

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import kotlin.enums.EnumEntries

class GenderTest {

    @DisplayName("성별 타입 조회가 정상 작동한다")
    @Test
    fun givenValid_whenGetEntries_thenReturn() {
        // when
        val entries = Gender.entries

        // then
        assertThat(entries).containsExactly(Gender.NONE, Gender.FEMALE, Gender.MALE)
    }

    @DisplayName("성별 값 조회가 정상 작동한다")
    @Test
    fun givenValid_whenValues_thenReturn() {
        // when
        val values = Gender.values()

        // then
        assertThat(values).containsExactly(Gender.NONE, Gender.FEMALE, Gender.MALE)
    }

    @ParameterizedTest(name = "[{index}] {0}을 성별 타입으로 변환한다")
    @EnumSource(Gender::class)
    @DisplayName("성별 타입 변환이 정상 작동한다")
    fun givenValid_whenValueOf_thenReturn(gender: Gender) {
        // when
        val result = Gender.valueOf(gender.name)

        // then
        assertThat(result).isEqualTo(gender)
    }
}