package com.woomulwoomul.woomulwoomulbackend.common.utils

import arrow.core.computations.result
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.Mockito
import java.security.Principal
import java.util.stream.Stream

class UserUtilsTest {

    @ParameterizedTest(name = "[{index}] {0}로 랜덤 닉네임 생성을 하면 {1}를 반환한다")
    @MethodSource("providerGenerateRandomNickname")
    @DisplayName("랜덤 닉네임 생성이 정상 작동한다")
    fun givenValid_whenGenerateRandomNickname_thenReturn(nickname: String, expected: String) {
        // when
        val result = UserUtils.generateRandomNickname(nickname)

        // then
        assertThat(result).contains(expected)
    }

    @ParameterizedTest(name = "[{index}] {0}로 랜덤 닉네임 생성을 하면 {1}를 반환한다")
    @MethodSource("providerGetNicknameFromEmail")
    @DisplayName("랜덤 닉네임 생성이 정상 작동한다")
    fun givenValid_whenGetNicknameFromEmail_thenReturn(email: String, expected: String) {
        // when
        val result = UserUtils.getNicknameFromEmail(email)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @DisplayName("회원 ID 추출이 정상 작동한다")
    @Test
    fun givenValid_whenGetUserId_thenReturn() {
        // given
        val expected = 1L

        val principal = Mockito.mock(Principal::class.java)!!
        Mockito.`when`(principal.name).thenReturn(expected.toString())

        // when
        val result = UserUtils.getUserId(principal)

        // then
        assertThat(result).isEqualTo(expected)
    }

    companion object {
        @JvmStatic
        fun providerGenerateRandomNickname(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("", "_"),
                Arguments.of("a", "a_"),
                Arguments.of("aaaaaaa", "aaaaaa_"),
            )
        }

        @JvmStatic
        fun providerGetNicknameFromEmail(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("", ""),
                Arguments.of("a", "a"),
                Arguments.of("aaaaaaaaaaa@woomulwoomul.com", "aaaaaaaaaa"),
            )
        }
    }
}