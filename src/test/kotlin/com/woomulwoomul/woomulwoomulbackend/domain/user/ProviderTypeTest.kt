package com.woomulwoomul.woomulwoomulbackend.domain.user

import com.woomulwoomul.woomulwoomulbackend.common.constant.ExceptionCode.PROVIDER_TYPE_INVALID
import com.woomulwoomul.woomulwoomulbackend.common.response.CustomException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

class ProviderTypeTest {

    @DisplayName("OAuth 제공자 타입 조회가 정상 작동한다")
    @Test
    fun givenValid_whenGetEntries_thenReturn() {
        // when
        val entries = ProviderType.entries

        // then
        assertThat(entries).containsExactly(ProviderType.KAKAO)
    }

    @DisplayName("OAuth 제공자 값 조회가 정상 작동한다")
    @Test
    fun givenValid_whenValues_thenReturn() {
        // when
        val values = ProviderType.values()

        // then
        assertThat(values).containsExactly(ProviderType.KAKAO)
    }

    @ParameterizedTest(name = "[{index}] {0}을 제공자 타입으로 변환한다")
    @EnumSource(ProviderType::class)
    @DisplayName("OAuth 제공자 타입 변환이 정상 작동한다")
    fun givenValid_whenValueOf_thenReturn(providerType: ProviderType) {
        // when
        val result = ProviderType.of(providerType.name)

        // then
        assertThat(result).isEqualTo(providerType)
    }

    @DisplayName("잘못된 OAuth 제공자로 타입 변환을 하면 예외가 발생한다")
    @Test
    fun givenInvalidType_whenOf_thenThrow() {
        // given
        val providerType = "invalid"

        // when & then
        assertThatThrownBy { ProviderType.of(providerType) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(PROVIDER_TYPE_INVALID)
    }
}