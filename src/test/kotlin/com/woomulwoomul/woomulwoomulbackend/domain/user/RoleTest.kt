package com.woomulwoomul.woomulwoomulbackend.domain.user

import com.woomulwoomul.woomulwoomulbackend.common.constant.ExceptionCode.ROLE_TYPE_INVALID
import com.woomulwoomul.woomulwoomulbackend.common.response.CustomException
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

class RoleTest {

    @DisplayName("역할 조회가 정상 작동한다")
    @Test
    fun givenValid_whenGetEntries_thenReturn() {
        // when
        val entries = Role.entries

        // then
        assertThat(entries).containsExactly(Role.USER, Role.ADMIN, Role.MASTER)
    }

    @DisplayName("역할 값 조회가 정상 작동한다")
    @Test
    fun givenValid_whenValues_thenReturn() {
        // when
        val values = Role.values()

        // then
        assertThat(values).containsExactly(Role.USER, Role.ADMIN, Role.MASTER)
    }

    @ParameterizedTest(name = "[{index}] {0}을 역할로 변환한다")
    @EnumSource(Role::class)
    @DisplayName("역할로 변환이 정상 작동한다")
    fun givenValid_whenOf_thenReturn(role: Role) {
        // when
        val result = Role.of(role.name)

        // then
        assertThat(result).isEqualTo(role)
    }

    @DisplayName("잘못된 역할로 변환을 하면 예외가 발생한다")
    @Test
    fun givenInvalidType_whenOf_thenThrow() {
        // given
        val role = "invalid"

        // when & then
        Assertions.assertThatThrownBy { Role.of(role) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(ROLE_TYPE_INVALID)
    }
}